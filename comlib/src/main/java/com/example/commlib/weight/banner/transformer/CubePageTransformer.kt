package com.example.commlib.weight.banner.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager.PageTransformer

/**
 * 创建时间:15/6/19 17:39
 * 描述:  //立方体
 */
class CubePageTransformer : PageTransformer {
    private var mMaxRotation = 90.0f

    constructor() {}
    constructor(maxRotation: Float) {
        setMaxRotation(maxRotation)
    }

    override fun transformPage(view: View, position: Float) {
        if (position < -1.0f) {
            // [-Infinity,-1)
            // This page is way off-screen to the left.
            handleInvisiblePage(view, position)
        } else if (position <= 0.0f) {
            // [-1,0]
            // Use the default slide transition when moving to the left page
            handleLeftPage(view, position)
        } else if (position <= 1.0f) {
            // (0,1]
            handleRightPage(view, position)
        } else {
            // (1,+Infinity]
            // This page is way off-screen to the right.
            handleInvisiblePage(view, position)
        }
    }

    fun handleInvisiblePage(view: View, position: Float) {
        view.pivotX = view.measuredWidth.toFloat()
        view.pivotY = view.measuredHeight * 0.5f
        view.rotationY = 0f
    }

    fun handleLeftPage(view: View, position: Float) {
        view.pivotX = view.measuredWidth.toFloat()
        view.pivotY = view.measuredHeight * 0.5f
        view.rotationY = mMaxRotation * position
    }

    fun handleRightPage(view: View, position: Float) {
        view.pivotX = 0f
        view.pivotY = view.measuredHeight * 0.5f
        view.rotationY = mMaxRotation * position
    }

    fun setMaxRotation(maxRotation: Float) {
        if (maxRotation >= 0.0f && maxRotation <= 90.0f) {
            mMaxRotation = maxRotation
        }
    }
}