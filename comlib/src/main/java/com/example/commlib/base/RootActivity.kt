package com.example.commlib.base

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.commlib.R
import com.example.commlib.api.ConfigApi
import com.example.commlib.base.mvvm.BaseMvvmRecyclerAdapter
import com.example.commlib.listener.Listener
import com.example.commlib.utils.ButtonUtils
import com.example.commlib.utils.CommUtils
import com.example.commlib.utils.ToastUtils
import com.example.commlib.utils.permission.PermissionsUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import kotlin.reflect.KClass

/**
 * 根activity
 * Created by yzh on 2020/8/24 10:47.
 */
open class RootActivity: RxAppCompatActivity() {
//    @JvmField
//    var mContext: RxAppCompatActivity? = null

    val mContext: RxAppCompatActivity by lazy {
        this@RootActivity
    }

    companion object {
        private const val ACCESS_FINE_LOCATION_CODE=2//申请获得定位权限
        private const val ACTION_LOCATION_SOURCE_SETTINGS_CODE=3//打开GPS设置界面
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mConfiguration: Configuration=resources.configuration//获取设置的配置信息
        val orientation=mConfiguration.orientation//获取屏幕方向
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //   DensityUtil.setDensity(getApplication(), this,600);   根据自己项目设计图去修改值--我这里先屏蔽
        } else {
            //  DensityUtil.setDensity(getApplication(), this,960);
        }
       // mContext = this
    }

    private fun isOpenGPS(){
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // 转到手机设置界面，用户设置GPS
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, ACTION_LOCATION_SOURCE_SETTINGS_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtils.instance.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
        when(requestCode){
            ACCESS_FINE_LOCATION_CODE ->//定位权限   加设置GPS
                //定位权限   加设置GPS
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请权限可获得位置权限成功了
                    isOpenGPS()
                } else {
                    ToastUtils.showShort("申请失败,可能导致定位不准确")
                }
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //设置相应的  设计图  dp  比率
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //"横屏"
            // DensityUtil.setDensity(getApplication(), this,960);
        } else {
            // "竖屏"
            // DensityUtil.setDensity(getApplication(), this,600);
        }
    }

    //************************************** Activity跳转(兼容4.4) **************************************//
    @JvmOverloads
    fun startActivity(clz: Class<*>, bundle: Bundle? = null){
        if (ButtonUtils.isFastDoubleClick) {
            return
        }
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }



    /**
     * Activity跳转(带动画)
     *
     * @param clz 要跳转的Activity的类名
     */
    @JvmOverloads
    fun startActivityAnimation(clz: Class<*>, bundle: Bundle? = null) {
        if (ButtonUtils.isFastDoubleClick) {
            return
        }
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(Intent(this, clz), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(clz)
        }
    }

    /**
     * Activity跳转(共享元素动画,带Bundle数据)
     * @param clz Class<*>
     * @param view View
     * @param shareView String
     * @param bundle Bundle?
     */
    @JvmOverloads
    fun startActivityAnimation(clz: Class<*>, view: View, shareView: String, bundle: Bundle? = null){
        val intent=Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view, shareView).toBundle())
        } else {
            startActivity(intent)
        }
    }


    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    @JvmOverloads
    fun startActivityForResult(cls: Class<*>?, bundle: Bundle? = null, requestCode: Int) {
        if (ButtonUtils.isFastDoubleClick) {
            return
        }
        val intent = Intent()
        intent.setClass(this, cls!!)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }


    /**
     * 有动画的Finish掉界面
     */
    fun animationFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 通过 Fragment.class.getCanonicalName()获取
     * @param bundle
     */
    @JvmOverloads
    fun startContainerActivity(canonicalName: String?, bundle: Bundle? = null) {
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }
    //************************************** Activity跳转 **************************************//

    /**
     * 8.0需要校验安装未知源权限
     */
    fun canInstallAPK(listener: Listener) {
        var hasInstallPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hasInstallPermission = packageManager.canRequestPackageInstalls()
        }
        if (hasInstallPermission) {
            //去下载安装应用
            listener.onResult()
        } else {
            //跳转至“安装未知应用”权限界面，引导用户开启权限，可以在onActivityResult中接收权限的开启结果
            this.listener = listener
            showDialogBysure("应用安装", "更新app需要您开启安装权限", object : Listener {
                override fun onResult() {
                    val packageURI = Uri.parse("package:$packageName")
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                    startActivityForResult(intent, 0x33)
                }
            })?.setCancelable(true)
        }
    }

    var listener: Listener? = null

    //接收“安装未知应用”权限的开启结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x33) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && resultCode == RESULT_OK) {
                listener!!.onResult()
                listener = null
            }
        }
    }

    /**
     * 只有确定按钮的简化弹窗
     * @param title
     * @param msg
     * @param listener
     * @return
     */
    fun showDialogBysure(title: String?, msg: String, listener: Listener?): Dialog? {
        return CommUtils.showDialog(mContext, title, msg, "确定", null, listener, null)
    }

    /**
     * 为textview 设值，避免空值情况
     *
     * @param tv
     * @param str
     */
    fun setTextValues(tv: TextView?, str: String?) {
        if (tv != null && !TextUtils.isEmpty(str)) {
            tv.text = str
        }
    }

    fun setTextValues(tv: TextView?, @StringRes id: Int) {
        val str = getString(id)
        if (tv != null && !TextUtils.isEmpty(str)) {
            tv.text = str
        }
    }

    /**
     * 获取
     * @param colorId
     * @return
     */
    fun getColors(colorId: Int): Int {
        return ContextCompat.getColor(this, colorId)
    }

    fun getView(@LayoutRes layoutId: Int): View? {
        return LayoutInflater.from(mContext).inflate(layoutId, null)
    }

//    fun <T> setFooterView(list:List<T>,adapter: BaseRecyclerAdapters<*>,refreshLayout: SmartRefreshLayout){
//        adapter.recyclerView.adapter = adapter //切换设置FooterView，需要重新setAdapter,不然会报错
//    }

    fun <T> showEmptyView(list: List<T>, adapter: BaseMvvmRecyclerAdapter<*>?, mRefresh: SmartRefreshLayout?, content: String){
        if(list.isNullOrEmpty()){
            adapter?.emptyView=getEmptyView(content)
            mRefresh?.setEnableLoadMore(false)
            ConfigApi.EMPTY_VIEW = true
        }else{
            mRefresh?.setEnableLoadMore(true)
            ConfigApi.EMPTY_VIEW = false
        }
        mRefresh?.finishRefresh()
        mRefresh?.finishLoadMore()
    }



    private var emptyView: View? = null
    private fun getEmptyView(content: String): View? {
        if (emptyView == null) {
            emptyView = getView(R.layout.empty_view)
            emptyView?.setOnClickListener{ ToastUtils.showShort("点击emptyView刷新不够优雅，直接下拉emptyView刷新吧") }
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            emptyView?.layoutParams = lp
        }
        CommUtils.setTextValues(emptyView?.findViewById(R.id.tv_empty), content)
        return emptyView
    }
}