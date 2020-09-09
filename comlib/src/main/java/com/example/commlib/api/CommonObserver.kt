package com.example.commlib.api

import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.bean.ResultBean
import com.example.commlib.bean.ResultBeans
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * ViewModel里 使用的CommonObserver
 * Created by yzh on 2020/8/19 14:55.
 */
abstract class CommonObserver<T>(private val mViewModel:BaseViewModel, private val isShowDialog:Boolean): Observer<T> {
    abstract fun success(data: T)
    abstract fun error(e: Throwable?)

    override fun onSubscribe(d: Disposable) {
        if (isShowDialog) {
            mViewModel.showDialog()
        }
        //调用addSubscribe()添加Disposable，请求与View周期同步
        mViewModel.addDisposable(d)
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        error(e)
        mViewModel.dismissDialog()
        ALog.e("onError===$e")
    }

    override fun onComplete() {
        mViewModel.dismissDialog()
    }

    override fun onNext(t: T) {
        success(t)
        mViewModel.dismissDialog();
        if(t is ResultBean<*>){
            if (t.errorCode == ConfigApi.ERROR_CODE) {
                ALog.e("请求失败========" + ConfigApi.ERROR_CODE + " " + t.errorMsg)
            }
        }else if (t is ResultBeans<*>) {
            if (t.errorCode == ConfigApi.ERROR_CODE) {
                ALog.e("请求失败~~~~~~~~" + ConfigApi.ERROR_CODE + " " + t.errorMsg)
            }
        }
    }
}