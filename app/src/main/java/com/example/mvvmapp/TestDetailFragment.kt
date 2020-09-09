package com.example.mvvmapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseFragment
import com.example.commlib.download.DownLoadManager
import com.example.commlib.download.ProgressCallBack
import com.example.commlib.listener.Listener
import com.example.commlib.utils.CommUtils
import com.example.commlib.utils.CommUtils.showDialog
import com.example.commlib.utils.StatusBarUtil
import com.example.commlib.utils.ToastUtils
import com.example.commlib.utils.permission.PermissionsUtil
import com.example.commlib.utils.permission.PermissionsUtils
import com.example.commlib.utils.permission.PermissionsUtils.IPermissionsResult
import com.example.mvvmapp.databinding.ActivityMainDetailBinding
import com.example.mvvmapp.databinding.TitleLayoutBinding
import com.example.mvvmapp.viewmodel.MainDetialViewModel
import okhttp3.ResponseBody
import java.io.File

/**
 * 除了下载功能与MainDetailActivity一样  --只是为了 示例Fragment 使用
 * Author yzh Date 2019/12/9 13:57
 */
class TestDetailFragment constructor() : BaseFragment<ActivityMainDetailBinding, MainDetialViewModel>() {
    private var mTitleLayoutBinding: TitleLayoutBinding? = null

    override fun getLayoutId(inflater: LayoutInflater, container: ViewGroup?): Int {
        return R.layout.activity_main_detail
    }

    public override fun initViewObservable() {
        mViewModel?.downLoadEvent?.observe(this,observer = Observer {url: String? -> downloadApk(url)  })

        mViewModel?.downLoadEvent?.observe(this,observer = Observer { mActivity.showDialog() })

        mViewModel?.permissionsEvent?.observe(this, Observer {
            ToastUtils.showShort("权限申请")
            PermissionsUtils.instance.chekPermissions(mActivity, PermissionsUtil.PERMISSION_CAMERA,
                object : IPermissionsResult {
                    override fun passPermissons() {
                        ToastUtils.showShort("获取相机成功")
                    }

                    override fun forbitPermissons() {
                        ToastUtils.showShort("您拒绝了相机权限")
                    }
                })
        })
        mViewModel?.errorEvent?.observe(this, Observer { o: String? -> throw NullPointerException(o) })
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        StatusBarUtil.setColorNoTranslucent(mActivity, getColors(R.color.colorPrimary))
        //示例如何动态的添加一个BindingView
        mTitleLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.title_layout, mBinding?.topLayout, false)
        mBinding?.topLayout?.addView(mTitleLayoutBinding!!.root)
        mTitleLayoutBinding?.titleBack?.setOnClickListener(View.OnClickListener { mActivity.finish() })
        // mBinding.mUpdateButton.setOnClickListener(v -> download());

        // WanAndroidBannerBean mBannerBean=(WanAndroidBannerBean) getIntent().getSerializableExtra("bannerBean");
        //  mBinding.setBannerBean(mBannerBean);
        mTitleLayoutBinding?.titleText?.text = "示例Fragment使用"
        mViewModel?.downBtnName?.set("===使用Retrofit方式下载apk===")

    }

    fun downloadApk(url: String?) {
        showDialog(mActivity, "提示", "测试Retrofit方式更新apk"
                , "确定", "取消", object : Listener {
                override fun onResult() {
                    PermissionsUtils.instance.chekPermissions(
                        mActivity,
                        PermissionsUtil.PERMISSION_FILE,
                        object : IPermissionsResult {
                            override fun passPermissons() {
                                canInstallAPK(object : Listener {
                                    override fun onResult() {
                                        downLoad(url)
                                    }
                                })
                            }

                            override fun forbitPermissons() {
                                ToastUtils.showShort("您拒绝了存储权限，将无法下载更新")
                            }
                        })
                }
            }, null)
    }

    fun downLoad(url: String?) {
        val destFileDir: String = mActivity.cacheDir?.path.toString()
        val destFileName: String = "downlaod.apk"
        val progressDialog: ProgressDialog = ProgressDialog(mActivity)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setTitle("正在下载...")
        progressDialog.setCancelable(true)
        progressDialog.show()
        DownLoadManager.instance.load(url, object : ProgressCallBack<ResponseBody?>(destFileDir, destFileName) {
            override fun onStart() {
                super.onStart()
               ALog.i("onStart")
            }

            override fun onCompleted() {
                progressDialog.dismiss()
                mViewModel?.mBannerBean?.progressValue?.set(100)
            }

            override fun onSuccess(responseBody: ResponseBody?) {
                ToastUtils.showShort("文件下载完成！")
                val fileStr: String = destFileDir + File.separator + destFileName
                ALog.v(fileStr)
                startActivity(CommUtils.getInstallAppIntent(fileStr))
            }

            override fun progress(progress: Long, total: Long) {
                progressDialog.max = total.toInt()
                progressDialog.progress = progress.toInt()
                val mProgress  = (+progress * 100 / total)
                //进度显示2位小数：
                // double mProgress= ArithUtils.round((progress * 100 / (double) total),2);
                Log.v("DownloadAPk", mProgress.toString() + "%    总大小：" + total + "已下载大小：" + progress)
                mViewModel?.mBannerBean?.progressValue?.set(mProgress.toInt())
            }

            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                ToastUtils.showShort("文件下载失败！")
                progressDialog.dismiss()
            }

        })
    }


}