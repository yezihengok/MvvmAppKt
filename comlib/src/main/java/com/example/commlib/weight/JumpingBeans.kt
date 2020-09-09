/*
 * Copyright 2014 Frakbot (Sebastiano Poggi and Francesco Pontillo)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.example.commlib.weight

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.widget.TextView
import java.lang.ref.WeakReference

/**
 * Provides "jumping beans" functionality for a TextView.
 *
 *
 * Remember to call the [.stopJumping] method once you're done
 * using the JumpingBeans (that is, when you detach the TextView from
 * the view tree, you hide it, or the parent Activity/Fragment goes in
 * the paused status). This will allow to release the animations and
 * free up memory and CPU that would be otherwise wasted.
 *
 *
 * Please note that you:
 *
 *  * **Must not** try to change a jumping beans text in a textview before calling
 * [.stopJumping] as to avoid unnecessary invalidation calls;
 * the JumpingBeans class cannot know when this happens and will keep
 * animating the textview (well, try to, anyway), wasting resources
 *  * **Must not** try to use a jumping beans text in another view; it will not
 * animate. Just create another jumping beans animation for each new
 * view
 *  * **Must not** use more than one JumpingBeans instance on a single TextView, as
 * the first cleanup operation called on any of these JumpingBeans will also cleanup
 * all other JumpingBeans' stuff. This is most likely not what you want to happen in
 * some cases.
 *  * **Should not** use JumpingBeans on large chunks of text. Ideally this should
 * be done on small views with just a few words. We've strived to make it as inexpensive
 * as possible to use JumpingBeans but invalidating and possibly relayouting a large
 * TextView can be pretty expensive.
 *
 */
class JumpingBeans private constructor(private val jumpingBeans: Array<JumpingBeansSpan?>, textView: TextView?) {
    private val textView: WeakReference<TextView?> = WeakReference(textView)

    /**
     * Stops the jumping animation and frees up the animations.
     */
    fun stopJumping() {
        for (bean: JumpingBeansSpan? in jumpingBeans) {
            bean?.teardown()
        }
        cleanupSpansFrom(textView.get())
    }

    /**
     * Builder class for [net.frakbot.jumpingbeans.JumpingBeans] objects.
     *
     *
     * Provides a way to set the fields of a [com.example.yzh.view.JumpingBeans] and generate
     * the desired jumping beans effect. With this builder you can easily append
     * a Hangouts-style trio of jumping suspension points to any TextView, or
     * apply the effect to any other subset of a TextView's text.
     *
     *
     *
     * Example:
     *
     *
     * <pre class="prettyprint">
     * JumpingBeans jumpingBeans = JumpingBeans.with(myTextView)
     * .appendJumpingDots()
     * .setLoopDuration(1500)
     * .build();
    </pre> *
     */
    class Builder /*package*/ internal constructor(private val textView: TextView?) {
        private var startPos: Int = 0
        private var endPos: Int = 0
        private var animRange: Float = DEFAULT_ANIMATION_DUTY_CYCLE
        private var loopDuration: Int = DEFAULT_LOOP_DURATION
        private var waveCharDelay: Int = -1
        private var text: CharSequence? = null
        private var wave: Boolean = false

        /**
         * Appends three jumping dots to the end of a TextView text.
         *
         *
         * This implies that the animation will by default be a wave.
         *
         *
         * If the TextView has no text, the resulting TextView text will
         * consist of the three dots only.
         *
         *
         * The TextView text is cached to the current value at
         * this time and set again in the [.build] method, so any
         * change to the TextView text done in the meantime will be lost.
         * This means that **you should do all changes to the TextView text
         * *before* you begin using this builder.**
         *
         *
         * Call the [.build] method once you're done to get the
         * resulting [net.frakbot.jumpingbeans.JumpingBeans].
         *
         * @see .setIsWave
         */
        fun appendJumpingDots(): Builder {
            val text: CharSequence = appendThreeDotsEllipsisTo(textView)
            this.text = text
            wave = true
            startPos = text.length - THREE_DOTS_ELLIPSIS_LENGTH
            endPos = text.length
            return this
        }

