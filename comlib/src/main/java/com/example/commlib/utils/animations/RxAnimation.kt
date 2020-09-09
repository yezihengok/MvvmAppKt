package com.example.commlib.utils.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Build
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import com.blankj.ALog

/**
 * 属性动画工具类
 * @author  yzh 2019/10/30 17:37
 */
class RxAnimation {
    private  var animator: AnimatorSet ?=null
    
    // setAnimation(Bounce.In(textView))
    fun setAnimation(animationSet: AnimatorSet): RxAnimation {
        animator = animationSet
        //  animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animator?.interpolator = LinearInterpolator() //匀速
        return get
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun start() {
        if (animator != null&&animator!=null) {
            if (animator?.isPaused!!) {
                animator?.resume()
                ALog.w("resume----------")
            } else {
                animator?.start()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun pause() {
        if (animator != null) {
            animator?.pause()
        }
    }

    fun cancel(v: View) {
        if (animator != null) {
            animator?.cancel()
            reset(v)
        }
    }

    /**
     * 需要 执行动画 并控制隐藏 显示view时设置
     * @param v
     * @param type  0执行前显示view   1执行前显示view且执行完后隐藏view
     */
    fun visableOrGone(v: View, type: Int): RxAnimation {
        if (animator != null) {
            animator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    v.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (type == 1) {
                        v.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        } else {
            throw NullPointerException("请先设置animatorSet！")
        }
        return get
    }

    fun setDuration(duration: Long): RxAnimation {
        if (animator != null) {
            animator?.duration = duration
        }
        return get
    }

    companion object {
        @JvmStatic
        val get: RxAnimation by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RxAnimation() }
        
        /**
         * 恢复view 默认状态
         *
         * @param target
         */
        fun reset(target: View) {
            target.alpha = 1f
            target.scaleX = 1f
            target.scaleY = 1f
            target.translationX = 0f
            target.translationY = 0f
            target.rotation = 0f
            target.rotationY = 0f
            target.rotationX = 0f
        }
    }
}