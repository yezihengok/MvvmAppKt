package com.example.mvvmapp.viewmodel

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.event.SingleLiveEvent
import com.example.commlib.listener.ClickListener
import com.example.commlib.webview.WebViewActivity.Companion.loadUrl
import com.example.mvvmapp.TestDetailFragment
import com.example.mvvmapp.activity.GreenDaoAvtivity
import com.example.mvvmapp.activity.TestWeightActivity
import com.example.mvvmapp.bean.WanAndroidBannerBean

/**
 * Author yzh Date 2019/12/6 11:32
 */
class MainDetialViewModel constructor(application: Application) : BaseViewModel(application) {
    private var bundle: Bundle? = null
  //  @kotlin.jvm.JvmField
    var mBannerBean: WanAndroidBannerBean? = null

    //权限点击事件 在onBindingClick里调用了call，
   // @kotlin.jvm.JvmField
    var permissionsEvent: SingleLiveEvent<Void> = SingleLiveEvent()
    var downLoadEvent: SingleLiveEvent<String> = SingleLiveEvent()
    var errorEvent: SingleLiveEvent<String> = SingleLiveEvent()
  //  @kotlin.jvm.JvmField
    var downBtnName: ObservableField<String?> = ObservableField("点击体验下载的乐趣")

    override fun onBundle(bundle: Bundle?) {
        this.bundle = bundle
        if (bundle != null) {
            mBannerBean = bundle.getSerializable("bannerBean") as WanAndroidBannerBean?
            ALog.w("示例ViewModel获取activity的传值:" + (if (mBannerBean != null) mBannerBean.toString() else null))
        }
    }

    //闪退点击事件
  //  @kotlin.jvm.JvmField
    var errorClick: ClickListener = object : ClickListener {
        override fun onResult(v: View) {
            errorEvent.value = "哟哟哟，项目又报空指针了呢"
        }
    }

    //下载
  //  @kotlin.jvm.JvmField
    var downloadClick: ClickListener = object : ClickListener {
        override fun onResult(v: View) {
            downLoadEvent.value = "http://s.duapps.com/apks/own/ESFileExplorer-V4.2.1.7.apk"
        }
    }

    //跳转测试Fragment
  //  @kotlin.jvm.JvmField
    var fragmentClick: ClickListener = object : ClickListener {
        override fun onResult(v: View) {
            startContainerActivity((TestDetailFragment::class.java.canonicalName)?:"canonicalName不可能为空", bundle)
        }
    }

    //跳转网页
    fun toWebView() {
        // WebViewActivity.loadUrl("http://www.baidu.com",null);
        loadUrl("http://www.baidu.commmmmmmm", null)
    }

    //跳转
    fun toTestWeight() {
        startActivity(TestWeightActivity::class.java)
    }

    //跳转
    fun toGreenDaoAvtivity() {
        startActivity(GreenDaoAvtivity::class.java)
    }
}