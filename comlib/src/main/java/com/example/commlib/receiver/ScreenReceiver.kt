package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 屏幕监听广播 ( 锁屏 / 解锁 / 亮屏 )
 * @author Ttt
 */
class ScreenReceiver private constructor() : BroadcastReceiver() {
     override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.action
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            when (action) {
                Intent.ACTION_SCREEN_ON ->sListener?.screenOn()
                Intent.ACTION_SCREEN_OFF ->sListener?.screenOff()
                Intent.ACTION_USER_PRESENT ->sListener?.userPresent()
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 屏幕监听事件
     * @author Ttt
     */
    open interface ScreenListener {
        /**
         * 用户打开屏幕 ( 屏幕变亮 )
         */
        fun screenOn()

        /**
         * 锁屏触发
         */
        fun screenOff()

        /**
         * 用户解锁触发
         */
        fun userPresent()
    }

    companion object {
        // 日志 TAG
        private val TAG: String = ScreenReceiver::class.java.simpleName

        // ================
        // = 对外公开方法 =
        // ================
        // 屏幕广播监听
        private val sReceiver: ScreenReceiver = ScreenReceiver()

        /**
         * 注册屏幕监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 屏幕状态改变通知
                filter.addAction(Intent.ACTION_SCREEN_ON)
                filter.addAction(Intent.ACTION_SCREEN_OFF)
                filter.addAction(Intent.ACTION_USER_PRESENT)
                filter.priority = Int.MAX_VALUE
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册屏幕监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 屏幕监听事件
        private var sListener: ScreenListener? = null

        /**
         * 设置屏幕监听事件
         * @param listener [ScreenListener]
         * @return [ScreenReceiver]
         */
        fun setScreenListener(listener: ScreenListener?): ScreenReceiver {
            sListener = listener
            return sReceiver
        }
    }
}