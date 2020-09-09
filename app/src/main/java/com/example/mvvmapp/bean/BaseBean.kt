package com.example.mvvmapp.bean

import com.example.commlib.utils.GsonUtil

/**
 * Created by Android Studio.
 * 以继承方式  方便拓展
 * @author zjx
 * @date 2020/4/27
 */
open class BaseBean {
    override fun toString(): String {
        return GsonUtil.getBeanToJson(this)
    }
}