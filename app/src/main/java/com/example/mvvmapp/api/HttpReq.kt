package com.example.mvvmapp.api

import com.example.commlib.api.RetrofitFactory
import com.example.commlib.bean.ResultBean
import com.example.commlib.bean.ResultBeans
import com.example.mvvmapp.bean.HomeListBean
import com.example.mvvmapp.bean.WanAndroidBannerBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.set

/**
 * @Description: Http请求类
 * 基于原有的 RetrofitFactory.getInstance()请求类 在封装的一层。
 * @Author: yzh
 * @CreateDate: 2019/10/28 9:57
 */
class HttpReq {

/*    companion object {
        @Volatile private var mInstance: HttpReq? = null
        val instance: HttpReq?
            get() {
                if (mInstance == null) {
                    synchronized(HttpReq::class.java) {
                        if (mInstance == null) {
                            mInstance = HttpReq()
                        }
                    }
                }
                return mInstance
            }
    }*/

/*    companion object {
        @Volatile private var instance: HttpReq? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: HttpReq().also { instance = it }
            }
    }*/

    //使用 by lazy 实现 单例
    companion object {
        val instance: HttpReq by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { HttpReq() }
    }

    /**
     * 返回 Observable<ResultBean></ResultBean><T>> 类型
    </T> */
    private fun <T> requests(beanObservable: Observable<ResultBean<T>>): Observable<ResultBean<T>> {
        return beanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        //onErrorReturn 的作用：请求失败了返回一个错误信息的对象,即使请求失败CommonObserver 里也会回调成功并返回一个错误码：ERROR_CODE的空对象
        // .onErrorReturn(t -> new ResultBean<>(ERROR_CODE, t.getMessage()));
    }

    private fun <T> requestss(beanObservable: Observable<ResultBeans<T>>): Observable<ResultBeans<T>> {
        return beanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        //  .onErrorReturn(t -> new ResultBeans<>(ERROR_CODE, t.getMessage()));
    }

    private fun <T> requestT(beanObservable: Observable<T>): Observable<T> {
        return beanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 返回 Observable<ResultBean> 类型
    </ResultBean> */
    private fun request(beanObservable: Observable<ResultBean<*>>): Observable<ResultBean<*>> {
        return beanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        //   .onErrorReturn(t -> new ResultBean<>(ERROR_CODE, t.getMessage()));
    }
    //    /**
    //     * 获取消息列表
    //     */
    //    public Observable<ResultBean<MessageBean>> getMessageList(String pageNo, String pageSize) {
    //        return RetrofitFactory.getInstance().create(SeniorApi.class)
    //                .getMessageList(pageNo,pageSize)
    //                .subscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .onErrorReturn(t -> new ResultBean<>(ERROR_CODE, t.getMessage()));
    //    }
    /**
     * 获取消息列表
     */
    //    public Observable<ResultBean<MessageBean>> getMessageList(String pageNo, String pageSize) {
    //        return requests(RetrofitFactory.getInstance().create(SeniorApi.class)
    //                .getMessageList(pageNo,pageSize)
    //        );
    //    }
    val wanBanner: Observable<ResultBeans<WanAndroidBannerBean>>
        get() = requestss(RetrofitFactory.get().create(AppApi::class.java).wanBanner)

    fun getHomeList(page: Int, cid: Int): Observable<ResultBean<HomeListBean>> {
        val map = HashMap<String, Int>()
        if (cid != -1) {
            map["cid"] = cid
        }
        return requests(RetrofitFactory.get().create(AppApi::class.java).getHomeList(page, map))
    }


}