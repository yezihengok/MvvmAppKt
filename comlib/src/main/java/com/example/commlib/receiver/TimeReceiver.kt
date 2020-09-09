package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 时间监听广播
 * @author Ttt
 */
class TimeReceiver private constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.getAction()
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            when (action) {
                Intent.ACTION_TIMEZONE_CHANGED -> if (sListener != null) {
                    sListener!!.onTimeZoneChanged()
                }
                Intent.ACTION_TIME_CHANGED -> if (sListener != null) {
                    sListener!!.onTimeChanged()
                }
                Intent.ACTION_TIME_TICK -> if (sListener != null) {
                    sListener!!.onTimeTick()
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 时间监听事件
     * @author Ttt
     */
     interface TimeListener {
        /**
         * 时区改变
         */
        fun onTimeZoneChanged()

        /**
         * 设置时间
         */
        fun onTimeChanged()

        /**
         * 每分钟调用
         */
        fun onTimeTick()
    }

    companion object {
        // 日志 TAG
        private val TAG: String = TimeReceiver::class.java.simpleName

        // ================
        // = 对外公开方法 =
        // ================
        // 时间监听广播
        private val sReceiver: TimeReceiver = TimeReceiver()

        /**
         * 注册时间监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 监听时间、时区改变通知
                filter.addAction(Intent.ACTION_TIME_TICK)
                filter.addAction(Intent.ACTION_TIME_CHANGED)
                filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
                filter.priority = Int.MAX_VALUE
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册时间监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 时间监听事件
        private var sListener: TimeListener? = null

        /**
         * 设置时间监听事件
         * @param listener [TimeListener]
         * @return [TimeReceiver]
         */
        fun setTimeListener(listener: TimeListener?): TimeReceiver {
            sListener = listener
            return sReceiver
        }
    }
}