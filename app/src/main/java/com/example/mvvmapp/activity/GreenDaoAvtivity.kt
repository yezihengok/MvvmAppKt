package com.example.mvvmapp.activity

import android.graphics.Color
import android.text.TextUtils
import android.widget.ScrollView
import androidx.lifecycle.Observer
import com.example.commlib.base.mvvm.BaseActivity
import com.example.commlib.utils.EllipsizeUtils
import com.example.mvvmapp.R
import com.example.mvvmapp.databinding.ActivityGreendaoBinding
import com.example.mvvmapp.viewmodel.GreenDaoViewModel

/**
 * Created by yzh on 2020/6/9 15:01.
 */
class GreenDaoAvtivity constructor() : BaseActivity<ActivityGreendaoBinding?, GreenDaoViewModel?>() {
    override val layoutId: Int
         get() {
            return R.layout.activity_greendao
        }

    var content: String = "contentType"
    public override fun initViewObservable() {
        mViewModel!!.deleteEvent.observe(this, Observer{
            val txt: String = mBinding!!.editText.text.toString()
            val _id: Int = if (TextUtils.isEmpty(txt)) 0 else txt.toInt()
            mViewModel!!.delete(_id)
        })

        mViewModel!!.updateEvent.observe(this, Observer{
            val txt: String = mBinding!!.editText.text.toString()
            val _id: Int = if (TextUtils.isEmpty(txt)) 0 else txt.toInt()
            content = String.format("【我是修改后的内容%s】", _id)
            mViewModel!!.update(_id, content)
        })
        mViewModel!!.addEvent.observe(this, Observer{ mBinding!!.mScrollView.postDelayed(Runnable {
            mBinding!!.mScrollView.fullScroll(
                ScrollView.FOCUS_DOWN
            )
        }, 300) })

        mViewModel!!.contentChangeEvent.observe(this, Observer{
            EllipsizeUtils.ellipsizeAndHighlight(mBinding!!.tvContent, mBinding!!.tvContent.text.toString(), "_id", Color.BLUE, true, false)
            mBinding!!.tvContent.postDelayed(Runnable {
                //文字高亮工具类
                EllipsizeUtils.ellipsizeAndHighlight(
                    mBinding!!.tvContent, mBinding!!.tvContent.text.toString(), content,
                    Color.RED, true, false
                )
            }, 400)
        })
    }

    override fun initView() {}
}