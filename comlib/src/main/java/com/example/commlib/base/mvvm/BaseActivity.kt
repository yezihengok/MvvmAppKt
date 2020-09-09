package com.example.commlib.base.mvvm

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.commlib.BR
import com.example.commlib.base.RootActivity
import com.example.commlib.utils.ClassUtil
import com.example.commlib.weight.LoadDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by yzh on 2020/8/20 15:34.
 */
 abstract class BaseActivity<V : ViewDataBinding?, VM : BaseViewModel?>: RootActivity() {
    //@JvmStatic 和@JvmFiled区别
//    1.@JvmField消除了变量的getter与setter方法
//    2.@JvmField修饰的变量不能是private属性的
//    3.@JvmStatic只能在object类或者伴生对象companion object中使用，而@JvmField没有这些限制
//    4.@JvmStatic一般用于修饰方法，使方法变成真正的静态方法；如果修饰变量不会消除变量的getter与setter方法，但会使getter与setter方法和变量都变成静态

   // @JvmField // 等于public
    var mBinding:V? = null

   // @JvmField
    var mViewModel:VM?=null//如果某个页面很简单不需要单独的ViewModel去展示。VM直接传BaseViewModel即可，mViewModel对象将不会被创建

    private var mCompositeDisposable:CompositeDisposable?=null
    var dialog: LoadDialog?=null
    /**
     * 初始化布局的id
     * @return 布局的id
     */
    protected abstract val layoutId:Int
    protected abstract fun initViewObservable()
    protected abstract fun initView()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewDataBinding()
        //页面间传值
        if (savedInstanceState != null) {
            initBundle(savedInstanceState)
        } else if (intent != null && intent.extras != null) {
            initBundle(intent.extras!!)
        }
        initView()
    }

    /**
     * 绑定mViewModel
     */
    open fun initViewDataBinding(){
        if(layoutId==0){
            return
        }
        mBinding = DataBindingUtil.setContentView<V>(this, layoutId)
        mViewModel=initMVVMViewModel()
        if (mViewModel == null) {
            createViewModel()
        }
        if (mViewModel != null) {
            mBinding!!.setVariable(initVariableId(), mViewModel)
            registorLiveDataCallBack()
            //页面事件监听的方法 用于ViewModel层转到View层的事件注册
            initViewObservable()
        }
    }

    /**
     * 不使用类名传来的ViewModel，使用临时自定义的ViewModel
     * @return 重写此方法返回
     */
    open fun initMVVMViewModel(): VM? {
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

    private fun initBundle(bundle: Bundle){
        mViewModel?.onBundle(bundle)
    }

    private fun createViewModel(){
        val viewModelClass: Class<VM>? = ClassUtil.getViewModel(this)
        if(viewModelClass!=null){
            mViewModel=ViewModelProviders.of(this).get(viewModelClass)
        }
    }

    /**
     * 注册(初始化)ViewModel与View的UI回调事件
     */
    private fun registorLiveDataCallBack(){
        //加载对话框显示
        mViewModel?.mUILiveData?.showDialogEvent?.observe(this, Observer { s: String? ->
            showDialog(s)
        })
        //加载对话框消失
        mViewModel?.mUILiveData?.dismissDialogEvent?.observe(this, Observer { dismissDialog() })
        //跳入新页面
        mViewModel?.mUILiveData?.startActivityEvent?.observe(this,observer = Observer {

            // tips: java里 通过包类名称字符串 强转成 类 用 Class
           // val clz = classStr as Class<*>
            //而kotlin里  直接强转得到的是KClass,因此这里用 Class.forName()方式
          //  val clz = Class.forName(classStr)

            val clz=it?.get(BaseViewModel.CLASS) as Class<*>
            val bundle:Bundle? = it[BaseViewModel.BUNDLE] as Bundle?
            startActivity(clz, bundle)
        })
        //跳入新页面(共享元素动画)
        mViewModel?.mUILiveData?.startActivityAnimationEvent?.observe(this, Observer { it ->
            val caz = it?.get(BaseViewModel.CLASS) as Class<*>
            val bundle = it[BaseViewModel.BUNDLE] as Bundle?
            val v = it[BaseViewModel.VIEW] as View
            val vName = it[BaseViewModel.VIEW_NAME] as String
            startActivityAnimation(caz, v, vName, bundle)
        })
        //finish界面
        mViewModel?.mUILiveData?.finishEvent?.observe(this, observer = Observer { finish() })//observer =  可以省略
        //关闭上一层
        mViewModel?.mUILiveData?.onBackPressedEvent?.observe(this, Observer { onBackPressed() })
        //跳转一个共用显示fragment页面
        mViewModel?.mUILiveData?.startContainerActivityEvent?.observe(
            this,
            Observer { params: Map<String, *>? ->
                val canonicalName = params?.get(BaseViewModel.FARGMENT_NAME) as String?
                val bundle = params?.get(BaseViewModel.BUNDLE) as Bundle?
                startContainerActivity(canonicalName, bundle)
            })
    }

    fun showDialog(msg: String? = "Loading...", vararg cancelAble: Boolean){
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
            return
        }
        dialog= if(cancelAble.isNotEmpty()){
            LoadDialog(mContext, msg!!, cancelAble[0])
        }else{
            LoadDialog(this, msg!!, false)
        }
        dialog?.show()
    }

    fun dismissDialog(){
        dialog?.dismiss()
        dialog=null
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
        unsubscribe()
    }
}