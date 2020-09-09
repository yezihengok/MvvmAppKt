package com.example.commlib.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * 禁止滑动的viewpager
 * Created by yzh on 2016/2/25.
 */
class NoScrollViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null): ViewPager(context, attrs) {
    private var noScroll: Boolean = true


    fun setNoScroll(noScroll: Boolean) {
        this.noScroll = noScroll
    }

    override fun onTouchEvent(arg0: MotionEvent): Boolean {
        /* return false;//super.onTouchEvent(arg0); */
        //触摸没有反应就可以了
        return if (noScroll) false else super.onTouchEvent(arg0)
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return if (noScroll) false else super.onInterceptTouchEvent(arg0)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item)
    }
}