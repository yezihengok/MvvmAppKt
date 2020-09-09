package com.example.commlib.receiver

import android.Manifest.permission
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresPermission
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 网络监听广播
 * @author Ttt
 */
class NetWorkReceiver private constructor() : BroadcastReceiver() {
     override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.action
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            // 网络连接状态改变时通知
            if ((ConnectivityManager.CONNECTIVITY_ACTION == action)) {
                // 设置连接类型
                mConnectState = getConnectType()
                // 触发事件
                if (sListener != null) {
                    sListener!!.onNetworkState(mConnectState)
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 监听通知事件
     * @author Ttt
     */
     interface NetwordStateListener {
        /**
         * 网络连接状态改变时通知
         * @param type 通知类型
         */
        fun onNetworkState(type: Int)
    }

    companion object {
        // 日志 TAG
        private val TAG: String = NetWorkReceiver::class.java.simpleName


        // ========
        // = 常量 =
        // ========
        private val BASE: Int = 202030

        // Wifi
        val NET_WIFI: Int = BASE + 1

        // 移动网络
        val NET_MOBILE: Int = BASE + 2

        // ( 无网络 / 未知 ) 状态
        private val NO_NETWORK: Int = BASE + 3


        // 当前连接的状态
        private var mConnectState: Int = NO_NETWORK

        // ================
        // = 对外公开方法 =
        // ================
        /**
         * 是否连接网络
         * @return `true` yes, `false` no
         */
        fun isConnectNetWork(): Boolean {
            return (mConnectState == NET_WIFI || mConnectState == NET_MOBILE)
        }

        /**
         * 获取连接的网络类型
         * @return 连接的网络类型
         */
        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        fun getConnectType(): Int {
            // 获取手机所有连接管理对象 ( 包括对 wi-fi,net 等连接的管理 )
            try {
                // 获取网络连接状态
                val cManager: ConnectivityManager? = AppUtils.connectivityManager
                // 版本兼容处理
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    // 判断连接的是否 Wifi
                    val wifiState: NetworkInfo.State = cManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                    // 判断是否连接上
                    if (wifiState == NetworkInfo.State.CONNECTED || wifiState == NetworkInfo.State.CONNECTING) {
                        return NET_WIFI
                    } else {
                        // 判断连接的是否移动网络
                        val mobileState: NetworkInfo.State = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                        // 判断移动网络是否连接上
                        if (mobileState == NetworkInfo.State.CONNECTED || mobileState == NetworkInfo.State.CONNECTING) {
                            return NET_MOBILE
                        }
                    }
                } else {
                    // 获取当前活跃的网络 ( 连接的网络信息 )
                    val network: Network? = cManager!!.getActiveNetwork()
                    if (network != null) {
                        val networkCapabilities: NetworkCapabilities = cManager.getNetworkCapabilities(network)
                        // 判断连接的是否 Wifi
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            return NET_WIFI
                        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            return NET_MOBILE
                        }
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getConnectType")
            }
            return NO_NETWORK
        }

        // =
        // 网络广播监听
        private val sReceiver: NetWorkReceiver = NetWorkReceiver()

        /**
         * 注册网络广播监听
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 网络连接状态改变时通知
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                filter.priority = Int.MAX_VALUE
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册网络广播监听
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 监听通知事件
        private var sListener: NetwordStateListener? = null

        /**
         * 设置监听通知事件
         * @param listener [NetwordStateListener]
         * @return [NetWorkReceiver]
         */
        fun setNetListener(listener: NetwordStateListener?): NetWorkReceiver {
            sListener = listener
            return sReceiver
        }
    }
}