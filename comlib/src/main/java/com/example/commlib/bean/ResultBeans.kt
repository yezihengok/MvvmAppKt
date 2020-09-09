package com.example.commlib.bean

import java.io.Serializable

/**
 * Created by yzh on 2020/8/19 16:30.
 */
class ResultBeans<T> (var errorCode: Int, var errorMsg: String) : Serializable {
    var data: List<T>? = null
}