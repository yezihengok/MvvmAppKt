package com.example.commlib.base.mvvm

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.blankj.ALog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.commlib.R
import com.example.commlib.api.ConfigApi

/**
 * Created by yzh on 2020/8/22 9:40.
 */
abstract class BaseMvvmRecyclerAdapter<T> constructor(@LayoutRes layoutResId: Int, data: List<T>?): BaseQuickAdapter<T, BaseMvvmRecyclerAdapter.BindingViewHolder>(layoutResId, data) {

    //让list数据变更后自动notifyItemRangeChanged刷新
    private var mTObservableList: ObservableList<T>? = null
    companion object {
        var recyclerView: RecyclerView? = null
    }


    //init --初始化数据，基本等于 无参构造函数
    //不管是什么构造方法，先执行init模块逻辑，后执行构造方法的逻辑
    init {
        mTObservableList = if (data==null) ObservableArrayList() else data as ObservableList<T>
        mTObservableList?.addOnListChangedCallback(object : OnListChangedCallback<ObservableList<T>?>() {
            override fun onChanged(sender: ObservableList<T>?) {
                notifyDataSetChanged()
                ALog.e("onChanged()")
            }

            override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                notifyItemRangeChanged(positionStart, itemCount)
                ALog.e("onItemRangeChanged()")
            }

            override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                ALog.e("onItemRangeInserted()$emptyViewCount  ${ConfigApi.EMPTY_VIEW}")
                //踩坑提示：使用 quickadapter.setEmptyView 设置空布局后， 刷新又有了数据 必须调用mAdapter.setNewData(mList); 而不是调用notifyDataSetChanged()系列; 否则会报错
//                if(getEmptyViewCount()>0){ 不能用这个在这里判断,因为mTObservableList有值后getEmptyViewCount 会变成0
                if (ConfigApi.EMPTY_VIEW) {
                    setNewData(sender)
                } else {
                    notifyItemRangeInserted(positionStart, itemCount)
                }
            }

            override fun onItemRangeMoved(sender: ObservableList<T>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                for (i in 0 until itemCount) {
                    notifyItemMoved(fromPosition + i, toPosition + i)
                }
                ALog.e("onItemRangeMoved()")
            }

            override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                notifyItemRangeRemoved(positionStart, itemCount)
                ALog.e("onItemRangeRemoved()")
            }
        })
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        //在RecyclerView提供数据的时候调用
        Companion.recyclerView = recyclerView
    }
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Companion.recyclerView = null
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
        val binding: ViewDataBinding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false)
            ?: return super.getItemView(layoutResId, parent)
        val view:View=binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }

    override fun convert(helper: BindingViewHolder, item: T) {
        val binding:ViewDataBinding=helper.binding
        binding.setVariable(com.example.commlib.BR.itemBean, item)
        //BaseQuickAdapter position获取
        var position=helper.adapterPosition
        if (position == RecyclerView.NO_POSITION) {
            return
        }
        position -= this.headerLayoutCount
        convert(helper, item,position)
    }
    /**
     * 填充RecyclerView适配器的方法
     */
    abstract fun convert(holder: BindingViewHolder, item: T, position: Int)

    class BindingViewHolder constructor(view: View?) : BaseViewHolder(view) {
        val binding: ViewDataBinding
            get() = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as ViewDataBinding
    }

}