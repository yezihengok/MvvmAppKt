package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 电量监听广播
 * @author Ttt
 */
class BatteryReceiver private constructor() : BroadcastReceiver() {
     override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.action
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            // 获取当前电量, 范围是 0-100
            val level: Int = intent.getIntExtra("level", 0)
            when (action) {
                Intent.ACTION_BATTERY_CHANGED ->sListener?.onBatteryChanged(level)
                Intent.ACTION_BATTERY_LOW ->  sListener?.onBatteryLow(level)
                Intent.ACTION_BATTERY_OKAY ->sListener?.onBatteryOkay(level)
                Intent.ACTION_POWER_CONNECTED ->sListener?.onPowerConnected(level, true)
                Intent.ACTION_POWER_DISCONNECTED ->sListener?.onPowerConnected(level, false)
                Intent.ACTION_POWER_USAGE_SUMMARY ->sListener?.onPowerUsageSummary(level)
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 电量监听事件
     * @author Ttt
     */
     interface BatteryListener {
        /**
         * 电量改变通知
         * @param level 电量百分比
         */
        fun onBatteryChanged(level: Int)

        /**
         * 电量低通知
         * @param level 电量百分比
         */
        fun onBatteryLow(level: Int)

        /**
         * 电量从低变回高通知
         * @param level 电量百分比
         */
        fun onBatteryOkay(level: Int)

        /**
         * 充电状态改变通知
         * @param level       电量百分比
         * @param isConnected 是否充电连接中
         */
        fun onPowerConnected(level: Int, isConnected: Boolean)

        /**
         * 电力使用情况总结
         * @param level 电量百分比
         */
        fun onPowerUsageSummary(level: Int)
    }

    companion object {
        // 日志 TAG
        private val TAG: String = BatteryReceiver::class.java.simpleName

        // ================
        // = 对外公开方法 =
        // ================
        // 电量监听广播
        private val sReceiver: BatteryReceiver = BatteryReceiver()

        /**
         * 注册电量监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 电量状态发送改变
                filter.addAction(Intent.ACTION_BATTERY_CHANGED)
                // 电量低
                filter.addAction(Intent.ACTION_BATTERY_LOW)
                // 电量从低变回高
                filter.addAction(Intent.ACTION_BATTERY_OKAY)
                // 连接充电器
                filter.addAction(Intent.ACTION_POWER_CONNECTED)
                // 断开充电器
                filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                // 电力使用情况总结
                filter.addAction(Intent.ACTION_POWER_USAGE_SUMMARY)
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册电量监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 电量监听事件
        private var sListener: BatteryListener? = null

        /**
         * 设置电量监听事件
         * @param listener [BatteryListener]
         * @return [BatteryReceiver]
         */
        fun setBatteryListener(listener: BatteryListener?): BatteryReceiver {
            sListener = listener
            return sReceiver
        }
    }
}