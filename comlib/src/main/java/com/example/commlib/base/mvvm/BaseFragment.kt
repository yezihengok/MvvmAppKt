package com.example.commlib.base.mvvm

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.commlib.BR
import com.example.commlib.listener.Listener
import com.example.commlib.utils.CommUtils
import com.example.commlib.weight.LoadDialog
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Created by yzh on 2020/8/21 14:56.
 */
 abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel>: RxFragment() {
    @JvmField
    var mBinding:V? = null
    @JvmField
    var mViewModel:VM?=null
    var mCompositeDisposable: CompositeDisposable?=null
    var dialog: LoadDialog?=null
    /**
     * Fragment是否可见状态
     */
    var isFragmentVisible:Boolean=false

    /**
     * 标志位，View是否已经初始化完成。
     */
    private var isPrepared = false

    /**
     * 是否第一次加载
     */
    private var isFirstLoad = true
    protected lateinit var mActivity: BaseActivity<*, *>

    protected abstract fun getLayoutId(inflater: LayoutInflater, container: ViewGroup?): Int
    protected abstract fun initViewObservable()
    protected abstract fun initView()

    fun initBundle(bundle: Bundle){
        mViewModel?.onBundle(bundle)
    }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      mActivity = activity as BaseActivity<*, *>
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val v:View=inflater.inflate(getLayoutId(inflater, container), container, false)
       initViewDataBinding(v)
       //页面间传值
       if (savedInstanceState != null) {
           initBundle(savedInstanceState)
       } else if (arguments != null) {
           initBundle(arguments!!)
       }
       // 若 viewpager 不设置 setOffscreenPageLimit 或设置数量不够
       // 销毁的Fragment onCreateView 每次都会执行(但实体类没有从内存销毁)
       isFirstLoad = true
       loadData()
       initView()
      return v

   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //界面初始化完成
        isPrepared = true
        loadData()
    }

    private fun initViewDataBinding(view: View?){
        if (view == null) {
            return
        }
        mBinding = DataBindingUtil.bind<V>(view)
        mViewModel = initViewModel()
        if (mViewModel == null) {
            val modelClass: Class<*>
            val type: Type? = javaClass.genericSuperclass
            if(type is ParameterizedType){
                modelClass = type.actualTypeArguments[1] as Class<*>
            }else{
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel::class.java
            }
            mViewModel = createViewModel(this, modelClass as Class<ViewModel>) as VM
//            mViewModel = (VM) createViewModel(this, modelClass);

            if (mViewModel != null) {
                mBinding?.setVariable(initVariableId(), mViewModel)
            }
            //页面事件监听的方法 用于ViewModel层转到View层的事件注册
            initViewObservable()
            registorLiveDataCallBack()
        }
    }

    /**
     * 创建ViewModel
     */
    open fun <VM : ViewModel?> createViewModel(fragment: Fragment?, cls: Class<VM>): VM? {
        return ViewModelProviders.of(fragment!!)[cls]
    }
