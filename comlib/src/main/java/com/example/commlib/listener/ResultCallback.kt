package com.example.commlib.listener

/**
 * Created by yzh on 2020/8/20 15:04.
 */
interface ResultCallback<T> {
    fun onResult(result: T)
}