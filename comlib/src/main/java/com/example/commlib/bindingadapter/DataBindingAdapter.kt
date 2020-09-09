package com.example.commlib.bindingadapter

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.commlib.base.mvvm.BaseMvvmRecyclerAdapter
import com.example.commlib.weight.recyclerview.DividerLine
import com.example.commlib.weight.recyclerview.DividerLine.LineDrawMode

/**
 * Created by yzh on 2020/8/20 9:08.
 */
object DataBindingAdapter {
    val TAG:String=DataBindingAdapter.javaClass.simpleName

    /**
     * 圆形图片
     *
     * @param img
     * @param path
     */
    @JvmStatic
    @BindingAdapter("circleImg")
    fun setCircleImg(img: ImageView?, path: String?) {
        if (path == null || path.isEmpty()||img==null) {
            return
        }
        Glide.with(img)
            .load(if (TextUtils.isDigitsOnly(path)) Integer.valueOf(path) else path)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(img)
    }

    /**
     * 加载网络或者本地资源
     *
     * @param img
     * @param url
     */
    @JvmStatic
    @BindingAdapter("imgUrl")
    fun setImgUrl(img: ImageView?, url: String?) {
        if (url == null || url.isEmpty()||img==null) {
            return
        }
        //如果是-1不设置图片
        if (TextUtils.isDigitsOnly(url)) {
            val resId: Int = Integer.valueOf(url)
            if (resId <= 0) {
                return
            }
        }
        Glide.with(img)
            .load(if (TextUtils.isDigitsOnly(url)) Integer.valueOf(url) else url)
            .into(img)
    }

    @JvmStatic
    @BindingAdapter("android:itemDecoration")
    fun addItemDecoration(mRecyclerView:RecyclerView,type:Int){
            when(type){
                0-> mRecyclerView.addItemDecoration(DividerLine(mRecyclerView.context, LineDrawMode.HORIZONTAL))
                1-> mRecyclerView.addItemDecoration(DividerLine(mRecyclerView.context, LineDrawMode.VERTICAL))
                2-> mRecyclerView.addItemDecoration(DividerLine(mRecyclerView.context, LineDrawMode.BOTH))
                else->mRecyclerView.addItemDecoration(DividerLine(mRecyclerView.context, LineDrawMode.VERTICAL))
            }
    }

    @JvmStatic
    @BindingAdapter(value = ["adapter", "bindAdapterAnimation"], requireAll = false)
    fun bindAdapter(recyclerView: RecyclerView, adapter: BaseMvvmRecyclerAdapter<*>, animation: Int) {
        recyclerView.adapter = adapter
        //设置动画
        if (animation != 0) {
            adapter.openLoadAnimation(animation)
        }
        //adapter.notifyDataSetChanged();
        // recyclerView.setPageFooter(R.layout.layout_loading_footer);
    }
}