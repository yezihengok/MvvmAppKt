package com.example.commlib.weight

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.text.DecimalFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by yzh-t105 on 2017/8/29.
 */
class RunningTextView  @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyle: Int=0): AppCompatTextView(context,attrs,defStyle) {
    var content // 最后显示的数字
            : Double = 0.0
    private var frames: Int = 40 // 总共跳跃的帧数,默认40跳
    private var nowNumber: Double = 0.00 // 显示的时间
    lateinit var thread_pool: ExecutorService
    private var formater // 格式化时间，保留两位小数
            : DecimalFormat? = null
    init {
        init()
    }
//    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
//        init()
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        init()
//    }
//
//    constructor(context: Context?) : super(context) {
//        init()
//    }

    // 设置帧数
    fun setFrames(frames: Int) {
        this.frames = frames
    }
    // mRunningtextview.setFormat("¥00.00");
    /**
     * 设置数字格式，具体查DecimalFormat类的api
     * @param pattern
     */
    fun setFormat(pattern: String?) {
        formater = DecimalFormat(pattern)
    }

    @SuppressLint("HandlerLeak")
    // 初始化
    private fun init() {
        thread_pool = Executors.newFixedThreadPool(2) // 2个线程的线程池
        formater = DecimalFormat("0.00") // 最多两位小数，而且不够两位整数用0占位。可以通过setFormat再次设置
        Companion.handler = object : Handler() {
             override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                this@RunningTextView.text = formater!!.format(nowNumber)
                    .toString() // 更新显示的数字
                nowNumber += msg.obj.toString().toDouble() // 跳跃arg1那么多的数字间隔
                //              Log.v("nowNumber增加之后的值", nowNumber + "");
                if (nowNumber < content) {
                    val msg2: Message = Companion.handler!!.obtainMessage()
                    msg2.obj = msg.obj
                    Companion.handler!!.sendMessage(msg2) // 继续发送通知改变UI
                } else {
                    this@RunningTextView.text = formater!!.format(content)
                        .toString() // 最后显示的数字，动画停止
                }
            }
        }
    }

    /**
     * 播放数字动画的方法
     *
     * @param moneyNumber
     */
    fun playNumber(moneyNumber: Double) {
        if (moneyNumber == 0.0) {
            this@RunningTextView.text = "0.00"
            return
        }
        content = moneyNumber // 设置最后要显示的数字
        nowNumber = 0.00 // 默认都是从0开始动画
        thread_pool.execute {
            val msg: Message = Companion.handler.obtainMessage()
            val temp: Double = content / frames
            msg.obj = if (temp < 0.01) 0.01 else temp // 如果每帧的间隔比1小，就设置为1
            //              Log.v("每帧跳跃的数量：", "" + msg.obj.toString());
            Companion.handler.sendMessage(msg) // 发送通知改变UI
        }
    }

    companion object {
        lateinit var handler: Handler
    }
}