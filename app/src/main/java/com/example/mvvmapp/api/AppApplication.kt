package com.example.mvvmapp.api

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.blankj.ALog
import com.didichuxing.doraemonkit.DoraemonKit
import com.example.commlib.api.App
import com.example.commlib.crash.CaocConfig
import com.example.commlib.crash.MyDefaultErrorActivity
import com.example.commlib.webview.WebViewActivity.Companion.loadUrl
import com.example.mvvmapp.R
import com.example.mvvmapp.db.DaoManager
import com.example.mvvmapp.main.MainNewActivity

/**
 * Anthor yzh Date 2019/11/6 10:36
 */
class AppApplication : App() {
    override fun onCreate() {
        super.onCreate()
        initCrash()
        registerActivityLifecycleCallbacks(this)
        initDoraemonKit()
        initGreenDao()
    }

    private fun initGreenDao() {
        DaoManager.instance.init(this)
    }

    private fun initDoraemonKit() {
        // DoraemonKit.install(this);
        DoraemonKit.install(this, null, "780a59b23dde39e0527856bc30cd3056")
        DoraemonKit.setDebug(false) //DoraemonKit疯狂打印日志看了很烦

        // H5任意门功能需要，非必须（使用自己的H5容器打开这个链接）
        DoraemonKit.setWebDoorCallback { context: Context?, url: String? -> loadUrl(url, "DoraemonKit测试") }
    }

    private fun initCrash() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
            .enabled(true) //是否启动全局异常捕获
            .showErrorDetails(true) //是否显示错误详细信息
            .showRestartButton(true) //是否显示重启按钮
            .trackActivities(true) //是否跟踪Activity
            .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
            .errorDrawable(R.drawable.customactivityoncrash_error_image) //错误图标
            .restartActivity(MainNewActivity::class.java) //重新启动后的activity
            .errorActivity(MyDefaultErrorActivity::class.java) //崩溃后的错误activity(不设置使用默认)
            //.eventListener(new YourCustomEventListener()) //崩溃后的错误监听
            .apply()
    }

    //注册activity生命周期， activity生命周期监听可以做一些事情
    private fun registerActivityLifecycleCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                ALog.v(activity.javaClass.simpleName + "-onActivityCreated")
                ActivityManager.instance?.addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                ALog.v(activity.javaClass.simpleName + "-onActivityResumed")
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}
            override fun onActivityDestroyed(activity: Activity) {
                ALog.v(activity.javaClass.simpleName + "-onActivityDestroyed")
                ActivityManager.instance?.removeActivity(activity)
            }
        })
    }
}