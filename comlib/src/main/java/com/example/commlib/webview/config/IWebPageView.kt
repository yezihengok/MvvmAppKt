package com.example.commlib.webview.config

import android.content.Intent
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout

/**
 * Created by jingbin on 2019/07/27.
 */
open interface IWebPageView {
    /**
     * 显示webview
     */
    fun showWebView()

    /**
     * 隐藏webview
     */
    fun hindWebView()

    /**
     * 进度条变化时调用
     *
     * @param newProgress 进度0-100
     */
    fun startProgress(newProgress: Int)

    /**
     * 添加视频全屏view
     */
    fun fullViewAddView(view: View?)

    /**
     * 显示全屏view
     */
    fun showVideoFullView()

    /**
     * 隐藏全屏view
     */
    fun hindVideoFullView()

    /**
     * 设置横竖屏
     */
    fun setRequestedOrientation(screenOrientationPortrait: Int)

    /**
     * 得到全屏view
     */
    fun getVideoFullView(): FrameLayout?

    /**
     * 加载视频进度条
     */
    fun getVideoLoadingProgressView(): View?

    /**
     * 返回标题处理
     */
    fun onReceivedTitle(view: WebView?, title: String?)

    /**
     * 上传图片打开文件夹
     */
    fun startFileChooserForResult(intent: Intent?, requestCode: Int)

    /**
     * 页面加载结束，添加js监听等
     */
    fun onPageFinished(view: WebView?, url: String?)

    /**
     * 是否处理打开三方app
     * @param url
     */
    fun isOpenThirdApp(url: String?): Boolean

    /**
     * 网页加载失败
     */
    fun onReceivedError(errorCode: Int, description: String?)
    fun onPageStarted(url: String)
}