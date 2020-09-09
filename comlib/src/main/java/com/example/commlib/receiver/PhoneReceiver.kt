package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 手机监听广播
 * @author Ttt
 * <pre>
 * 所需权限
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
 * <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"></uses-permission>
</pre> *
 */
class PhoneReceiver private constructor() : BroadcastReceiver() {
    // 通话号码
    private var mNumber: String? = null

    // 是否拨号打出
    private var mIsDialOut: Boolean = false
     override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.action
            // 打印当前触发的广播
            ALog.dTag(TAG, "onReceive Action: $action")
            // 判断类型
            if ((NEW_OUTGOING_CALL == action)) {
                // 表示属于拨号
                mIsDialOut = true
                // 拨出号码
                mNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                // 触发事件
                sListener?.onPhoneStateChanged(CallState.Outgoing, mNumber)// 播出电话
            } else if ((PHONE_STATE == action)) {
                // 通话号码
                mNumber = intent.getStringExtra("incoming_number")
                // 状态
                when (intent.getStringExtra("state")) {
                    RINGING -> {
                        mIsDialOut = false
                        sListener?.onPhoneStateChanged(CallState.IncomingRing, mNumber)// 接入电话铃响
                    }
                    OFFHOOK -> if (!mIsDialOut) {
                        sListener?.onPhoneStateChanged(CallState.Incoming, mNumber) // 接入通话中
                    }
                    IDLE -> if (mIsDialOut) {
                        sListener?.onPhoneStateChanged(CallState.OutgoingEnd, mNumber) // 播出电话结束
                    } else {
                        sListener?.onPhoneStateChanged(CallState.IncomingEnd, mNumber)// 接入通话完毕
                    }
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 电话状态监听事件
     * @author Ttt
     */
    open interface PhoneListener {
        /**
         * 电话状态改变通知
         * @param state  通话状态
         * @param number 通话号码
         */
        fun onPhoneStateChanged(state: CallState?, number: String?)
    }

    /**
     * detail: 通话状态
     * @author Ttt
     */
    enum class CallState {
        Outgoing,  // 播出电话
        OutgoingEnd,  // 播出电话结束
        IncomingRing,  // 接入电话铃响
        Incoming,  // 接入通话中
        IncomingEnd // 接入通话完毕
    }

    companion object {
        // 日志 TAG
        private val TAG: String = PhoneReceiver::class.java.getSimpleName()

        // 电话状态监听意图
        private val PHONE_STATE: String = "android.intent.action.PHONE_STATE"

        // 拨出电话意图
        private val NEW_OUTGOING_CALL: String = "android.intent.action.NEW_OUTGOING_CALL"

        // ========
        // = 状态 =
        // ========
        // 未接
        private val RINGING: String = "RINGING"

        // 已接
        private val OFFHOOK: String = "OFFHOOK"

        // 挂断
        private val IDLE: String = "IDLE"

        // ================
        // = 对外公开方法 =
        // ================
        // 电话监听广播
        private val sReceiver: PhoneReceiver = PhoneReceiver()

        /**
         * 注册电话监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 电话状态监听
                filter.addAction(PHONE_STATE)
                filter.addAction(NEW_OUTGOING_CALL)
                filter.priority = Int.MAX_VALUE
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册电话监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 电话状态监听事件
        private var sListener: PhoneListener? = null

        /**
         * 设置电话状态监听事件
         * @param listener [PhoneListener]
         * @return [PhoneReceiver]
         */
        fun setPhoneListener(listener: PhoneListener?): PhoneReceiver {
            sListener = listener
            return sReceiver
        }
    }
}