package com.example.commlib.webview

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.blankj.ALog
import com.example.commlib.R
import com.example.commlib.api.App
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.databinding.ActivityWebviewBinding
import com.example.commlib.utils.StatusBarUtil
import com.example.commlib.webview.WebViewActivity
import com.example.commlib.webview.config.WebProgress

/**
 * 公用展示的WebView的Activity
 * Created by yzh on 2019/12/11.
 */
class WebViewActivity : BaseWebAcivity<ActivityWebviewBinding?, BaseViewModel?>() {
    // 网页链接
    private var mUrl: String? = ""

    // 可滚动的title 使用简单 没有渐变效果，文字两旁有阴影
    private var mTitleToolBar: Toolbar? = null
    private var mTitle: String? = null
    var type: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_webview);
    }


    override val layoutId: Int
        get() = R.layout.activity_webview


    public override fun initViewObservable() {}
    override fun initView() {
        mUrl = intent.getStringExtra("mUrl")
        mTitle = intent.getStringExtra("mTitle")
        type = intent.getIntExtra("type", 0)
        initTitle()
        initWebView()
        handleLoadUrl()
        getDataFromBrowser(intent)
    }

    private fun handleLoadUrl() {
        if (!TextUtils.isEmpty(mUrl) && mUrl?.endsWith("mp4")!! && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)) {
            webView?.loadData(WebTools.getVideoHtmlBody(mUrl), "text/html", "UTF-8")
        } else {
            webView?.loadUrl(mUrl)
        }
    }

    @Suppress("MISSING_DEPENDENCY_CLASS")
    private fun initTitle() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorAccent), 0)
        mProgressBar = mBinding?.pbProgress

        mProgressBar?.setColor(ContextCompat.getColor(this, R.color.ui_blue))
        mProgressBar?.show()
        webView = mBinding?.webviewDetail
        initToolBar()
//        webView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) ->
//            scrollChangeHeader(scrollY,titleLayout)
//        );
    }

    private fun initToolBar() {
        mTitleToolBar = mBinding?.commonToolbar
        setSupportActionBar(mTitleToolBar)
        val actionBar: ActionBar? = supportActionBar
        //去除默认Title显示
        actionBar?.setDisplayShowTitleEnabled(false)
        mTitleToolBar?.overflowIcon = ContextCompat.getDrawable(this, R.drawable.actionbar_more)
        mBinding?.commonTitle?.postDelayed(Runnable { mBinding?.commonTitle?.isSelected = true }, 1000)
        setTitle(mTitle)
        mTitleToolBar?.setNavigationOnClickListener(View.OnClickListener { back() })
    }

    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getDataFromBrowser(intent)
    }

    public override fun setTitle(mTitle: String?) {
        mBinding?.commonTitle?.text = mTitle
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView?.resumeTimers()
        // 设置为横屏
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDestroy() {
        if (videoFullView != null) {
            videoFullView?.removeAllViews()
            videoFullView = null
        }
        if (webView != null) {
            val parent: ViewGroup? = webView?.parent as ViewGroup?
            parent?.removeView(webView)
            webView?.removeAllViews()
            webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView?.stopLoading()
            webView?.webChromeClient = null
            webView?.webViewClient = null
            webView?.destroy()
            webView = null
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_webview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) { // 返回键
            handleFinish()
        } else if (itemId == R.id.actionbar_share) { // 分享到
            val shareText: String = webView?.title + webView?.url
            WebTools.share(this@WebViewActivity, shareText)
        } else if (itemId == R.id.actionbar_cope) { // 复制链接
            WebTools.copy(webView?.url)
            Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show()
        } else if (itemId == R.id.actionbar_open) { // 打开链接
            WebTools.openLink(this@WebViewActivity, webView!!.url)
        } else if (itemId == R.id.actionbar_webview_refresh) { // 刷新页面
            webView?.reload()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun startProgress(newProgress: Int) {
        super.startProgress(newProgress)
        ALog.i("newProgress:$newProgress")
    }

    companion object {
        /**
         * 打开网页:
         *
         * @param mUrl   要加载的网页url
         * @param mTitle 标题
         */
        @kotlin.jvm.JvmStatic
        fun loadUrl(mUrl: String?, mTitle: String?) {
            val intent: Intent = Intent(App.instance, WebViewActivity::class.java)
            intent.putExtra("mUrl", mUrl)
            intent.putExtra("mTitle", mTitle ?: "加载中...")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            App.instance.startActivity(intent)
        }
    }



}