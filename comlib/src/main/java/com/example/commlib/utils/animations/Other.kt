package com.example.commlib.utils.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View

/**
 * anthor yzh
 * time 2019/12/5 14:37
 */
object Other {
    fun pulseAnimator(view: View?, vararg repeatCount: Int): AnimatorSet {
        val animatorSet = AnimatorSet()
        val object1 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f)
        val object2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f)
        if (repeatCount.isNotEmpty()) {
            object1.repeatCount = repeatCount[0]
            object2.repeatCount = repeatCount[0]
        }
        animatorSet.playTogether(object1, object2)
        return animatorSet
    }

    fun Rotation(view: View?): AnimatorSet {
        val animatorSet = AnimatorSet()
        val objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        //objectAnimator.setDuration(2000);
        objectAnimator.repeatCount = ValueAnimator.INFINITE
        animatorSet.play(objectAnimator)
        return animatorSet
    }
}