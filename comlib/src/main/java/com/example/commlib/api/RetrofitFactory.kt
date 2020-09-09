package com.example.commlib.api

import android.util.Log
import com.blankj.ALog
import com.example.commlib.utils.CommUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Created by yzh on 2020/8/19 15:44.
 */
object RetrofitFactory {
     private val TIMEOUT=20

    /**
     * 初始化
     * @return
     */

    @JvmStatic
    fun get(): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient) // 设置请求的域名
            .baseUrl(ConfigApi.BASE_URL) // 设置解析转换工厂
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private var okHttpClient:OkHttpClient?=null
    get() {
        if(field==null){
            field = OkHttpClient.Builder() //设置禁止代理
                //  .proxy(null)
                .proxy(Proxy.NO_PROXY)
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS) //设置连接超时时间
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS) //设置读取超时时间
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS) //设置写入超时时间
                .addInterceptor(HeaderInterceptor()) //加header  he token   比如要加保密这些都可以从这里走
                //请求日志拦截打印
                .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                    if (CommUtils.isJson(message)) {
                        ALog.json(message)
                    } else {
                        Log.w("RetrofitFactory", message)
                    }
                })
                .setLevel(HttpLoggingInterceptor.Level.BODY)) //    .addInterceptor(new CaptureInfoInterceptor())//com.github.DingProg.NetworkCaptureSelf:library:v1.0.1 抓包工具，可屏蔽
                .build()
        }
        return field
    }


}