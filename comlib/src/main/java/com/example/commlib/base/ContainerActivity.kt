package com.example.commlib.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.commlib.R
import com.example.commlib.base.mvvm.BaseActivity
import com.example.commlib.base.mvvm.BaseViewModel
import java.lang.ref.WeakReference

/**
 *  一个公用来显示 Fragment的Activity
 * 一些普通界面只需要编写Fragment,使用此Activity显示,这样就不需要每个界面都在AndroidManifest中注册一遍
 * Created by yzh on 2019/8/24 15:28.
 */
class ContainerActivity : BaseActivity<ViewDataBinding, BaseViewModel>(){
    companion object{
        const val FRAGMENT_TAG:String="content_fragment_tag"
        const val FRAGMENT = "fragment"
        const val BUNDLE = "bundle"
    }
    var mFragment: WeakReference<Fragment>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        val fm: FragmentManager= supportFragmentManager
        var fragment:Fragment?=null
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG)
        }

        if(fragment==null){
            fragment = initFromIntent(intent)
        }
        val trans:FragmentTransaction=supportFragmentManager.beginTransaction()
        trans.replace(R.id.contentLayout, fragment!!)
        trans.commitAllowingStateLoss()
        mFragment = WeakReference(fragment)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mFragment?.get()?.let { supportFragmentManager.putFragment(outState, FRAGMENT_TAG, it) }
        //getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, mFragment.get());
    }

    private fun initFromIntent(data: Intent?): Fragment? {
        if (data == null) { throw RuntimeException("you must provide a page info to display")}

        try {
            val fragmentName = data.getStringExtra(FRAGMENT)

            require(!fragmentName.isNullOrEmpty()) { "can not find page fragmentName" }
            //require(执行条件) 等同于 throw new IllegalArgumentException("can not find page fragmentName");

            val fragmentClass:Class<*> = Class.forName(fragmentName)
            val fragment:Fragment= fragmentClass.newInstance() as Fragment
            val args = data.getBundleExtra(BUNDLE)
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        throw java.lang.RuntimeException("fragment initialization failed!!")
    }

    override fun onBackPressed() {
/*        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment instanceof BaseFragment) {
        }*/
        super.onBackPressed()
    }

    override val layoutId: Int
        get() = 0

    override fun initViewObservable() {

    }

    override fun initView() {

    }


}