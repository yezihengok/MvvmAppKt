package com.example.commlib.base.mvvm

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.blankj.ALog
import com.example.commlib.event.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by yzh on 2020/8/21 9:50.
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    @kotlin.jvm.JvmField
    var mPage: ObservableInt = ObservableInt(1) //默认1开始

    private var mCompositeDisposable: CompositeDisposable? = null
        get() {
            if (field == null) {
                field = CompositeDisposable()
            }
            return field
        }

    var mUILiveData: UILiveData? = null
        get() {
            if (field == null) {
                field = UILiveData()
            }
            return field
        }

    /**
     * UILiveData 的作用 放一些常用的事件，减少去创建 重复的SingleLiveEvent()
     */
    class UILiveData : SingleLiveEvent<Any?>() {

        private fun <T> createLiveData(liveData: SingleLiveEvent<T>): SingleLiveEvent<T>? {
            var liveData: SingleLiveEvent<T>? = liveData
            if (liveData == null) {
                liveData = SingleLiveEvent()
            }
            return liveData
        }

//        public SingleLiveEvent<String> getShowDialogEvent() {
//            return showDialogEvent = createLiveData(showDialogEvent);
//        }
        val showDialogEvent: SingleLiveEvent<String> by lazy {
            SingleLiveEvent<String>()
        }
/*        var showDialogEvent: SingleLiveEvent<String>? = null
            get() {
                //also 函数内使用it代替本对象。返回值为本对象。
                return createLiveData(field).also { showDialogEvent = it }
            }*/

        val dismissDialogEvent: SingleLiveEvent<Void> by lazy {
            SingleLiveEvent<Void>()
        }

        //Activity跳转事件
        val startActivityEvent: SingleLiveEvent<Map<String, *>>by lazy {
            SingleLiveEvent<Map<String, *>>()
        }

        //Activity跳转(共享元素动画,带Bundle数据)事件
        val startActivityAnimationEvent: SingleLiveEvent<Map<String, *>>by lazy {
            SingleLiveEvent<Map<String, *>>()
        }
        val finishEvent: SingleLiveEvent<Void>by lazy {
            SingleLiveEvent<Void>()
        }

        val onBackPressedEvent:SingleLiveEvent<Void>by lazy {
            SingleLiveEvent<Void>()
        }

        val startContainerActivityEvent: SingleLiveEvent<Map<String, *>?>by lazy {
            SingleLiveEvent<Map<String, *>?>()
        }

        //普通通用的一般回调事件
        val commEvent: SingleLiveEvent<Any>by lazy {
            SingleLiveEvent<Any>()
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in Any?>) {
            super.observe(owner, observer)
        }

    }

    companion object ParameterType {
        var CLASS: String = "CLASS"
        var BUNDLE: String = "BUNDLE"
        var FARGMENT_NAME: String = "FARGMENT_NAME"

        //Activity跳转共享元素动画
        var VIEW: String = "VIEW"
        var VIEW_NAME: String = "VIEW_NAME"
    }


    //避免Rxjava内存泄漏,
    //1、可以将Rxjava 订阅的时间添至CompositeDisposable进来，Activity销毁时切断订阅
    //2、也可以用 RxLifeCycle 将Rxjava绑定Acitivty/Fragment,销毁时自动取消订阅
    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable?.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        if (mCompositeDisposable != null && !mCompositeDisposable!!.isDisposed) {
            mCompositeDisposable?.clear()
        }
    }

    fun dismissDialog() {
        mUILiveData?.dismissDialogEvent?.call()
    }

    //    fun showDialog(){
//        mUILiveData?.showDialogEvent?.value=null
//    }
    @JvmOverloads
    fun showDialog(msg: String? = null) {
        mUILiveData?.showDialogEvent?.value = msg
    }

    //@JvmOverloads, Kotlin会暴露多个重载方法,等同于:
//    fun startActivity1(clz:Class<*>){
//    }
//    fun startActivity1(clz:Class<*>,bundle: Bundle? ){
//    }
    //加了注解直接可以写成：
    @JvmOverloads
    fun startActivity(clz: Class<*>, bundle: Bundle? = null) {
        val params: HashMap<String, Any> = HashMap()
        params[CLASS] = clz
        if (bundle != null) {
            params[BUNDLE] = bundle
        }
        mUILiveData?.startActivityEvent?.postValue(params)
    }

    /**
     * Activity跳转(共享元素动画,带Bundle数据)
     */
    @JvmOverloads
    fun startActivityAnimation(clz: Class<*>, view: View, shareName: String, bundle: Bundle? = null) {
        val params: HashMap<String, Any> = HashMap()
        params[CLASS] = clz
        params[VIEW] = view
        params[VIEW_NAME] = shareName
        if (bundle != null) {
            params[BUNDLE] = bundle
        }
        mUILiveData?.startActivityAnimationEvent?.postValue(params)
    }


    /**
     * 跳转显示一个fragment的公共页面
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle Bundle?
     */
    @JvmOverloads
    fun startContainerActivity(canonicalName:String ,bundle:Bundle?=null){
        val params:HashMap<String,Any> = HashMap()
        params[FARGMENT_NAME] = canonicalName
        if (bundle != null) {
            params[BUNDLE] = bundle
        }
        mUILiveData?.startContainerActivityEvent?.postValue(params)
    }

    /**
     * 请求成功后，设置下一次请求的分页
     * @param mList ObservableArrayList<*>
     * @param isRefresh Boolean 是否是下拉刷新
     */
    fun setPage(mList: ObservableArrayList<*>,isRefresh:Boolean){
        if(!mList.isNullOrEmpty()){
            if(isRefresh)mPage.set(2)else mPage.set(mPage.get()+1)
            ALog.i("下一次请求的分页数：${mPage.get()}")
        }
   }

    open abstract fun onBundle(bundle:Bundle?)


}