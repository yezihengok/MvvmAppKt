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

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.TargetApi
import android.os.Build
import android.text.TextPaint
import android.text.style.SuperscriptSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import java.lang.ref.WeakReference
import kotlin.math.abs

/*package*/
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
internal class JumpingBeansSpan constructor(textView: TextView?,
                                            private val loopDuration: Int, position: Int, waveCharOffset: Int,
                                            private val animatedRange: Float
) : SuperscriptSpan(), AnimatorUpdateListener {
    private val textView: WeakReference<TextView?> = WeakReference(textView)
    private val delay: Int = waveCharOffset * position
    private var shift: Int = 0
    lateinit var jumpAnimator: ValueAnimator
    public override fun updateMeasureState(tp: TextPaint) {
        initIfNecessary(tp.ascent())
        tp.baselineShift = shift
    }

    public override fun updateDrawState(tp: TextPaint) {
        initIfNecessary(tp.ascent())
        tp.baselineShift = shift
    }

    private fun initIfNecessary(ascent: Float) {
        if (jumpAnimator != null) {
            return
        }
        shift = 0
        val maxShift: Int = ascent.toInt() / 2
        jumpAnimator = ValueAnimator.ofInt(0, maxShift)
        jumpAnimator.setDuration(loopDuration.toLong()).startDelay = delay.toLong()
        jumpAnimator.interpolator = JumpInterpolator(animatedRange)
        jumpAnimator.repeatCount = ValueAnimator.INFINITE
        jumpAnimator.repeatMode = ValueAnimator.RESTART
        jumpAnimator.addUpdateListener(this)
        jumpAnimator.start()
    }

    public override fun onAnimationUpdate(animation: ValueAnimator) {
        // No need for synchronization as this always run on main thread anyway
        val v: TextView? = textView.get()
        if (v != null) {
            updateAnimationFor(animation, v)
        } else {
            cleanupAndComplainAboutUserBeingAFool()
        }
    }

    private fun updateAnimationFor(animation: ValueAnimator, v: TextView) {
        if (isAttachedToHierarchy(v)) {
            shift = animation.animatedValue as Int
            v.invalidate()
        }
    }

    private fun cleanupAndComplainAboutUserBeingAFool() {
        // The textview has been destroyed and teardown() hasn't been called
        teardown()
        Log.w("JumpingBeans", "!!! Remember to call JumpingBeans.stopJumping() when appropriate !!!")
    }

    /*package*/
    fun teardown() {
        if (jumpAnimator != null) {
            jumpAnimator.cancel()
            jumpAnimator.removeAllListeners()
        }
        if (textView.get() != null) {
            textView.clear()
        }
    }

    /**
     * A tweaked [android.view.animation.AccelerateDecelerateInterpolator]
     * that covers the full range in a fraction of its input range, and holds on
     * the final value on the rest of the input range. By default, this fraction
     * is 65% of the full range.
     *
     * @see net.frakbot.jumpingbeans.JumpingBeans.DEFAULT_ANIMATION_DUTY_CYCLE
     */
    private class JumpInterpolator constructor(animatedRange: Float) : TimeInterpolator {
        private val animRange: Float = Math.abs(animatedRange)
        public override fun getInterpolation(input: Float): Float {
            // We want to map the [0, PI] sine range onto [0, animRange]
            val radians: Double = (input / animRange) * Math.PI
            val interpolatedValue: Double = Math.max(0.0, Math.sin(radians))
            return interpolatedValue.toFloat()
        }

    }

    companion object {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private fun isAttachedToHierarchy(v: View): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return v.isAttachedToWindow
            }
            return v.parent != null // Best-effort fallback
        }
    }

}