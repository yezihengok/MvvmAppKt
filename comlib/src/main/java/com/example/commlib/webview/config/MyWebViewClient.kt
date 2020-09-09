package com.example.commlib.webview.config

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import com.blankj.ALog
import com.example.commlib.webview.WebTools

/**
 * Created by jingbin on 2016/11/17.
 * 监听网页链接:
 * - 根据标识:打电话、发短信、发邮件
 * - 进度条的显示
 * - 添加javascript监听
 * - 唤起京东，支付宝，微信原生App
 */
class MyWebViewClient constructor(private val mIWebPageView: IWebPageView) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if(!TextUtils.isEmpty(url)){
            mIWebPageView.onPageStarted(url?:"")
            val webSettings: WebSettings? = view?.settings
            Log.v("MyWebViewClient", "onPageStarted()-返回 userAgent: " + webSettings?.userAgentString)
        }
    }


    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        Log.e("jing", "----url:$url")
        if (TextUtils.isEmpty(url)) {
            return false
        }
        return mIWebPageView.isOpenThirdApp(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        // html加载完成之后，添加监听图片的点击js函数
        mIWebPageView.onPageFinished(view, url)
        super.onPageFinished(view, url)
    }

    //这个方法在 android 6.0一下会回调这个
    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        ALog.v("$errorCode---onReceivedError---$description")
        //有些网页回调了些错误，但依旧能打开网址：如net::ERR_CONNECTION_REFUSED -6  是不需要显示本地的错误页的
        if (errorCode != -6) {
            //用javascript隐藏系统定义的404页面信息
//            String data = "Page NO FOUND！";
//            view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
            view?.loadUrl(WebTools.DEFAULT_ERROR) //加载自定义错误页面html（注意会影响回退栈,失败了返回需要直接finish）
            mIWebPageView.onReceivedError(errorCode, description)
        }
    }

    //这个方法在 android 6.0以上会回调这个
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        //屏蔽系统默认的错误页面
        super.onReceivedError(view, request, error)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val errorCode: Int = error?.errorCode?:-1
            ALog.v(errorCode.toString() + "---onReceivedError---" + error?.description)
            if ((errorCode == 500) || (errorCode == 404) || (errorCode == -2)) {
                view?.loadUrl(WebTools.DEFAULT_ERROR) //显示本地失败的html（注意会影响回退栈,失败了返回需要直接finish）
                mIWebPageView.onReceivedError(errorCode, error?.description.toString())
            }
        }
    }

    // SSL Error. Failed to validate the certificate chain,error: java.security.cert.CertPathValidatorExcept
    public override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        ALog.e(error?.toString())
        //        if(isDbug){
//            //测试环境默认信任所有htpps的证书
        handler?.proceed()
        //        }else{
//            super.onReceivedSslError(view, handler, error);
//        }
    }

    // 视频全屏播放按返回页面被放大的问题
    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        super.onScaleChanged(view, oldScale, newScale)
        if (newScale - oldScale > 7) {
            view?.setInitialScale((oldScale / newScale * 100).toInt()) //异常放大，缩回去。
        }
    }

}