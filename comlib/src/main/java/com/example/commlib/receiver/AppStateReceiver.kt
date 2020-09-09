package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 应用状态监听广播 ( 安装、更新、卸载 )
 * @author Ttt
 */
class AppStateReceiver private constructor() : BroadcastReceiver() {
     override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.action
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            // 被操作应用包名
            var packageName: String? = null
            val uri: Uri? = intent.data
            //                packageName = uri.toString();
            packageName = uri?.encodedSchemeSpecificPart
            when (action) {
                Intent.ACTION_PACKAGE_ADDED ->sListener?.onAdded(packageName)
                Intent.ACTION_PACKAGE_REPLACED ->sListener?.onReplaced(packageName)
                Intent.ACTION_PACKAGE_REMOVED ->sListener?.onRemoved(packageName)
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 应用状态监听事件
     * @author Ttt
     */
    interface AppStateListener {
        /**
         * 应用安装
         * @param packageName 应用包名
         */
        fun onAdded(packageName: String?)

        /**
         * 应用更新
         * @param packageName 应用包名
         */
        fun onReplaced(packageName: String?)

        /**
         * 应用卸载
         * @param packageName 应用包名
         */
        fun onRemoved(packageName: String?)
    }

    companion object {
        // 日志 TAG
        private val TAG: String = AppStateReceiver::class.java.simpleName

        // ================
        // = 对外公开方法 =
        // ================
        // 应用状态监听广播
        private val sReceiver: AppStateReceiver = AppStateReceiver()

        /**
         * 注册应用状态监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 安装
                filter.addAction(Intent.ACTION_PACKAGE_ADDED)
                // 更新
                filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
                // 卸载
                filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
                filter.addDataScheme("package")
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册应用状态监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 应用状态监听事件
        private var sListener: AppStateListener? = null

        /**
         * 设置应用状态监听事件
         * @param listener [AppStateListener]
         * @return [AppStateReceiver]
         */
        fun setAppStateListener(listener: AppStateListener?): AppStateReceiver {
            sListener = listener
            return sReceiver
        }
    }
}