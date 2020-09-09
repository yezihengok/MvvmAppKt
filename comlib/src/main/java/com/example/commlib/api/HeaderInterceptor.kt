package com.example.commlib.api

import android.util.Log
import com.blankj.ALog
import com.example.commlib.BuildConfig
import com.example.commlib.utils.AppUtils
import com.example.commlib.utils.CheckNetwork
import com.example.commlib.utils.DeviceUtils
import com.example.commlib.utils.ToastUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

/**
 * Created by yzh on 2020/8/19 15:18.
 */
class HeaderInterceptor : Interceptor {
    companion object {
        /**
         * 用于转换 Request 的 body 字符串
         */
        private val BUFFER: Buffer = Buffer()

        /**
         * 用于格式化输出网络请求
         */
        private const val OUTPUT = "%1\$s\n-\n%2\$s\n-\n%3\$s"
        private const val TAG = "HeaderInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var builder: Request.Builder = chain.request().newBuilder()
        // builder.addHeader("token", SpUtil.getInstance().getString(SpKey.KEY_TOKEN));
        builder.addHeader("App-Version", AppUtils.getAppVersionCode().toString())
        builder.addHeader("Model", DeviceUtils.getDevice())
        builder.addHeader("Content-Type", "application/json;charset=UTF-8")
        builder.addHeader("Accept", "application/json;versions=1")
        if (CheckNetwork.isNetworkConnected()) {
            val maxAge = 60
            builder.addHeader("Cache-Control", "public, max-age=$maxAge")
        } else {
            val maxStale = 60 * 60 * 24 * 28
            builder.addHeader("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
        }
        val request: Request = builder.build()
        val startTime = System.currentTimeMillis()
        var response: Response = chain.proceed(request)
        val endTime = System.currentTimeMillis()
        val responseBuilder: Response.Builder = response.newBuilder()
        response = responseBuilder.build()

        //外面已经添加了addInterceptor 日志打印
        //  response = printRequestAndResponse(request, response, endTime - startTime);
        if (!CheckNetwork.isNetworkConnected()) {
            ToastUtils.showShortSafe("没有网络连接~", 2500)
            ALog.e("没有网络连接~~~~~~~~")
        }
        return response
    }


    /**
     * 将 Request 和 Response 的大致内容输出
     *
     * @param request  Request
     * @param response Response
     * @param time     请求时间
     * @return 返回 Response，因为 Response 的 body 只能读取一次，所以读取打印之后需要再塞回去生成新的 Response
     */
    private fun printRequestAndResponse(
        request: Request,
        response: Response,
        time: Long
    ): Response? {
        var response = response
        if (!CheckNetwork.isNetworkConnected()) {
            ToastUtils.showShortSafe("没有网络连接~")
        }
        var requestBody = ""
        var responseBody = ""
        try {
            if (request.body() != null) {
                request.body()!!.writeTo(BUFFER)
                requestBody = BUFFER.readUtf8()
            }
            val body = response.body()
            if (body != null) {
                responseBody = unicodeToString(body.string())
                response = response.newBuilder()
                    .body(ResponseBody.create(body.contentType(), responseBody)).build()
            }
        } catch (e: IOException) {
            ALog.e(TAG, "is in trycatch")
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        Log.w(
            TAG,
            ">>>>>>> Http Request Start >>>>>>>"
        )
        ALog.w(
            TAG, String.format(
                OUTPUT,
                time.toString() + "ms",
                """
                    ${request.url()}
                    ${request.headers()}
                    $requestBody
                    """.trimIndent(),
                """
                    ${response.headers()}
                    $responseBody
                    """.trimIndent()
            )
        )
        Log.w(
            TAG,
            "<<<<<<<  Http Request End  <<<<<<<"
        )
        return response
    }

    /**
     * Unicode 转成字符串
     *
     * @param str 输入
     * @return 返回字符串
     */
    fun unicodeToString(str: String): String {
        var str = str
        val pattern =
            Pattern.compile("(\\\\u(\\p{XDigit}{4}))")
        val matcher = pattern.matcher(str)
        var ch: Char
        while (matcher.find()) {
            ch = matcher.group(2).toInt(16).toChar()
            str = str.replace(matcher.group(1), ch.toString() + "")
        }
        return str
    }

    private fun addHeaders(
        builder: Request.Builder,
        headers: HashMap<String, String>
    ) {
        for ((key, value) in headers) {
            builder.addHeader(key, value)
            ALog.w(
                TAG,
                "key=" + key + "value=" + value
            )
        }
    }
}