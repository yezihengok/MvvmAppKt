package com.example.commlib.webview.config

import android.R
import android.content.Context
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Created by yzh on 2020/8/25 11:38.
 */
class FullscreenHolder constructor(context: Context):  FrameLayout(context) {
    public override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    init {
        setBackgroundColor(context.resources.getColor(R.color.black))
    }
}