package com.example.mvvmapp.main

import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.Observer
import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseActivity
import com.example.commlib.rx.RxBus
import com.example.commlib.rx.RxBusCode
import com.example.commlib.utils.GlideImageLoader
import com.example.commlib.utils.StatusBarUtil
import com.example.commlib.utils.ToastUtils
import com.example.commlib.webview.WebViewActivity.Companion.loadUrl
import com.example.commlib.weight.banner.BannerConfig
import com.example.commlib.weight.banner.Transformer
import com.example.commlib.weight.banner.loader.OnBannerListener
import com.example.mvvmapp.BuildConfig
import com.example.mvvmapp.R
import com.example.mvvmapp.activity.MainDetailActivity
import com.example.mvvmapp.bean.ArticlesBean
import com.example.mvvmapp.bean.WanAndroidBannerBean
import com.example.mvvmapp.databinding.ActivityNewMainBinding
import com.example.mvvmapp.viewmodel.MainNewViewModel
import java.util.*

/**
 * ViewModelProviders.of 方式 初始化的ViewModel，ViewModel不持有context，LiveDada通知回调 不用担心管理内存泄漏问题  方式 实现的MVVM（推荐这种）
 */
class MainNewActivity() : BaseActivity<ActivityNewMainBinding?, MainNewViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTransparentForImageView(mContext, null)

        //示例 RxBus使用
        val subscribe = RxBus.instance.toObservable(RxBusCode.TYPE_0, String::class.java).subscribe {
                s: String -> ALog.i("返回值:$s")
        }
        addRxDisposable(subscribe)
    }

    override val layoutId: Int
        get() = R.layout.activity_new_main

    override fun initViewObservable() {
        //mViewModel.toastEvent.observe(this, s -> ToastUtils.showShort(s));
        mViewModel?.toastEvent?.observe(this, Observer {s->ToastUtils.showShort(s)  })
    }

    override fun initView() {
        initBanner()
        mBinding?.mRefreshLayout?.setOnRefreshListener {
            getHomeList(true)
            wanBanner
        }
        mBinding?.mRefreshLayout?.setOnLoadMoreListener { getHomeList(false) }
    }

    private fun initBanner() {

        //设置banner样式
        mBinding!!.include.mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) //设置banner样式
                .setImageLoader(GlideImageLoader()) //设置图片加载器
                //.setImages(images)
                .setBannerAnimation(Transformer.ZoomOutSlide) //.setBannerTitles(titles);//设置标题集合（当banner样式有显示title时）
                .isAutoPlay(true) //设置自动轮播，默认为true
                .setDelayTime(3000) //设置轮播时间
                .setIndicatorGravity(BannerConfig.LEFT) //设置指示器位置（当banner模式中有指示器时）
                .start() //banner设置方法全部调用完毕时最后调用
        wanBanner
    }

    private fun getHomeList(isRefresh: Boolean) {
        mViewModel?.getHomeList(-1, isRefresh)?.observe(this, Observer {
                articlesBeans: List<ArticlesBean> ->
            showEmptyView(articlesBeans, mViewModel?.mAdapter, mBinding?.mRefreshLayout, "数据空空如也~")
        })
    }

    private val wanBanner: Unit
        get() {
            mViewModel?.wanAndroidBanner?.observe(this, Observer { dataBeans ->
                mBinding?.mRefreshLayout?.autoRefresh(500)
                if (!dataBeans.isNullOrEmpty()) {
                    val images: MutableList<String?> = ArrayList()
                    val titles: MutableList<String?> = ArrayList()
                    for (articlesBean: WanAndroidBannerBean in dataBeans) {
                        images.add(articlesBean.imagePath)
                        titles.add(articlesBean.title)
                    }
                    mBinding?.include?.mBanner?.update(images, titles)
                        ?.setOnBannerListener(OnBannerListener { position: Int ->
                                ToastUtils.showShort(dataBeans[position].url)
                                val bundle: Bundle = Bundle()
                                bundle.putSerializable("bannerBean", dataBeans[position])
                                startActivity(MainDetailActivity::class.java, bundle)
                            })
                } else {
                    ToastUtils.showShort("获取banner失败~~")
                }
            })
        }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (BuildConfig.DEBUG) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    loadUrl("https://github.com/yezihengok", null)
                    return super.onKeyDown(keyCode, event)
                }
                KeyEvent.KEYCODE_VOLUME_UP -> return super.onKeyDown(keyCode, event)
                KeyEvent.KEYCODE_MENU -> return true
                KeyEvent.KEYCODE_BACK -> {
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}