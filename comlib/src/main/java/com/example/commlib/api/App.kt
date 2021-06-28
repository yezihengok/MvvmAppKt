package com.example.commlib.api

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.blankj.ALog
import com.example.commlib.BuildConfig
import com.example.commlib.R
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import java.util.ArrayList

/**
 * Created by yzh on 2020/8/19 14:05.
 */
open class App : MultiDexApplication() {

    init {
        instance = this
    }
    companion object{

        //lateinit 和 lazy 是 Kotlin 中的两种不同的延迟初始化的实现

        // lateinit可以在任何位置初始化并且可以初始化多次。而lazy在第一次被调用时就被初始化，想要被改变只能重新定义      只用于变量 var

        //by lazy 惰性初始化 是第一次访问该属性的时候，才根据需要创建对象的一部分，后续不在创建初始化 .例如实现单例、初始化某些对象。 只能用在常量 val 后

        lateinit var instance:App
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance=this
        initLog()
        setSmartRefreshLayout()
    }

    private fun initLog(){
        var config=ALog.init(this)
            .setLogSwitch(BuildConfig.DEBUG) // 设置是否输出到控制台开关，默认开
            .setConsoleSwitch(BuildConfig.DEBUG) // 设置 log 全局标签，默认为空
            .setGlobalTag(null) // 当全局标签不为空时，我们输出的 log 全部为该 tag，
            // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            // 设置 log 头信息开关，默认为开
            .setLogHeadSwitch(true) // 打印 log 时是否存到文件的开关，默认关
            .setLog2FileSwitch(false) // 当自定义路径为空时，写入应用的 /cache/log/ 目录中
            .setDir("") // 当文件前缀为空时，默认为 "alog"，即写入文件为 "alog-MM-dd.txt"
            .setFilePrefix("") // 输出日志是否带边框开关，默认开
            .setBorderSwitch(true) // 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setSingleTagSwitch(false) // log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setConsoleFilter(ALog.V) // log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(ALog.V) // log 栈深度，默认为 1
            .setStackDeep(1) // 设置栈偏移，比如二次封装的话就需要设置，默认为 0
            .setStackOffset(0) // 设置日志可保留天数，默认为 -1 表示无限时长
            .setSaveDays(3) // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
            .addFormatter<ArrayList<*>> { list -> "ALog Formatter ArrayList { $list }" }
        ALog.d(config.toString())
    }

    private fun setSmartRefreshLayout(){
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            val header = ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Scale)
            header.setPrimaryColorId(R.color.ui_activity_bg)
            header.setAccentColorId(R.color.ui_gray)
            header.setTextSizeTime(13f)
            header.setTextSizeTitle(15f)
            header //指定为经典Header，默认是 贝塞尔雷达Header
        }
    }



}
