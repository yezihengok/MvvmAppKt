/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.commlib.weight

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Paint.FontMetricsInt
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.example.commlib.R
import com.example.commlib.utils.DensityUtil

/**
 * 一个进度条控件，通过颜色变化显示进度，支持环形和矩形两种形式，主要特性如下：
 *
 *  1. 支持在进度条中以文字形式显示进度，支持修改文字的颜色和大小。
 *  1. 可以通过 xml 属性修改进度背景色，当前进度颜色，进度条尺寸。
 *  1. 支持限制进度的最大值。
 *
 *
 * @author cginechen
 * @date 2015-07-29
 */
class IProgressBar : View {
    var mIProgressBarTextGenerator: IProgressBarTextGenerator? = null

    /*rect_progress member*/
    var mBgRect: RectF? = null
    var mProgressRect: RectF? = null

    /*common member*/
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mType: Int = 0
    private var mProgressColor: Int = 0
    private var mBackgroundColor: Int = 0
    private var mMaxValue: Int = 0
    private var mValue: Int = 0
    private var mPendingValue: Int = 0
    private var mAnimationStartTime: Long = 0
    private var mAnimationDistance: Int = 0
    private var mAnimationDuration: Int = 0
    private var mTextSize: Int = 0
    private var mTextColor: Int = 0
    private var mRoundCap: Boolean = false
    private val mBackgroundPaint: Paint = Paint()
    private val mPaint: Paint = Paint()
    private val mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mArcOval: RectF = RectF()
    private var mText: String? = ""
    private var mStrokeWidth: Int = 0
    private var mCircleRadius: Int = 0
    private var mCenterPoint: Point? = null

    constructor(context: Context) : super(context) {
        setup(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context, attrs)
    }

