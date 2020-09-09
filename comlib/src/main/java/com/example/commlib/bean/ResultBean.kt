package com.example.commlib.bean

import java.io.Serializable

/**
 * Created by yzh on 2020/8/19 16:24.
 */
class ResultBean<T>(var errorCode: Int, var errorMsg: String): Serializable {
    var data: T? = null
//        private set
//    fun setData(data: T) {
//        this.data = data
//    }
}