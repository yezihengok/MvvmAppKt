package com.example.commlib.utils.scroll

import android.content.Context
import android.util.TypedValue
import android.view.MotionEvent

/**
 * 手势辅助器
 */
class GestureHelper(pointSize: Float, longClickTime: Int, xyScale: Float) {
    private val pointSize // 点的大小
            : Float
    private val longClickTime // 长按判定时间
            : Int
    private val xyScale: Float

    /**
     * 获取手势
     *
     * @return 手势
     */
    var gesture = GESTURE_NONE // 手势
        private set
    private var downTime: Long = 0
    private var downX = 0f
    private var downY = 0f
    private var preX = 0f
    private var preY = 0f

    /**
     * 触发触摸滑动事件
     *
     * @param event 事件
     */
    fun onTouchEvent(event: MotionEvent) {
//        System.out.println("onTouchEvent:action=" + event.getAction());
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown(event)
            MotionEvent.ACTION_MOVE -> touchMove(event)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> touchFinish(event)
        }
        //        System.out.println("onTouchEvent:" + gesture);
    }

    /**
     * 判定是否为水平滑动手势
     *
     * @return true，水平滑动手势
     */
    val isHorizontalGesture: Boolean
        get() = gesture == GESTURE_LEFT || gesture == GESTURE_RIGHT

    /**
     * 判定是否为垂直滑动手势
     *
     * @return true，垂直滑动手势
     */
    val isVerticalGesture: Boolean
        get() = gesture == GESTURE_UP || gesture == GESTURE_DOWN

    private fun touchDown(event: MotionEvent) {
        downTime = System.currentTimeMillis()
        preX = event.rawX
        downX = preX
        preY = event.rawY
        downY = preY
        gesture = GESTURE_PRESSED
    }

    private fun touchMove(event: MotionEvent) {
        val rangeX = event.rawX - downX
        val rangeY = event.rawY - downY
        //        System.out.println(String.format("touchMove:rangeX=%f,rangeY=%f,pointSize=%f",
//                rangeX, rangeY, pointSize));
        if (gesture == GESTURE_NONE || gesture == GESTURE_PRESSED) { // 未确定手势或正在长按
            gesture =
                if (Math.abs(rangeX) > pointSize || Math.abs(rangeY) > pointSize) {
                    // 超出点的范围，不算点击、按住手势，应该是滑动手势
                    val ox = event.rawX - preX
                    val oy = event.rawY - preY
                    if (Math.abs(ox) > xyScale * Math.abs(oy)) {
                        // 水平方向滑动
                        if (ox < 0) {
                            GESTURE_LEFT
                        } else {
                            GESTURE_RIGHT
                        }
                    } else {
                        // 垂直方向滑动
                        if (oy < 0) {
                            GESTURE_UP
                        } else {
                            GESTURE_DOWN
                        }
                    }
                } else {
                    GESTURE_PRESSED // 按住手势
                }
        }
        if (gesture == GESTURE_PRESSED) { // 按住中
            if (System.currentTimeMillis() - downTime >= longClickTime) { // 按住超过长按时间，算长按时间
                gesture = GESTURE_LONG_CLICK
            }
        }
        preX = event.rawX
        preY = event.rawY
    }

    private fun touchFinish(event: MotionEvent) {
        if (gesture == GESTURE_PRESSED) { // 按住到释放，应该算点击手势
            gesture =
                if (System.currentTimeMillis() - downTime >= longClickTime) { // 按住超过长按时间，算长按时间
                    GESTURE_LONG_CLICK
                } else {
                    GESTURE_CLICK
                }
        }
    }

    companion object {
        /**
         * 无手势，还不能确定手势
         */
        const val GESTURE_NONE = 0

        /**
         * 手势：按住
         */
        const val GESTURE_PRESSED = 1

        /**
         * 手势：点击
         */
        const val GESTURE_CLICK = 2

        /**
         * 手势：长按
         */
        const val GESTURE_LONG_CLICK = 3

        /**
         * 手势：左滑
         */
        const val GESTURE_LEFT = 4

        /**
         * 手势：上滑
         */
        const val GESTURE_UP = 5

        /**
         * 手势：右滑
         */
        const val GESTURE_RIGHT = 6

        /**
         * 手势：下滑
         */
        const val GESTURE_DOWN = 7

        /**
         * 默认的点大小，单位：dip
         */
        const val DEFAULT_FONT_SIZE_DP = 5f

        /**
         * 默认的长按时间
         */
        const val DEFAULT_LONG_CLICK_TIME = 800

        /**
         * 创建默认的手势辅助器
         *
         * @param context 上下文对象
         * @return 手势器
         */
        fun createDefault(context: Context): GestureHelper {
            val pointSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_FONT_SIZE_DP, context.resources.displayMetrics
            )
            return GestureHelper(pointSize, DEFAULT_LONG_CLICK_TIME, 1f)
        }
    }

    /**
     * 创建一个手势帮助器
     *
     * @param pointSize     点的大小，超出此大小的滑动手势会被判定为非点击手势
     * @param longClickTime 长按点击时间，超过或等于此时间的按住手势算长按点击事件
     * @param xyScale       X轴与Y轴比例，影响方向手势的判定，默认是1；
     * 越小，手势判定越偏重于水平方向；
     * 越大，手势判定偏重于垂直方向；
     * 1，不偏重任何方向；
     * 如果是专注于水平方向，可以将此值设置小于1的数，
     * 如果是专注于垂直方向，可以将此值设置大于1的数；
     * 如果是垂直与水平同等重要，将此值设置成1
     */
    init {
        require(pointSize > 0) { "Illegal:pointSize <= 0" }
        require(longClickTime > 0) { "Illegal:longClickTime <= 0" }
        require(xyScale != 0f) { "Illegal:xyScale equals 0" }
        this.pointSize = pointSize
        this.longClickTime = longClickTime
        this.xyScale = xyScale
    }
}