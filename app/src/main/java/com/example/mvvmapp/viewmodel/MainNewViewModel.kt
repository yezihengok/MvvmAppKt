package com.example.mvvmapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.blankj.ALog
import com.example.commlib.api.CommonObserver
import com.example.commlib.api.ConfigApi.ERROR_CODE
import com.example.commlib.base.mvvm.BaseMvvmRecyclerAdapter
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.bean.ResultBean
import com.example.commlib.bean.ResultBeans
import com.example.commlib.event.SingleLiveEvent
import com.example.commlib.utils.CommUtils.isListNotNull
import com.example.commlib.utils.ToastUtils
import com.example.commlib.webview.WebViewActivity
import com.example.mvvmapp.R
import com.example.mvvmapp.activity.MainDetailActivity
import com.example.mvvmapp.api.HttpReq
import com.example.mvvmapp.bean.ArticlesBean
import com.example.mvvmapp.bean.HomeListBean
import com.example.mvvmapp.bean.WanAndroidBannerBean
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function

/**
 *
 * Author: yzh
 * @CreateDate: 2019/11/16 11:58
 */
@SuppressLint("CheckResult")
class MainNewViewModel constructor(application: Application) : BaseViewModel(application) {
    //使用LiveData 可通知Activity去toast
    @kotlin.jvm.JvmField
    var toastEvent: SingleLiveEvent<String> = SingleLiveEvent()

    private val mList: ObservableArrayList<ArticlesBean> = ObservableArrayList()

    override fun onBundle(bundle: Bundle?) {
        TODO("Not yet implemented")
    }

    @kotlin.jvm.JvmField
    var mAdapter: BaseMvvmRecyclerAdapter<ArticlesBean> = object:BaseMvvmRecyclerAdapter<ArticlesBean>(R.layout.item_message, mList) {
            override fun convert(holder: BindingViewHolder, item: ArticlesBean, position: Int) {
                //ALog.i("position=======$position")
                holder.itemView.setOnClickListener { v: View? ->
                    //当然可以直接在这toast,这里示例：回调给activity去处理
                    //ToastUtils.showShort(item.getLink());
                    toastEvent.value = "试试点击banner"
                    WebViewActivity.loadUrl(item.link, null)
                }
                holder.itemView.setOnLongClickListener { v: View? ->
                    val bundle = Bundle()
                    bundle.putSerializable("bannerBean", null)
                    startActivity(MainDetailActivity::class.java, bundle)
                    true
                }
            }

 }


    //    在LiveData出现之前，一般状态分发我们使用EventBus或者RxJava，这些都很容易出现内存泄漏问题，而且需要我们手动管理生命周期。而LiveData则规避了这些问题，
    //    LiveData是一个持有Activity、Fragment生命周期的数据容器。当数据源发生变化的时候，通知它的观察者(相应的界面)更新UI界面。同时它只会通知处于Active状态的观察者更新界面，
    //    如果某个观察者的状态处于Paused或Destroyed时那么它将不会收到通知。所以不用担心内存泄漏问题。

    val wanAndroidBanner: MutableLiveData<List<WanAndroidBannerBean>?>
        get() {
            val data: MutableLiveData<List<WanAndroidBannerBean>?> = MutableLiveData()
            HttpReq.instance.wanBanner.subscribe(
                object : CommonObserver<ResultBeans<WanAndroidBannerBean>>(this@MainNewViewModel, false) {
                        override fun success(bean: ResultBeans<WanAndroidBannerBean>) {
                            data.value = bean.data
                        }

                        override fun error(e: Throwable?) {
                            data.value = null
                        }
                    })
            return data
        }

    @SuppressLint("CheckResult")
    fun getHomeList(cid: Int, isRefresh: Boolean): MutableLiveData<List<ArticlesBean>> {
        if (isRefresh) {
            // mPage = 1;
            mPage.set(1)

        }
        val data: MutableLiveData<List<ArticlesBean>> = MutableLiveData()
        val subscribe: Disposable = HttpReq.instance.getHomeList(mPage.get(), cid)
                .onErrorReturn(Function { throwable: Throwable -> ResultBean<HomeListBean>(ERROR_CODE, (throwable.message)!!) })
                .subscribe(Consumer { homeListBean: ResultBean<HomeListBean> ->  //可以使用CommonObserver弹窗
                    if (homeListBean.errorCode == ERROR_CODE) {
                        ToastUtils.showShort("接口请求失败了~~")
                    }
                    val articlesBeans: List<ArticlesBean>?
                    if (homeListBean.data != null) {
                        articlesBeans = homeListBean.data?.datas
                        if (isListNotNull(articlesBeans)) {
                            if (isRefresh) {
                                mList.clear()
                            }
                            //mList.addAll(articlesBeans!!)
                            articlesBeans?.let { mList.addAll(it) }
                        }
                        //  mAdapter.setNewData(mList);  list改变 无需调用刷新，已封装在BaseMvvmRecyclerAdapter
                        //  mAdapter.notifyDataSetChanged();
                    }
                    setPage(mList, isRefresh)
                    data.setValue(mList)
                })

        //没有用 CommonObserver 这里添加addDisposable
        addDisposable(subscribe)
        return data
    }


}