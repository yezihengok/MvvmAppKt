package com.example.commlib.webview

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.example.commlib.api.App.Companion.instance

object WebTools {
    /**
     * 网页加载失败时显示的本地默认网页
     */
    var DEFAULT_ERROR: String = "file:///android_asset/html/404.html"

    /**
     * 判断网络是否连通
     */
    fun isNetworkConnected(context: Context?): Boolean {
        return try {
            if (context != null) {
                val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val info: NetworkInfo? = cm.getActiveNetworkInfo()
                info != null && info.isConnected()
            } else {
                /**如果context为空，就返回false，表示网络未连接 */
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isWifiConnected(context: Context?): Boolean {
        if (context != null) {
            val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info: NetworkInfo? = cm.getActiveNetworkInfo()
            return info != null && (info.getType() == ConnectivityManager.TYPE_WIFI)
        } else {
            /**如果context为null就表示为未连接 */
            return false
        }
    }

    /**
     * 将 Android5.0以下手机不能直接打开mp4后缀的链接
     *
     * @param url 视频链接
     */
    fun getVideoHtmlBody(url: String?): String {
        return ("<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\">" +
                "<style type=\"text/css\" abt=\"234\"></style>" +
                "</head>" +
                "<body>" +
                "<video controls=\"\" autoplay=\"\" name=\"media\">" +
                "<source src=\"" + url + "\" type=\"video/mp4\">" +
                "</video>" +
                "</body>" +
                "</html>")
    }

    /**
     * 实现文本复制功能
     *
     * @param content 复制的文本
     */
    fun copy(content: String?) {
        if (!TextUtils.isEmpty(content)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard = instance?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = content
            } else {
                val clipboard = instance?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(content, content)
                clipboard.setPrimaryClip(clip)
            }
        }
    }

    /**
     * 使用浏览器打开链接
     */
    fun openLink(context: Context, content: String) {
        if (!TextUtils.isEmpty(content) && content.startsWith("http")) {
            val issuesUrl: Uri = Uri.parse(content)
            val intent: Intent = Intent(Intent.ACTION_VIEW, issuesUrl)
            context.startActivity(intent)
        }
    }

    /**
     * 分享
     */
    fun share(context: Context, extraText: String?) {
        val intent: Intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(intent, "分享"))
    }

    /**
     * 通过包名找应用,不需要权限
     */
    fun hasPackage(context: Context?, packageName: String?): Boolean {
        if (null == context || TextUtils.isEmpty(packageName)) {
            return false
        }
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_GIDS)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            // 抛出找不到的异常，说明该程序已经被卸载
            false
        }
    }

    /**
     * 处理三方链接
     * 网页里可能唤起其他的app
     */
    fun handleThirdApp(activity: Activity, backUrl: String?): Boolean {
        if (TextUtils.isEmpty(backUrl)){
            return false
        }
        /**http开头直接跳过 */
        if (backUrl?.startsWith("http")!!) {
            // 可能有提示下载Apk文件
            if (backUrl.contains(".apk")) {
                startActivity(activity, backUrl)
                return true
            }
            return false
        }
        var isJump: Boolean = true
        /**屏蔽以下应用唤起App，可根据需求 添加或取消 */
        if ((backUrl.startsWith("tbopen:") // 淘宝
                        //                        || backUrl.startsWith("openapp.jdmobile:")// 京东
                        //                        || backUrl.startsWith("jdmobile:")//京东
                        //                        || backUrl.startsWith("alipay:")// 支付宝
                        //                        || backUrl.startsWith("alipays:")//支付宝
                        || backUrl.startsWith("zhihu:") // 知乎
                        || backUrl.startsWith("vipshop:") //
                        || backUrl.startsWith("youku:") //优酷
                        || backUrl.startsWith("uclink:") // UC
                        || backUrl.startsWith("ucbrowser:") // UC
                        || backUrl.startsWith("newsapp:") //
                        || backUrl.startsWith("sinaweibo:") // 新浪微博
                        || backUrl.startsWith("suning:") //
                        || backUrl.startsWith("pinduoduo:") // 拼多多
                        //                  || backUrl.startsWith("baiduboxapp:")// 百度
                        || backUrl.startsWith("qtt:")) //
        ) {
            isJump = false
        }
        if (isJump) {
            startActivity(activity, backUrl)
        }
        return isJump
    }

    private fun startActivity(context: Context, url: String) {
        try {

            // 用于DeepLink测试
            if (url.startsWith("will://")) {
                val uri: Uri = Uri.parse(url)
                Log.e("---------scheme", uri.getScheme() + "；host: " + uri.getHost() + "；Id: " + uri.getPathSegments().get(0))
            }
            val intent: Intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val uri: Uri = Uri.parse(url)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}