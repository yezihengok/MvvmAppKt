package com.example.commlib.download

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by goldze
 * 文件下载管理，封装一行代码实现下载
 */
class DownLoadManager private constructor() {
    //下载
    fun load(downUrl: String?, callBack: ProgressCallBack<ResponseBody?>) {
        retrofit.create(ApiService::class.java)
            .download(downUrl)
            .subscribeOn(Schedulers.io()) //请求网络 在调度者的io线程
            .observeOn(Schedulers.io()) //指定线程保存文件
            .doOnNext {
                    responseBody -> callBack.saveFile(responseBody)
            }
            .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
            .subscribe(DownLoadSubscriber(callBack))
    }

    private fun buildNetWork() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ProgressInterceptor())
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()
        retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("http://www.baidu.com")
            .build()
    }

    private interface ApiService {
        @Streaming
        @GET
        fun download(@Url url: String?): Observable<ResponseBody?>
    }

    inner class ProgressInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())
            return originalResponse.newBuilder()
                .body(ProgressResponseBody(originalResponse.body()))
                .build()
        }
    }

    companion object {
        val instance:DownLoadManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { DownLoadManager() }
//        var instance: DownLoadManager? = null
//            get() {
//                if (field == null) {
//                    synchronized(DownLoadManager::class.java) {
//                        if (field == null) {
//                            field = DownLoadManager()
//                        }
//                    }
//                }
//                return field
//            }
//            private set
        lateinit var retrofit: Retrofit
    }

    init {
        buildNetWork()
    }
}