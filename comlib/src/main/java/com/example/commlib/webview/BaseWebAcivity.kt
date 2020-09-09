package com.example.commlib.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.FrameLayout
import androidx.databinding.ViewDataBinding
import com.blankj.ALog
import com.example.commlib.R
import com.example.commlib.base.mvvm.BaseActivity
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.utils.BarUtils
import com.example.commlib.webview.config.*

/**
 * Created by yzh on 2019/12/11 16:06.
 */
abstract class BaseWebAcivity<V : ViewDataBinding?, VM : BaseViewModel?> : BaseActivity<V, VM>(), IWebPageView {
    // 加载视频相关
    private var mWebChromeClient: MyWebChromeClient? = null

    // 全屏时视频加载view
    internal var videoFullView: FrameLayout? = null
    protected var webView: WebView? = null

    // 进度条
     var mProgressBar: WebProgress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BarUtils.setNavBarColor(mContext, getColors(R.color.colorAccent))
        }
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //设置相应的  设计图  dp  比率
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //"横屏"
            // DensityUtil.setDensity(getApplication(), this,960);
        } else {
            // "竖屏"
            //  DensityUtil.setDensity(getApplication(), this,600);
        }
    }

    protected fun back() {
        //返回网页上一页
        if (webView?.canGoBack()!! && !onPageError) {
            webView?.goBack()
            //退出网页
        } else {
            handleFinish()
        }
    }

    protected fun scrollChangeHeader(scrolledY: Int, titleLayout: View) {
        var scrolled: Int = scrolledY
        if (scrolledY < 0) {
            scrolled = 0
        }
        // 滑动多少距离后标题透明
        val slidingDistance: Int = 500
        val alpha: Float = Math.abs(scrolled) * 1.0f / slidingDistance
        //ALog.i("scrolledY "+scrolledY+"  "+alpha);
        if (alpha <= 1f) {
            titleLayout.alpha = 1f - alpha
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface", "JavascriptInterface")
    protected fun initWebView() {
        val ws: WebSettings? = webView?.settings
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws?.loadWithOverviewMode = false
        // 保存表单数据
        ws?.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws?.setSupportZoom(true)
        ws?.builtInZoomControls = false
        ws?.displayZoomControls = false
        // 启动应用缓存
        ws?.setAppCacheEnabled(true)
        // 设置缓存模式
        ws?.cacheMode = WebSettings.LOAD_DEFAULT
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws?.useWideViewPort = true
        // 不缩放
        webView?.setInitialScale(100)
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws?.javaScriptEnabled = true
        //  页面加载好以后，再放开图片
        ws?.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws?.domStorageEnabled = true
        // 排版适应屏幕
        ws?.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView是否新窗口打开(加了后可能打不开网页)
//        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用) */
        ws?.textZoom = 100
        mWebChromeClient = MyWebChromeClient(this)
        webView?.webChromeClient = mWebChromeClient
        webView?.webViewClient = MyWebViewClient(this)
        webView?.setOnLongClickListener { handleLongImage() }

        // 与js交互
        webView?.addJavascriptInterface(MyJavascriptInterface(this), "injectedObject")
        webView?.addJavascriptInterface(this, "androidInjected")
    }

    var time: Int = 0

    @JavascriptInterface
    fun reload(s: String) {
        ALog.v("reload===$s")
        if (time < 3) {
            runOnUiThread(Runnable {
                webView?.goBack() //先关闭加载的本地404页面,在刷新
                webView?.postDelayed(Runnable { webView?.reload() }, 1000)
            })
        } else {
            ALog.w("java传递参数去调用 js方法")
            loadJs("javascript:callJsWithArgs('" + "请稍后再试哦~" + "')")
        }
        time++
    }

    public override fun showWebView() {
        webView?.visibility = View.VISIBLE
    }

    public override fun hindWebView() {
        webView?.visibility = View.INVISIBLE
    }

    public override fun fullViewAddView(view: View?) {
        val decor: FrameLayout = window.decorView as FrameLayout
        videoFullView = FullscreenHolder(this)
        videoFullView?.addView(view)
        decor.addView(videoFullView)
    }

    public override fun showVideoFullView() {
        videoFullView?.visibility = View.VISIBLE
    }

    public override fun hindVideoFullView() {
        videoFullView?.visibility = View.GONE
    }

    public override fun startProgress(newProgress: Int) {
        mProgressBar?.setWebProgress(newProgress)
    }

    /**
     * android与js交互：
     * 前端注入js代码：不能加重复的节点，不然会覆盖
     * 前端调用js代码
     */
    public override fun onPageFinished(view: WebView?, url: String?) {
        if (!WebTools.isNetworkConnected(this)) {
            mProgressBar?.hide()
        }
        loadImageClickJS()
        loadTextClickJS()
        loadCallJS()
    }

    /**
     * 处理是否唤起三方app
     */
    override fun isOpenThirdApp(url: String?): Boolean {
        return WebTools.handleThirdApp(this, url)
    }

    /**
     * 网页是否加载失败了
     */
    private var onPageError: Boolean = false
    override fun onReceivedError(errorCode: Int, description: String?) {
        onPageError = true
        ALog.v("onReceivedError---$onPageError")
    }

    override fun onPageStarted(url: String) {
        if (WebTools.DEFAULT_ERROR != url) {
            onPageError = false //每次加载时初始化错误状态
        }
        ALog.v("url: $url")
    }

    /**
     * 前端注入JS：
     * 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
     */
    fun loadImageClickJS() {
        loadJs(("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"));}" +
                "}" +
                "})()"))
    }

    /**
     * 前端注入JS：
     * 遍历所有的 * 节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
     */
    fun loadTextClickJS() {
        loadJs(("javascript:(function(){" +
                "var objs =document.getElementsByTagName(\"li\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){" +
                "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                "}" +
                "})()"))
    }

    /**
     * 传应用内的数据给html，方便html处理
     */
    fun loadCallJS() {
        // 无参数调用
        //   loadJs("javascript:javacalljs()");
        // 传递参数调用
        // loadJs("javascript:javacalljswithargs('" + "android传入到网页里的数据，有参" + "')");
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    fun hideCustomView() {
        mWebChromeClient?.onHideCustomView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    public override fun getVideoFullView(): FrameLayout? {
        return videoFullView
    }

    public override fun getVideoLoadingProgressView(): View? {
        return LayoutInflater.from(this).inflate(R.layout.video_loading_progress, null)
    }

    public override fun onReceivedTitle(view: WebView?, title: String?) {
        setTitle(title)
    }

    protected abstract fun setTitle(mTitle: String?)
    public override fun startFileChooserForResult(intent: Intent?, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    /**
     * 上传图片之后的回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyWebChromeClient.Companion.FILECHOOSER_RESULTCODE) {
            mWebChromeClient?.mUploadMessage(data, resultCode)
        } else if (requestCode == MyWebChromeClient.Companion.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient?.mUploadMessageForAndroid5(data, resultCode)
        }
    }

    /**
     * 作为三方浏览器打开传过来的值
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    protected fun getDataFromBrowser(intent: Intent) {
        val data: Uri? = intent.getData()
        if (data != null) {
            try {
                val scheme: String? = data.scheme
                val host: String? = data.host
                val path: String? = data.path
                val text: String = "Scheme: $scheme\nhost: $host\npath: $path"
                Log.e("data", text)
                val url: String = "$scheme://$host$path"
                webView?.loadUrl(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    protected fun handleFinish() {
        supportFinishAfterTransition()
        //        if (!MainActivity.isLaunch) {
//            MainActivity.start(this);
//        }
    }

    /**
     * 4.4以上可用 evaluateJavascript 效率高
     */
     fun loadJs(jsString: String?) {
        runOnUiThread(Runnable {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView?.evaluateJavascript(jsString, null)
            } else {
                webView?.loadUrl(jsString)
            }
        })
    }

    /**
     * 长按事件处理
     */
    private fun handleLongImage(): Boolean {
        val hitTestResult: HitTestResult? = webView?.hitTestResult
        // 如果是图片类型或者是带有图片链接的类型
        if (hitTestResult?.type == HitTestResult.IMAGE_TYPE ||
                hitTestResult?.type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // 弹出保存图片的对话框
//            new AlertDialog.Builder(WebViewActivity.this)
//                    .setItems(new String[]{"查看大图", "保存图片到相册"}, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String picUrl = hitTestResult.getExtra();
//                            //获取图片
//                            Log.e("picUrl","picUrl: "+ picUrl);
//                            switch (which) {
//                                case 0:
//                                    break;
//                                case 1:
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                    })
//                    .show();
            return true
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //全屏播放退出全屏
            if (mWebChromeClient!!.inCustomView()) {
                hideCustomView()
                return true
            } else {
                finish()
            }
        }
        return false
    }
}