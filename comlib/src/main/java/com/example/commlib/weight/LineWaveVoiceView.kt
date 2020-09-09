package com.example.commlib.weight

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.commlib.R
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @anthor yzh
 * 语音录制的动画效果
 * 基于 https://www.jianshu.com/p/6dd10a5adca8 修改
 */
class LineWaveVoiceView : View {
    private val paint: Paint = Paint()
    private var task: Runnable? = null
    private val executorService: ExecutorService = Executors.newCachedThreadPool()
    private val rectRight: RectF = RectF() //右边波纹矩形的数据，10个矩形复用一个rectF
    private val rectLeft: RectF = RectF() //左边波纹矩形的数据
    private var text: String = DEFAULT_TEXT
    private var updateSpeed: Int = 0
    private var lineColor: Int = 0
    private var textColor: Int = 0
    private var lineWidth: Float = 0f
    private var textSize: Float = 0f

    constructor(context: Context?) : super(context) {}

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initView(attrs, context)
        resetView(mWaveList, DEFAULT_WAVE_HEIGHT)
        task = LineJitterTask()
    }

    private fun initView(attrs: AttributeSet?, context: Context) {
        //获取布局属性里的值
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.LineWaveVoiceView)
        lineColor = mTypedArray.getColor(R.styleable.LineWaveVoiceView_voiceLineColor, ContextCompat.getColor(context, R.color.ui_blue))
        lineWidth = mTypedArray.getDimension(R.styleable.LineWaveVoiceView_voiceLineWidth, LINE_WIDTH.toFloat())
        textSize = mTypedArray.getDimension(R.styleable.LineWaveVoiceView_voiceTextSize, 42f)
        textColor = mTypedArray.getColor(R.styleable.LineWaveVoiceView_voiceTextColor, ContextCompat.getColor(context, R.color.ui_gray))
        updateSpeed = mTypedArray.getColor(R.styleable.LineWaveVoiceView_updateSpeed, UPDATE_INTERVAL_TIME)
        mTypedArray.recycle()
    }

    //    .获取该View的实际宽高的一半，然后设置矩形的四边，熟悉Android的view的绘制都知道，view的宽为right - left，
    //    高度为bottom - top。所以让right比left多一个lineWidth即可让矩形的宽为lineWidth，
    //    bottom比top多4lineWidth即可让高读为4lineWidth，并利用实际宽高的一半，把矩形绘制在view的中央。
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mWaveList == null) {
            return
        }
        //获取实际宽高的一半
        val widthCentre: Int = width / 2
        val heightCentre: Int = height / 2
        paint.strokeWidth = 0f
        paint.color = textColor
        paint.textSize = textSize
        val textWidth: Float = paint.measureText(text)
        canvas.drawText(text, widthCentre - textWidth / 2, heightCentre - (paint.ascent() + paint.descent()) / 2, paint)

        //设置颜色
        paint.color = lineColor
        //填充内部
        paint.style = Paint.Style.FILL
        //设置抗锯齿
        paint.isAntiAlias = true
        for (i in 0..8) {
            rectRight.left = widthCentre + (textWidth / 2) + ((1 + 2 * i) * lineWidth)
            rectRight.top = heightCentre - lineWidth * mWaveList[i] / 2
            rectRight.right = widthCentre + (textWidth / 2) + ((2 + 2 * i) * lineWidth)
            rectRight.bottom = heightCentre + lineWidth * mWaveList[i] / 2

            //左边矩形
            rectLeft.left = widthCentre - (textWidth / 2) - ((2 + 2 * i) * lineWidth)
            rectLeft.top = heightCentre - mWaveList.get(i) * lineWidth / 2
            rectLeft.right = widthCentre - (textWidth / 2) - ((1 + 2 * i) * lineWidth)
            rectLeft.bottom = heightCentre + mWaveList.get(i) * lineWidth / 2
            canvas.drawRoundRect(rectRight, 6f, 6f, paint)
            canvas.drawRoundRect(rectLeft, 6f, 6f, paint)
        }
    }

    private val mWaveList: LinkedList<Int>? = LinkedList()
    private var maxDb: Float = 0f
    private fun resetView(list: MutableList<Int>?, array: IntArray) {
        list!!.clear()
        for (anArray: Int in array) {
            list.add(anArray)
        }
    }

    @Synchronized
    private fun refreshElement() {
        val random: Random = Random()
        maxDb = random.nextInt(5) + 1.toFloat()
        val waveH: Int = MIN_WAVE_HEIGHT + Math.round(maxDb * (MAX_WAVE_HEIGHT - MIN_WAVE_HEIGHT))
        // ALog.i("waveH===="+waveH);
        mWaveList!!.add(0, waveH)
        mWaveList.removeLast()
    }

    var isStart: Boolean = false

    private inner class LineJitterTask constructor() : Runnable {
        public override fun run() {
            while (isStart) {
                refreshElement()
                try {
                    Thread.sleep(updateSpeed.toLong())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                postInvalidate()
            }
        }
    }

    @Synchronized
    fun startRecord() {
        isStart = true
        executorService.execute(task)
    }

    @Synchronized
    fun stopRecord() {
        isStart = false
        mWaveList!!.clear()
        resetView(mWaveList, DEFAULT_WAVE_HEIGHT)
        postInvalidate()
    }

    @Synchronized
    fun setText(text: String) {
        this.text = text
        postInvalidate()
    }

    fun setUpdateSpeed(updateSpeed: Int) {
        this.updateSpeed = updateSpeed
    }

    companion object {
        private val DEFAULT_TEXT: String = " 请录音 "
        private val LINE_WIDTH: Int = 9 //默认矩形波纹的宽度
        private val MIN_WAVE_HEIGHT: Int = 2 //矩形线最小高

        //  private static final int MAX_WAVE_HEIGHT = 10;//矩形线最大高
        private val MAX_WAVE_HEIGHT: Int = 3 //矩形线最大高
        private val DEFAULT_WAVE_HEIGHT: IntArray = intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2)
        private val UPDATE_INTERVAL_TIME: Int = 100 //100ms更新一次
    }
}