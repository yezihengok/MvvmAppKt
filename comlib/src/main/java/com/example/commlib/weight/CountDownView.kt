package com.example.commlib.weight

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.commlib.R
import com.example.commlib.utils.DensityUtil

/**
 * 环形倒计时view
 */
class CountDownView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    //圆轮颜色
    private val mRingColor: Int

    //圆轮宽度
    private val mRingWidth: Int

    //圆轮进度值文本大小
    private val mRingProgessTextSize: Int

    //宽度
    private var mWidth: Int = 0

    //高度
    private var mHeight: Int = 0
    private val mPaint: Paint

    //圆环的矩形区域
    private var mRectF: RectF? = null

    //
    private val mProgessTextColor: Int
    private var mCountdownTime: Int
    private var mCurrentProgress: Float = 0f
    private var mListener: OnCountDownFinishListener? = null
    fun setCountdownTime(mCountdownTime: Int) {
        this.mCountdownTime = mCountdownTime
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mRectF = RectF((0 + mRingWidth / 2).toFloat(), (0 + mRingWidth / 2).toFloat(),
                (mWidth - mRingWidth / 2).toFloat(), (mHeight - mRingWidth / 2).toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 圆环
         */
        //颜色
        mPaint.color = mRingColor
        //空心
        mPaint.style = Paint.Style.STROKE
        //宽度
        mPaint.strokeWidth = mRingWidth.toFloat()
        canvas.drawArc((mRectF)!!, -90f, mCurrentProgress - 360, false, mPaint)
        //绘制文本
        val textPaint: Paint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        val text: String = "${mCountdownTime - ((mCurrentProgress / 360 * mCountdownTime).toInt())}s"
        textPaint.textSize = mRingProgessTextSize.toFloat()
        textPaint.color = mProgessTextColor

        //文字居中显示
        val fontMetrics: FontMetricsInt = textPaint.fontMetricsInt
        val baseline: Int = (((mRectF!!.bottom + mRectF!!.top) - fontMetrics.bottom - fontMetrics.top) / 2).toInt()
        canvas.drawText(text, mRectF!!.centerX(), baseline.toFloat(), textPaint)
    }

    private fun getValA(countdownTime: Long): ValueAnimator {
        val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = countdownTime
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = 0
        return valueAnimator
    }

    /**
     * 开始倒计时
     */
    fun startCountDown() {
        isClickable = false
        val valueAnimator: ValueAnimator = getValA(mCountdownTime * 1000.toLong())
        valueAnimator.addUpdateListener { animation ->
            val i: Float = java.lang.Float.valueOf(animation.animatedValue.toString())
            mCurrentProgress = (360 * (i / 100f))
            invalidate()
        }
        valueAnimator.start()
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //倒计时结束回调
                if (mListener != null) {
                    mListener!!.countDownFinished()
                }
                isClickable = true
            }
        })
    }

    fun setAddCountDownListener(mListener: OnCountDownFinishListener?) {
        this.mListener = mListener
    }

    open interface OnCountDownFinishListener {
        fun countDownFinished()
    }

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView)
        mRingColor = a.getColor(R.styleable.CountDownView_ringColor, context.resources.getColor(R.color.colorAccent))
        //mRingWidth = a.getFloat(R.styleable.CountDownView_ringWidth, 40);
        mRingWidth = a.getDimensionPixelSize(R.styleable.CountDownView_ringWidth, 30)
        mRingProgessTextSize = a.getDimensionPixelSize(R.styleable.CountDownView_progressTextSize, DensityUtil.sp2px(20f))
        mProgessTextColor = a.getColor(R.styleable.CountDownView_progressTextColor, context.resources.getColor(R.color.colorAccent))
        mCountdownTime = a.getInteger(R.styleable.CountDownView_countdownTime, 60)
        a.recycle()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.isAntiAlias = true
        setWillNotDraw(false)
    }
}