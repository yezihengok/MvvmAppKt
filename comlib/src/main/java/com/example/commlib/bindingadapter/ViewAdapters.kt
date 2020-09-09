package com.example.commlib.bindingadapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.commlib.event.SingleLiveEvent
import com.example.commlib.listener.ClickListener
import com.example.commlib.utils.ButtonUtils

/**
 * Created by yzh on 2020/8/20 9:58.
 */
object ViewAdapters {
//    1、声明对象实例时，在类型名称后面加问号，表示该对象可以为空；
//    2、调用对象方法时，在实例名称后面加问号，表示一旦实例为空就返回null；
//    3、新引入运算符“?:”，一旦实例为空就返回该运算符右边的表达式；
//    4、新引入运算符“!!”，通知编译器不做非空校验，运行时一旦发现实例为空就扔出异常；


    /**
     * requireAll 是意思是是否需要绑定全部参数, false为否
     */
    @JvmStatic
    @BindingAdapter(value = ["textIsNullGone"], requireAll = false)
    fun setTexts(view:TextView,str:String?){
        //如果是空的话就设置为Gone
        view.visibility =if (TextUtils.isEmpty(str))View.GONE else View.VISIBLE
        view.text = str?:""
    }

    @JvmStatic
    @BindingAdapter(value = ["onBindingClick"], requireAll = false)
    fun onClicks(v: View?,listener:ClickListener?){
        v?.setOnClickListener {
            if(!ButtonUtils.isFastDoubleClick()) {
                listener?.onResult(v)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["onBindingClick"], requireAll = false)
    fun onClick(view: View?,even:SingleLiveEvent<*>?){
        view?.setOnClickListener{
            if(!ButtonUtils.isFastDoubleClick()){
                even?.call()
            }
        }
    }

    /**
     * 设置宽高
     * @param imageView
     * @param width
     * @param height
     */
    @JvmStatic
    @BindingAdapter(value = ["setWidth", "setHeight"], requireAll = false)
    fun setHW(imageView:View,width:Int,height:Int){
        if(width>0){
            imageView.layoutParams.width=width
        }
        if(height>0){
            imageView.layoutParams.height=height
        }
    }

}