//    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
//        return ViewModelProviders.of(fragment).get(cls);
//    }

    /**
     * 不使用类名传来的ViewModel，使用临时自定义的ViewModel
     * @return 重写此方法返回
     */
    open fun initViewModel(): VM? {
        return null
    }
    /**
     * 布局文件里的ViewModel默命名为viewModel（命名为其它请重写方法返回自定义的命名）
     */
    open fun initVariableId():Int{
        //因为commlib 是无法引用 主app 里的BR（com.example.mvvmapp.BR.viewModel）.所以我这里创建activity_binding.xml
        // 里命名了一个占位的viewModel以便通过编译期，实际运行时会被替换主app里的BR
        return BR.viewModel
    }

    fun addRxDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    fun unsubscribe(){
        mCompositeDisposable?.dispose()
        mCompositeDisposable?.clear()
        mCompositeDisposable=null
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel = null
        mBinding?.unbind()
        isPrepared = false
        unsubscribe()
    }

    /**
     * 注册(初始化)ViewModel与View的UI回调事件
     */
    private fun registorLiveDataCallBack(){
        //加载对话框显示
        mViewModel?.mUILiveData?.showDialogEvent?.observe(this, Observer { s: String? -> mActivity.showDialog(s) })
        //加载对话框消失
        mViewModel?.mUILiveData?.dismissDialogEvent?.observe(this, Observer { mActivity.dismissDialog() })
        //跳入新页面
        mViewModel?.mUILiveData?.startContainerActivityEvent?.observe(this, Observer {
            val clz = it?.get(BaseViewModel.FARGMENT_NAME) as Class<*>
            val bundle = it[BaseViewModel.BUNDLE] as Bundle
            mActivity.startActivity(clz, bundle)
        })
        //跳入新页面(共享元素动画)
        mViewModel?.mUILiveData?.startActivityAnimationEvent?.observe(this, Observer { it ->
            val caz = it?.get(BaseViewModel.CLASS) as Class<*>
            val bundle = it[BaseViewModel.BUNDLE] as Bundle
            val v = it[BaseViewModel.BUNDLE] as View
            val vName = it[BaseViewModel.VIEW_NAME] as String
            mActivity.startActivityAnimation(caz, v, vName, bundle)
        })
        //finish界面
        mViewModel?.mUILiveData?.finishEvent?.observe(this, observer = Observer { mActivity.finish() })//observer =  可以省略
        //关闭上一层
        mViewModel?.mUILiveData?.onBackPressedEvent?.observe(this, Observer { mActivity.onBackPressed() })
        //跳转一个共用显示fragment页面
        mViewModel?.mUILiveData?.startContainerActivityEvent?.observe(this, Observer { params: Map<String, *>? ->
            val canonicalName = params?.get(BaseViewModel.FARGMENT_NAME) as String
            val bundle = params[BaseViewModel.BUNDLE] as Bundle
            mActivity.startContainerActivity(canonicalName, bundle)
        })
    }

    fun getColors(colorId: Int):Int{
        return ContextCompat.getColor(mActivity, colorId)
    }

    var listener: Listener?=null
    fun canInstallAPK(listener: Listener){
        var hasInstallPermission:Boolean=true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hasInstallPermission = mActivity.packageManager.canRequestPackageInstalls()
        }
        if(hasInstallPermission){
            //去下载安装应用
            listener.onResult()
        }else{
            //跳转至“安装未知应用”权限界面，引导用户开启权限，可以在onActivityResult中接收权限的开启结果
            this.listener = listener

            showDialogBysure("应用安装", "更新app需要您开启安装权限", listener = object : Listener {
                override fun onResult() {
                    val packageURI: Uri = Uri.parse("package:" + mActivity.packageName)
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                    startActivityForResult(intent, 0x33)
                }
            }).setCancelable(true)

        }
    }

    fun showDialogBysure(title: String, msg: String, listener: Listener?): Dialog {
        return CommUtils.showDialog(mActivity, title, msg, "确定", null, listener, null)
    }

    //接收“安装未知应用”权限的开启结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x33) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && resultCode == Activity.RESULT_OK) {
                listener?.onResult()
                listener = null
            }
        }
    }

    /**
     * <pre>
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     * 一般用于PagerAdapter需要刷新各个子Fragment的场景
     * 不要new 新的 PagerAdapter 而采取reset数据的方式
     * 所以要求Fragment重新走initData方法
     * 故使用 [BaseFragment.setForceLoad]来让Fragment下次执行initData
    </pre> *
     */
    private var forceLoad = false

    /**
     * @param forceLoad 设置为true  lazyLoad()方法会执行多次 否则只会执行一次
     */
    open fun setForceLoad(forceLoad: Boolean) {
        this.forceLoad = forceLoad
    }
    /**
     * 这里执行懒加载的逻辑
     * 只会执行一次(如果不想只执行一次此方法): [BaseFragment.setForceLoad]
     */
    open fun lazyLoad() {
        Log.d("BaseMVVMFragment", "BaseMVVMFragment: lazyLoad")
    }

    private  fun loadData() {
        //判断View是否已经初始化完成 并且 fragment是可见 并且是第一次加载
        if (isPrepared && isFragmentVisible && isFirstLoad) {
            if (forceLoad || isFirstLoad) {
                forceLoad = false
                isFirstLoad = false
                lazyLoad()
            }
        }
    }

    open fun onInvisible() {
        isFragmentVisible = false
    }

    /**
     * 当界面可见的时候执行
     */
    open fun onVisible() {
        isFragmentVisible = true
        loadData()
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     *
     * 这个方法执行的时候onCreateView并不一定执行(切记)
     *
     * @param isVisibleToUser 是否显示出来了
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     * visible.
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            onVisible()
        } else {
            onInvisible()
        }
    }
}