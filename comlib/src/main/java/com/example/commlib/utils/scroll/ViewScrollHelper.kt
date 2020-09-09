package com.example.commlib.utils.scroll

import android.view.View

/**
 * 视图滑动辅助器
 * Created by yzh on 2018/12/18 10:13.
 */
abstract class ViewScrollHelper : ScrollHelper {
    protected var view: View
        private set

    constructor(view: View, gestureHelper: GestureHelper?) : super(gestureHelper) {
        this.view = view
    }

    constructor(view: View) : super(view.context) {
        this.view = view
    }

    override val viewScrollX: Int
        get() = view.scrollX
    override val viewScrollY: Int
        get() = view.scrollY

    override fun viewScrollTo(x: Int, y: Int) {
        view.scrollTo(x, y)
    }
}