        /**
         * Appends three jumping dots to the end of a TextView text.
         *
         *
         * This implies that the animation will by default be a wave.
         *
         *
         * If the TextView has no text, the resulting TextView text will
         * consist of the three dots only.
         *
         *
         * The TextView text is cached to the current value at
         * this time and set again in the [.build] method, so any
         * change to the TextView text done in the meantime will be lost.
         * This means that **you should do all changes to the TextView text
         * *before* you begin using this builder.**
         *
         *
         * Call the [.build] method once you're done to get the
         * resulting [net.frakbot.jumpingbeans.JumpingBeans].
         *
         * @param startPos The position of the first character to animate
         * @param endPos   The position after the one the animated range ends at
         * (just like in [String.substring])
         * @see .setIsWave
         */
        fun makeTextJump(startPos: Int, endPos: Int): Builder {
            val text: CharSequence = textView!!.text
            ensureTextCanJump(startPos, endPos, text)
            this.text = text
            wave = true
            this.startPos = startPos
            this.endPos = endPos
            return this
        }

        /**
         * Sets the fraction of the animation loop time spent actually animating.
         * The rest of the time will be spent "resting".
         * The default value is
         * [net.frakbot.jumpingbeans.JumpingBeans.DEFAULT_ANIMATION_DUTY_CYCLE].
         *
         * @param animatedRange The fraction of the animation loop time spent
         * actually animating the characters
         */
        fun setAnimatedDutyCycle(animatedRange: Float): Builder {
            if (animatedRange <= 0f || animatedRange > 1f) {
                throw IllegalArgumentException("The animated range must be in the (0, 1] range")
            }
            animRange = animatedRange
            return this
        }

        /**
         * Sets the jumping loop duration. The default value is
         * [net.frakbot.jumpingbeans.JumpingBeans.DEFAULT_LOOP_DURATION].
         *
         * @param loopDuration The jumping animation loop duration, in milliseconds
         */
        fun setLoopDuration(loopDuration: Int): Builder {
            if (loopDuration < 1) {
                throw IllegalArgumentException("The loop duration must be bigger than zero")
            }
            this.loopDuration = loopDuration
            return this
        }

        /**
         * Sets the delay for starting the animation of every single dot over the
         * start of the previous one, in milliseconds. The default value is
         * the loop length divided by three times the number of character animated
         * by this instance of JumpingBeans.
         *
         *
         * Only has a meaning when the animation is a wave.
         *
         * @param waveCharOffset The start delay for the animation of every single
         * character over the previous one, in milliseconds
         * @see .setIsWave
         */
        fun setWavePerCharDelay(waveCharOffset: Int): Builder {
            if (waveCharOffset < 0) {
                throw IllegalArgumentException("The wave char offset must be non-negative")
            }
            waveCharDelay = waveCharOffset
            return this
        }

        /**
         * Sets a flag that determines if the characters will jump in a wave
         * (i.e., with a delay between each other) or all at the same
         * time.
         *
         * @param wave If true, the animation is going to be a wave; if
         * false, all characters will jump ay the same time
         * @see .setWavePerCharDelay
         */
        fun setIsWave(wave: Boolean): Builder {
            this.wave = wave
            return this
        }

        /**
         * Combine all of the options that have been set and return a new
         * [net.frakbot.jumpingbeans.JumpingBeans] instance.
         *
         *
         * Remember to call the [.stopJumping] method once you're done
         * using the JumpingBeans (that is, when you detach the TextView from
         * the view tree, you hide it, or the parent Activity/Fragment goes in
         * the paused status). This will allow to release the animations and
         * free up memory and CPU that would be otherwise wasted.
         */
        fun build(): JumpingBeans {
            val sbb: SpannableStringBuilder = SpannableStringBuilder(text)
            val spans: Array<JumpingBeansSpan?>
            if (wave) {
                spans = getJumpingBeansSpans(sbb)
            } else {
                spans = buildSingleSpan(sbb)
            }
            textView?.text = sbb
            return JumpingBeans(spans, textView)
        }

