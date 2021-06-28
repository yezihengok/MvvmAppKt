package com.example.commlib.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * @anthor yzh
 * @time 2019/12/2 9:52
 */
class AudioWaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0): View(context, attrs,defStyleAttr) {
    lateinit var paint: Paint
    lateinit var rectF1: RectF
    lateinit var rectF2: RectF
    lateinit var rectF3: RectF
    lateinit var rectF4: RectF
    lateinit var rectF5: RectF
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    /** 每个条的宽度  */
    private var rectWidth: Int = 0

    /** 条数  */
    private val columnCount: Int = 5

    /** 条间距  */
    private val space: Int = 6

    /** 条随机高度  */
    private var randomHeight: Int = 0
    lateinit var random: Random

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            invalidate()
        }
    }

//    constructor(context: Context) : this(context, null) {
//        init()
//    }
//
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
//        init()
//    }
        init {
            init()
        }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        rectWidth = (viewWidth - space * (columnCount - 1)) / columnCount
    }

    private fun init() {
        paint = Paint()
        paint.color = Color.LTGRAY
        paint.style = Paint.Style.FILL
        random = Random()
        initRect()
    }

    private fun initRect() {
        rectF1 = RectF()
        rectF2 = RectF()
        rectF3 = RectF()
        rectF4 = RectF()
        rectF5 = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left: Int = rectWidth + space

        //画每个条之前高度都重新随机生成
        randomHeight = random.nextInt(viewHeight)
        rectF1.set(left * 0.toFloat(), randomHeight.toFloat(), left * 0 + rectWidth.toFloat(), viewHeight.toFloat())
        randomHeight = random.nextInt(viewHeight)
        rectF2.set(left * 1.toFloat(), randomHeight.toFloat(), left * 1 + rectWidth.toFloat(), viewHeight.toFloat())
        randomHeight = random.nextInt(viewHeight)
        rectF3.set(left * 2.toFloat(), randomHeight.toFloat(), left * 2 + rectWidth.toFloat(), viewHeight.toFloat())
        randomHeight = random.nextInt(viewHeight)
        rectF4.set(left * 3.toFloat(), randomHeight.toFloat(), left * 3 + rectWidth.toFloat(), viewHeight.toFloat())
        randomHeight = random.nextInt(viewHeight)
        rectF5.set(left * 4.toFloat(), randomHeight.toFloat(), left * 4 + rectWidth.toFloat(), viewHeight.toFloat())
        canvas.drawRect(rectF1, paint)
        canvas.drawRect(rectF2, paint)
        canvas.drawRect(rectF3, paint)
        canvas.drawRect(rectF4, paint)
        canvas.drawRect(rectF5, paint)
        handler.sendEmptyMessageDelayed(0, 200) //每间隔200毫秒发送消息刷新
    }
}