    fun setup(context: Context, attrs: AttributeSet?) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.IProgressBar)
        mType = array.getInt(R.styleable.IProgressBar_qmui_type, TYPE_RECT)
        mProgressColor = array.getColor(R.styleable.IProgressBar_qmui_progress_color, DEFAULT_PROGRESS_COLOR)
        mBackgroundColor = array.getColor(R.styleable.IProgressBar_qmui_background_color, DEFAULT_BACKGROUND_COLOR)
        mMaxValue = array.getInt(R.styleable.IProgressBar_qmui_max_value, 100)
        mValue = array.getInt(R.styleable.IProgressBar_qmui_value, 0)
        mRoundCap = array.getBoolean(R.styleable.IProgressBar_qmui_stroke_round_cap, false)
        mTextSize = DEFAULT_TEXT_SIZE
        if (array.hasValue(R.styleable.IProgressBar_android_textSize)) {
            mTextSize = array.getDimensionPixelSize(R.styleable.IProgressBar_android_textSize, DEFAULT_TEXT_SIZE)
        }
        mTextColor = DEFAULT_TEXT_COLOR
        if (array.hasValue(R.styleable.IProgressBar_android_textColor)) {
            mTextColor = array.getColor(R.styleable.IProgressBar_android_textColor, DEFAULT_TEXT_COLOR)
        }
        if (mType == TYPE_CIRCLE) {
            mStrokeWidth = array.getDimensionPixelSize(R.styleable.IProgressBar_qmui_stroke_width, DEFAULT_STROKE_WIDTH)
        }
        array.recycle()
        configPaint(mTextColor, mTextSize, mRoundCap)
        setProgress(mValue)
    }

    private fun configShape() {
        if (mType == TYPE_RECT || mType == TYPE_ROUND_RECT) {
            mBgRect = RectF(paddingLeft.toFloat(), paddingTop.toFloat(), (mWidth + paddingLeft).toFloat(), (mHeight + paddingTop).toFloat())
            mProgressRect = RectF()
        } else {
            mCircleRadius = (Math.min(mWidth, mHeight) - mStrokeWidth) / 2
            mCenterPoint = Point(mWidth / 2, mHeight / 2)
        }
    }

    private fun configPaint(textColor: Int, textSize: Int, isRoundCap: Boolean) {
        mPaint.color = mProgressColor
        mBackgroundPaint.setColor(mBackgroundColor)
        if (mType == TYPE_RECT || mType == TYPE_ROUND_RECT) {
            mPaint.style = Paint.Style.FILL
            mBackgroundPaint.style = Paint.Style.FILL
        } else {
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mStrokeWidth.toFloat()
            mPaint.isAntiAlias = true
            if (isRoundCap) {
                mPaint.strokeCap = Paint.Cap.ROUND
            }
            mBackgroundPaint.style = Paint.Style.STROKE
            mBackgroundPaint.strokeWidth = mStrokeWidth.toFloat()
            mBackgroundPaint.isAntiAlias = true
        }
        mTextPaint.color = textColor
        mTextPaint.textSize = textSize.toFloat()
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    fun setType(type: Int) {
        mType = type
        configPaint(mTextColor, mTextSize, mRoundCap)
        invalidate()
    }

    fun setBarColor(backgroundColor: Int, progressColor: Int) {
        mBackgroundColor = backgroundColor
        mProgressColor = progressColor
        mBackgroundPaint.color = mBackgroundColor
        mPaint.color = mProgressColor
        invalidate()
    }

    /**
     * 设置进度文案的文字大小
     *
     * @see .setTextColor
     * @see .setIProgressBarTextGenerator
     */
    fun setTextSize(textSize: Int) {
        mTextPaint.textSize = textSize.toFloat()
        invalidate()
    }

    /**
     * 设置进度文案的文字颜色
     *
     * @see .setTextSize
     * @see .setIProgressBarTextGenerator
     */
    fun setTextColor(textColor: Int) {
        mTextPaint.color = textColor
        invalidate()
    }

    /**
     * 设置环形进度条的两端是否有圆形的线帽，类型为[.TYPE_CIRCLE]时生效
     */
    fun setStrokeRoundCap(isRoundCap: Boolean) {
        mPaint.strokeCap = if (isRoundCap) Paint.Cap.ROUND else Paint.Cap.BUTT
        invalidate()
    }

    /**
     * 通过 [IProgressBarTextGenerator] 设置进度文案
     */
    fun setIProgressBarTextGenerator(IProgressBarTextGenerator: IProgressBarTextGenerator?) {
        mIProgressBarTextGenerator = IProgressBarTextGenerator
    }

    fun getIProgressBarTextGenerator(): IProgressBarTextGenerator? {
        return mIProgressBarTextGenerator
    }

    override fun onDraw(canvas: Canvas) {
        if (mPendingValue != PENDING_VALUE_NOT_SET) {
            val elapsed: Long = System.currentTimeMillis() - mAnimationStartTime
            if (elapsed >= mAnimationDuration) {
                mValue = mPendingValue
                mPendingValue = PENDING_VALUE_NOT_SET
            } else {
                mValue = (mPendingValue - (1f - (elapsed.toFloat() / mAnimationDuration)) * mAnimationDistance).toInt()
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
        if (mIProgressBarTextGenerator != null) {
            mText = mIProgressBarTextGenerator!!.generateText(this, mValue, mMaxValue)
        }
        if (((mType == TYPE_RECT || mType == TYPE_ROUND_RECT) && mBgRect == null) ||
                (mType == TYPE_CIRCLE && mCenterPoint == null)) {
            // npe protect, sometimes measure may not be called by parent.
            configShape()
        }
        if (mType == TYPE_RECT) {
            drawRect(canvas)
        } else if (mType == TYPE_ROUND_RECT) {
            drawRoundRect(canvas)
        } else {
            drawCircle(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth - paddingLeft - paddingRight
        mHeight = measuredHeight - paddingTop - paddingBottom
        configShape()
        setMeasuredDimension(mWidth, mHeight)
    }

    private fun drawRect(canvas: Canvas) {
        canvas.drawRect((mBgRect)!!, mBackgroundPaint)
        mProgressRect!!.set(paddingLeft.toFloat(), paddingTop.toFloat(), paddingLeft + parseValueToWidth().toFloat(), paddingTop + mHeight.toFloat())
        canvas.drawRect((mProgressRect)!!, mPaint)
        if (mText != null && mText!!.isNotEmpty()) {
            val fontMetrics: FontMetricsInt = mTextPaint.fontMetricsInt
            val baseline: Float = mBgRect!!.top + (mBgRect!!.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            canvas.drawText(mText!!, mBgRect!!.centerX(), baseline, mTextPaint)
        }
    }

    private fun drawRoundRect(canvas: Canvas) {
        val round: Float = mHeight / 2f
        canvas.drawRoundRect((mBgRect)!!, round, round, mBackgroundPaint)
        mProgressRect!!.set(paddingLeft.toFloat(), paddingTop.toFloat(), paddingLeft + parseValueToWidth().toFloat(), paddingTop + mHeight.toFloat())
        canvas.drawRoundRect((mProgressRect)!!, round, round, mPaint)
        if (mText != null && mText!!.isNotEmpty()) {
            val fontMetrics: FontMetricsInt = mTextPaint.fontMetricsInt
            val baseline: Float = mBgRect!!.top + (mBgRect!!.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            canvas.drawText(mText!!, mBgRect!!.centerX(), baseline, mTextPaint)
        }
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(mCenterPoint!!.x.toFloat(), mCenterPoint!!.y.toFloat(), mCircleRadius.toFloat(), mBackgroundPaint)
        mArcOval.left = mCenterPoint!!.x - mCircleRadius.toFloat()
        mArcOval.right = mCenterPoint!!.x + mCircleRadius.toFloat()
        mArcOval.top = mCenterPoint!!.y - mCircleRadius.toFloat()
        mArcOval.bottom = mCenterPoint!!.y + mCircleRadius.toFloat()
        if (mValue > 0) {
            canvas.drawArc(mArcOval, 270f, 360f * mValue / mMaxValue, false, mPaint)
        }
        if (mText != null && mText!!.isNotEmpty()) {
            val fontMetrics: FontMetricsInt = mTextPaint.fontMetricsInt
            val baseline: Float = mArcOval.top + (mArcOval.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            canvas.drawText(mText!!, mCenterPoint!!.x.toFloat(), baseline, mTextPaint)
        }
    }

    private fun parseValueToWidth(): Int {
        return mWidth * mValue / mMaxValue
    }

    fun getProgress(): Int {
        return mValue
    }

    fun setProgress(progress: Int) {
        setProgress(progress, true)
    }

    fun setProgress(progress: Int, animated: Boolean) {
        if (progress > mMaxValue || progress < 0) {
            return
        }
        if ((mPendingValue == PENDING_VALUE_NOT_SET && mValue == progress) ||
                (mPendingValue != PENDING_VALUE_NOT_SET && mPendingValue == progress)) {
            return
        }
        if (!animated) {
            mPendingValue = PENDING_VALUE_NOT_SET
            mValue = progress
            invalidate()
        } else {
            mAnimationDuration = Math.abs((TOTAL_DURATION * (mValue - progress) / mMaxValue.toFloat()).toInt())
            mAnimationStartTime = System.currentTimeMillis()
            mAnimationDistance = progress - mValue
            mPendingValue = progress
            invalidate()
        }
    }

    fun getMaxValue(): Int {
        return mMaxValue
    }

    fun setMaxValue(maxValue: Int) {
        mMaxValue = maxValue
    }

    open interface IProgressBarTextGenerator {
        /**
         * 设置进度文案, [IProgressBar] 会在进度更新时调用该方法获取要显示的文案
         *
         * @param value    当前进度值
         * @param maxValue 最大进度值
         * @return 进度文案
         */
        fun generateText(progressBar: IProgressBar?, value: Int, maxValue: Int): String?
    }

    companion object {
        val TYPE_RECT: Int = 0
        val TYPE_CIRCLE: Int = 1
        val TYPE_ROUND_RECT: Int = 2
        val TOTAL_DURATION: Int = 1000
        val DEFAULT_PROGRESS_COLOR: Int = Color.BLUE
        val DEFAULT_BACKGROUND_COLOR: Int = Color.GRAY
        val DEFAULT_TEXT_SIZE: Int = 20
        val DEFAULT_TEXT_COLOR: Int = Color.BLACK
        private val PENDING_VALUE_NOT_SET: Int = -1

        /*circle_progress member*/
        var DEFAULT_STROKE_WIDTH: Int = DensityUtil.dip2px(40f)
    }
}