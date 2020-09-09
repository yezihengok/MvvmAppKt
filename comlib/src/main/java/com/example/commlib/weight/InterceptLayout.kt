package com.example.commlib.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/**
 * 拦截子View不响应事件的FrameLayout
 * Created by yzh on 2020/6/6 9:57.
 */
class InterceptLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0): FrameLayout(context, attrs,defStyleAttr) {
    /**
     * 是否拦截子View
     */
    private var intercept: Boolean = false
    fun isIntercept(): Boolean {
        return intercept
    }

    fun setIntercept(intercept: Boolean) {
        this.intercept = intercept
    }

//    constructor(context: Context) : super(context) {}
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    public override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (intercept) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }
}