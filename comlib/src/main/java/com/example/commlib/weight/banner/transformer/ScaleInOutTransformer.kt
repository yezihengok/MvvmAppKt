package com.example.commlib.weight.banner.transformer

import android.view.View

class ScaleInOutTransformer : ABaseTransformer() {
    override fun onTransform(view: View, position: Float) {
        view.setPivotX(if (position < 0) 0f else view.width.toFloat())
        view.pivotY = view.height / 2f
        val scale = if (position < 0) 1f + position else 1f - position
        view.scaleX = scale
        view.scaleY = scale
    }
}