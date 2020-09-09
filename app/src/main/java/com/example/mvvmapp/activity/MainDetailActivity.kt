package com.example.mvvmapp.activity

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseActivity
import com.example.commlib.downloadapk.DownloadAPk
import com.example.commlib.downloadapk.DownloadAPk.DownLoadListener
import com.example.commlib.listener.Listener
import com.example.commlib.rx.RxBus
import com.example.commlib.rx.RxBusCode
import com.example.commlib.utils.*

import com.example.commlib.utils.permission.PermissionsUtil
import com.example.commlib.utils.permission.PermissionsUtils
import com.example.commlib.utils.permission.PermissionsUtils.IPermissionsResult
import com.example.mvvmapp.R
import com.example.mvvmapp.databinding.ActivityMainDetailBinding
import com.example.mvvmapp.databinding.TitleLayoutBinding
import com.example.mvvmapp.viewmodel.MainDetialViewModel

/**
 * @Description: banner 详情
 * @Author: yzh
 * @CreateDate: 2019/11/19 16:01
 */
class MainDetailActivity constructor() : BaseActivity<ActivityMainDetailBinding?, MainDetialViewModel?>() {

    private var mTitleLayoutBinding: TitleLayoutBinding? = null
    override val layoutId: Int
         get() {
            return R.layout.activity_main_detail
        }

    public override fun initViewObservable() {
        // mViewModel.downLoadEvent.observe(this,o -> download(o));
        mViewModel?.downLoadEvent?.observe(this,observer =  Observer{ url -> download(url) })

        mViewModel?.permissionsEvent?.observe(this,Observer {
            ToastUtils.showShort("权限申请")
            PermissionsUtils.instance.chekPermissions(this,PermissionsUtil.PERMISSION_CAMERA,object:IPermissionsResult{
                override fun passPermissons() {
                    ToastUtils.showShort("获取相机成功")
                }
                override fun forbitPermissons() {
                    ToastUtils.showShort("您拒绝了存储权限")
                }
            })
        })
        mViewModel?.errorEvent?.observe(this, Observer {s-> throw NullPointerException(s)  })
    }

    override fun initView() {
        StatusBarUtil.setColorNoTranslucent(mContext, getColors(R.color.colorAccent))
        // BarUtils.addMarginTopEqualStatusBarHeight(mBinding.rootLayout);
        BarUtils.setStatusBarLightMode(mContext, true)
        //示例如何动态的添加一个BindingView
        mTitleLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.title_layout, mBinding?.topLayout, false)
        mBinding?.topLayout?.addView(mTitleLayoutBinding?.root)
        mTitleLayoutBinding?.titleBack?.setOnClickListener(View.OnClickListener {finish()})

        if (mViewModel?.mBannerBean != null) {
            setTextValues(mTitleLayoutBinding?.titleText, mViewModel?.mBannerBean?.title)
        }

        //点击工具类使用
        ClickUtils.applyPressedViewScale(mBinding?.btn1, mBinding?.btn2, mBinding?.btn7) //给view 添加缩放点击效果
        ClickUtils.applyPressedViewAlpha(mBinding?.primaryMessageDetailsImg, 0.6f) //给view 透明度点击效果
        ClickUtils.applyPressedBgAlpha(mBinding?.btn3, 0.5f) //给view 透明度背景点击效果
        ClickUtils.applyPressedBgAlpha(mBinding?.btn4, 0.3f)
        ClickUtils.applyPressedBgDark(mBinding?.btn5) //给view 背景变暗点击效果
        ClickUtils.applyPressedBgDark(mBinding?.btn6)
        mBinding?.primaryMessageDetailsImg?.setOnClickListener(object : ClickUtils.OnMultiClickListener(5) {
            override fun onTriggerClick(v: View?) {
                ToastUtils.showShort("指定时间内连续点击5次触发事件")
            }

            override fun onBeforeTriggerClick(v: View?, count: Int) {
                ToastUtils.showShort(count.toString())
            }
        })
    }

    private fun download(url: String?) {
        CommUtils.showDialog(mContext, "提示", "测试更新apk", "确定", "取消", object : Listener {
                override fun onResult() {
                    PermissionsUtils.instance
                        .chekPermissions(this@MainDetailActivity, PermissionsUtil.PERMISSION_FILE, object : IPermissionsResult {
                            override fun passPermissons() {
                                canInstallAPK(
                                    object : Listener {
                                        override fun onResult() {
                                            DownloadAPk.getInstance()?.downApk(mContext,url, object : DownLoadListener {
                                                override fun onProgressUpdate(progress: Int) {
                                                    mViewModel?.mBannerBean?.progressValue?.set(progress)
                                                }
                                                override fun finish(filePath: String?) {
                                                    ALog.d(filePath)
                                                }
                                            })
                                        }
                                    }
                                )
                            }
                            override fun forbitPermissons() {
                                ToastUtils.showShort("您拒绝了存储权限，将无法下载更新")
                            }
                        })
                }
            }, null)
    }

    override fun onDestroy() {
        RxBus.instance.post(RxBusCode.TYPE_0, "MainDetailActivity")
        super.onDestroy()
    }
}