package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Message
import android.os.Parcelable
import com.blankj.ALog
import com.example.commlib.utils.AppUtils
import com.example.commlib.utils.wifi.WifiUtils

/**
 * detail: Wifi 监听广播
 * @author Ttt
 * <pre>
 * 所需权限
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
</pre> *
 */
class WifiReceiver private constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            // 触发回调通知 ( 每次进入都通知 )
            if (sListener != null) sListener?.onIntoTrigger()
            // 触发意图
            val action: String? = intent.action
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            when (action) {
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> sListener?.onTrigger(WIFI_SCAN_FINISH)
                WifiManager.RSSI_CHANGED_ACTION ->sListener?.onTrigger(WIFI_RSSI_CHANGED)
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> {
                    // 出现错误状态, 则获取错误状态
                    when (intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0)) {
                        WifiManager.ERROR_AUTHENTICATING -> sListener?.onTrigger(WIFI_ERROR_AUTHENTICATING)
                        else -> sListener?.onTrigger(WIFI_ERROR_UNKNOWN)
                    }
                }
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    // 获取 Wifi 状态
                    when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                        WifiManager.WIFI_STATE_ENABLED ->sListener?.onTrigger(WIFI_STATE_ENABLED)
                        WifiManager.WIFI_STATE_ENABLING -> sListener?.onTrigger(WIFI_STATE_ENABLING)
                        WifiManager.WIFI_STATE_DISABLED ->sListener?.onTrigger(WIFI_STATE_DISABLED)
                        WifiManager.WIFI_STATE_DISABLING ->sListener?.onTrigger(WIFI_STATE_DISABLING)
                        WifiManager.WIFI_STATE_UNKNOWN ->sListener?.onTrigger(WIFI_STATE_UNKNOWN)
                    }
                }
                WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                    val parcelableExtra: Parcelable? = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
                    if (parcelableExtra != null) {
                        // 获取连接信息
                        val networkInfo: NetworkInfo = parcelableExtra as NetworkInfo
                        // 获取连接的状态
                        val state: NetworkInfo.State = networkInfo.state
                        // 通知消息
                        val msg: Message = Message()
                        // 当前连接的 SSID
                        msg.obj = WifiUtils.getSSID()
                        when (state) {
                            NetworkInfo.State.CONNECTED -> msg.what = CONNECTED
                            NetworkInfo.State.CONNECTING -> msg.what = CONNECTING
                            NetworkInfo.State.DISCONNECTED -> msg.what = DISCONNECTED
                            NetworkInfo.State.SUSPENDED -> msg.what = SUSPENDED
                            NetworkInfo.State.UNKNOWN -> msg.what = UNKNOWN
                        }
                        // 触发回调
                        if (sListener != null) {
                            sListener?.onTrigger(msg.what, msg)
                        }
                    }
                }
                WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION -> {
                    // 判断是否打开 Wifi
                    val isOpenWifi: Boolean = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)
                    // 触发回调
                    if (sListener != null) {
                        sListener?.onWifiSwitch(isOpenWifi)
                    }
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: Wifi 监听事件
     * @author Ttt
     */
    abstract class WifiListener constructor() {
        /**
         * 触发回调通知 ( 每次进入都通知 )
         */
        fun onIntoTrigger() {}

        /**
         * 触发回调通知
         * @param what 触发类型
         */
        abstract fun onTrigger(what: Int)

        /**
         * 触发回调通知 ( Wifi 连接过程的状态 )
         * @param what 触发类型
         * @param msg  触发信息
         */
        abstract fun onTrigger(what: Int, msg: Message?)

        /**
         * Wifi 开关状态
         * @param isOpenWifi 是否打开 Wifi
         */
        abstract fun onWifiSwitch(isOpenWifi: Boolean)
    }

    companion object {
        // 日志 TAG
        private val TAG: String = WifiReceiver::class.java.simpleName

        // ========
        // = 常量 =
        // ========
        private val BASE: Int = 302030

        // startScan() 扫描附近 Wifi 结束触发
        val WIFI_SCAN_FINISH: Int = BASE + 1

        // 已连接的 Wifi 强度发生变化
        val WIFI_RSSI_CHANGED: Int = BASE + 2

        // Wifi 认证错误 ( 密码错误等 )
        val WIFI_ERROR_AUTHENTICATING: Int = BASE + 3

        // 连接错误 ( 其他错误 )
        val WIFI_ERROR_UNKNOWN: Int = BASE + 4

        // Wifi 已打开
        val WIFI_STATE_ENABLED: Int = BASE + 5

        // Wifi 正在打开
        val WIFI_STATE_ENABLING: Int = BASE + 6

        // Wifi 已关闭
        val WIFI_STATE_DISABLED: Int = BASE + 7

        // Wifi 正在关闭
        val WIFI_STATE_DISABLING: Int = BASE + 8

        // Wifi 状态未知
        val WIFI_STATE_UNKNOWN: Int = BASE + 9

        // Wifi 连接成功
        val CONNECTED: Int = BASE + 10

        // Wifi 连接中
        val CONNECTING: Int = BASE + 11

        // Wifi 连接失败、断开
        val DISCONNECTED: Int = BASE + 12

        // Wifi 暂停、延迟
        val SUSPENDED: Int = BASE + 13

        // Wifi 未知
        val UNKNOWN: Int = BASE + 14

        // ================
        // = 对外公开方法 =
        // ================
        // Wifi 监听广播
        private val sReceiver: WifiReceiver = WifiReceiver()

        /**
         * 注册 Wifi 监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 当调用 WifiManager 的 startScan() 方法, 扫描结束后, 系统会发出改 Action 广播
                filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                // 当前连接的 Wifi 强度发生变化触发
                filter.addAction(WifiManager.RSSI_CHANGED_ACTION)
                // Wifi 在连接过程的状态返回
                filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                // 监听 Wifi 的打开与关闭等状态, 与 Wifi 的连接无关
                filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                // 发送 Wifi 连接的过程信息, 如果出错 ERROR 信息才会收到, 连接 Wifi 时触发, 触发多次
                filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
                // 判断是否 Wifi 打开了, 变化触发一次
                filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册 Wifi 监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // Wifi 监听事件
        private var sListener: WifiListener? = null

        /**
         * 设置 Wifi 监听事件
         * @param listener [WifiListener]
         * @return [WifiReceiver]
         */
        fun setWifiListener(listener: WifiListener?): WifiReceiver {
            sListener = listener
            return sReceiver
        }
    }
}