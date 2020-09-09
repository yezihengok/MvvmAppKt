package com.example.commlib.utils.scroll

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker

/**
 * 滑动辅助器，可以直接使用[ViewScrollHelper]
 */
abstract class ScrollHelper(
    var gestureHelper: GestureHelper?) {
    private var velocityTracker: VelocityTracker?
    private var startTouchX = 0f
    private var startTouchY = 0f
    private var startScrollX = 0
    private var startScrollY = 0

    constructor(context: Context) : this(GestureHelper.Companion.createDefault(context)) {}

    /**
     * 触发触摸事件
     *
     * @param event 事件
     */
    fun onTouchEvent(event: MotionEvent) {
        gestureHelper?.onTouchEvent(event)
        velocityTracker?.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setStartPosition(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (canScroll()) {
                    val rangeX = event.x - startTouchX
                    val rangeY = event.y - startTouchY
                    var dstX = (startScrollX - rangeX).toInt()
                    var dstY = (startScrollY - rangeY).toInt()
                    if (dstX < minHorizontallyScroll) {
                        dstX = 0
                        startTouchX = event.x
                        startScrollX = dstX
                    } else if (dstX > maxHorizontallyScroll) {
                        dstX = viewHorizontallyScrollSize
                        startTouchX = event.x
                        startScrollX = dstX
                    }
                    if (dstY < minVerticallyScroll) {
                        dstY = 0
                        startTouchY = event.y
                        startScrollY = dstY
                    } else if (dstY > maxVerticallyScroll) {
                        dstY = viewVerticallyScrollSize
                        startTouchY = event.y
                        startScrollY = dstY
                    }
                    viewScrollTo(dstX, dstY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker?.computeCurrentVelocity(1000)
                if (canScroll()) {
                    val xv = velocityTracker?.xVelocity?:0.0f
                    val yv = velocityTracker?.yVelocity?:0.0f
                    viewFling(xv, yv)
                }
            }
        }
    }

    /**
     * 设置起始位置，一般是ACTION_DOWN的时候执行，如果有特殊要求，可以在外部主动调用，更改起始位置
     *
     * @param x 位置X
     * @param y 位置Y
     */
    fun setStartPosition(x: Float, y: Float) {
        startTouchX = x
        startTouchY = y
        startScrollX = viewScrollX
        startScrollY = viewScrollY
    }

    /**
     * 判断是否可以滑动
     *
     * @return 是否可以滑动
     */
    protected fun canScroll(): Boolean {
        return gestureHelper?.isVerticalGesture?:false || gestureHelper?.isHorizontalGesture?:false
    }

    /**
     * 获取水平方向最小的滑动位置
     *
     * @return 水平方向最小的滑动位置
     */
    val minHorizontallyScroll: Int
        get() = 0

    /**
     * 获取水平方向最大的滑动位置
     *
     * @return 水平方向最大的滑动位置
     */
    val maxHorizontallyScroll: Int
        get() = viewHorizontallyScrollSize

    /**
     * 获取垂直方向最小的滑动位置
     *
     * @return 垂直方向最小的滑动位置
     */
    val minVerticallyScroll: Int
        get() = 0

    /**
     * 获取垂直方向最大的滑动位置
     *
     * @return 垂直方向最大的滑动位置
     */
    val maxVerticallyScroll: Int
        get() = viewVerticallyScrollSize

    /**
     * 回收此对象
     */
    fun recycle() {
        if (null != velocityTracker) {
            velocityTracker?.recycle()
            velocityTracker = null
        }
        if (null != gestureHelper) {
            gestureHelper = null
        }
    }

    /**
     * 获取视图滑动位置X
     *
     * @return 视图滑动位置Y
     */
    protected abstract val viewScrollX: Int

    /**
     * 获取视图滑动位置Y
     *
     * @return 视图滑动位置Y
     */
    protected abstract val viewScrollY: Int

    /**
     * 获取视图水平方向可以滑动的范围，一般在此方法返回
     * [ViewGroup.computeHorizontalScrollRange] 减去
     * [ViewGroup.computeHorizontalScrollExtent] 的差
     * <br></br>result = range-extent
     *
     * @return 水平方向可以滑动的范围
     */
    protected abstract val viewHorizontallyScrollSize: Int

    /**
     * 获取视图垂直方向可以滑动的范围，一般在此方法返回
     * [ViewGroup.computeVerticalScrollRange] 减去
     * [ViewGroup.computeVerticalScrollExtent] 的差
     * <br></br>result = range-extent
     *
     * @return 垂直方向可以滑动的范围
     */
    protected abstract val viewVerticallyScrollSize: Int

    /**
     * 将视图滑动至指定位置，一般调用[View.scrollTo][android.view.View.scrollTo]方法即可
     *
     * @param x 位置X
     * @param y 位置Y
     */
    protected abstract fun viewScrollTo(x: Int, y: Int)

    /**
     * 当触摸抬起时，执行此方法，一般在此方法内执行
     * [Scroller.fling][android.widget.Scroller.fling]
     * 方法，需要注意的是，速度应该取参数的相反值，因为参数的速度表示的是触摸滑动的速度，刚好与滑动
     * 的速度方向相反。
     *
     * @param xv 水平触摸滑动的速度
     * @param yv 垂直触摸滑动的速度
     */
    protected abstract fun viewFling(xv: Float, yv: Float)

    init {
        velocityTracker = VelocityTracker.obtain()
    }
}