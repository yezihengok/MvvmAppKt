package com.example.commlib.api

import android.content.Context
import com.blankj.ALog
import com.example.commlib.bean.ResultBean
import com.example.commlib.bean.ResultBeans
import com.example.commlib.weight.LoadDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 请求是否加载等待框
 * Created by yzh on 2020/8/19 14:35.
 */
abstract class CommObserver<T>(val context:Context, private val isShowDialog:Boolean):Observer<T> {
    protected var dialog: LoadDialog? = null
    init {
        if(isShowDialog&&dialog==null){
            createLoading()
        }
    }

    private fun createLoading() {
    }
    private fun dismiss() {
        dialog?.dismiss()
        dialog=null
    }

    abstract fun success(data:T)
    abstract fun error(e: Throwable?)

    override fun onSubscribe(d: Disposable) {
        if(isShowDialog){
            dialog?.show()
        }
    }
    override fun onError(e: Throwable) {
        e.printStackTrace()
        error(e)
        dismiss()
        ALog.e("onError===$e")
    }

    override fun onNext(t: T) {
        success(t)
        dismiss()
        if(t is ResultBean<*>){
            //val bean = t as ResultBean<*>
            if (t.errorCode == ConfigApi.ERROR_CODE) {
                ALog.e("请求失败========" + ConfigApi.ERROR_CODE + " " + t.errorMsg)
            }
        }else if (t is ResultBeans<*>) {
            val bean = t as ResultBeans<*>
            if (bean.errorCode == ConfigApi.ERROR_CODE) {
                ALog.e("请求失败~~~~~~~~" + ConfigApi.ERROR_CODE + " " + bean.errorMsg)
            }
        }

    }

    override fun onComplete() {
        dismiss()
    }

}