        private fun getJumpingBeansSpans(sbb: SpannableStringBuilder): Array<JumpingBeansSpan?> {
            if (waveCharDelay == -1) {
                waveCharDelay = loopDuration / (3 * (endPos - startPos))
            }
            val spans: Array<JumpingBeansSpan?> = arrayOfNulls(endPos - startPos)
            for (pos in startPos until endPos) {
                val jumpingBean: JumpingBeansSpan = JumpingBeansSpan(textView, loopDuration, pos - startPos, waveCharDelay, animRange)
                sbb.setSpan(jumpingBean, pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                //spans.get(pos - startPos) = jumpingBean
                spans[pos - startPos]= jumpingBean
            }
            return spans
        }

        private fun buildSingleSpan(sbb: SpannableStringBuilder): Array<JumpingBeansSpan?> {
            val spans: Array<JumpingBeansSpan?> =
                arrayOf(JumpingBeansSpan(textView, loopDuration, 0, 0, animRange))
            sbb.setSpan(spans[0], startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spans
        }

    }

    companion object {
        /**
         * The default fraction of the whole animation time spent actually animating.
         * The rest of the range will be spent in "resting" state.
         * This the "duty cycle" of the jumping animation.
         */
        val DEFAULT_ANIMATION_DUTY_CYCLE: Float = 0.65f

        /**
         * The default duration of a whole jumping animation loop, in milliseconds.
         */
        val DEFAULT_LOOP_DURATION: Int = 1300 // ms
        val ELLIPSIS_GLYPH: String = "…"
        val THREE_DOTS_ELLIPSIS: String = "..."
        val THREE_DOTS_ELLIPSIS_LENGTH: Int = 3

        /**
         * Create an instance of the [net.frakbot.jumpingbeans.JumpingBeans.Builder]
         * applied to the provided `TextView`.
         *
         * @param textView The TextView to apply the JumpingBeans to
         * @return the [net.frakbot.jumpingbeans.JumpingBeans.Builder]
         */
        fun with(textView: TextView?): Builder {
            return Builder(textView)
        }

        private fun cleanupSpansFrom(tv: TextView?) {
            if (tv != null) {
                val text: CharSequence = tv.getText()
                if (text is Spanned) {
                    val cleanText: CharSequence = removeJumpingBeansSpansFrom(text)
                    tv.setText(cleanText)
                }
            }
        }

        private fun removeJumpingBeansSpansFrom(text: Spanned): CharSequence {
            val sbb: SpannableStringBuilder = SpannableStringBuilder(text.toString())
            val spans: Array<Any> = text.getSpans(0, text.length, Any::class.java)
            for (span: Any in spans) {
                if (!(span is JumpingBeansSpan)) {
                    sbb.setSpan(span, text.getSpanStart(span),
                            text.getSpanEnd(span), text.getSpanFlags(span))
                }
            }
            return sbb
        }

        private fun appendThreeDotsEllipsisTo(textView: TextView?): CharSequence {
            var text: CharSequence = getTextSafe(textView)
            if (text.isNotEmpty() && endsWithEllipsisGlyph(text)) {
                text = text.subSequence(0, text.length - 1)
            }
            if (!endsWithThreeEllipsisDots(text)) {
                text = SpannableStringBuilder(text).append(THREE_DOTS_ELLIPSIS) // Preserve spans in original text
            }
            return text
        }

        private fun getTextSafe(textView: TextView?): CharSequence {
            return textView?.text ?:""
        }

        private fun endsWithEllipsisGlyph(text: CharSequence): Boolean {
            return TextUtils.equals(text.subSequence(text.length - 1, text.length), ELLIPSIS_GLYPH)
        }

        private fun endsWithThreeEllipsisDots(text: CharSequence): Boolean {
            if (text.length < THREE_DOTS_ELLIPSIS_LENGTH) {
                // TODO we should try to normalize "invalid" ellipsis (e.g., ".." or "....")
                return false
            }
            return TextUtils.equals(text.subSequence(text.length - THREE_DOTS_ELLIPSIS_LENGTH, text.length), THREE_DOTS_ELLIPSIS)
        }

        private fun ensureTextCanJump(startPos: Int, endPos: Int, text: CharSequence?): CharSequence {
            if (text == null) {
                throw NullPointerException("The textView text must not be null")
            }
            if (endPos < startPos) {
                throw IllegalArgumentException("The start position must be smaller than the end position")
            }
            if (startPos < 0) {
                throw IndexOutOfBoundsException("The start position must be non-negative")
            }
            if (endPos > text.length) {
                throw IndexOutOfBoundsException("The end position must be smaller than the text length")
            }
            return text
        }
    }

}