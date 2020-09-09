package com.example.commlib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsMessage
import com.blankj.ALog
import com.example.commlib.utils.AppUtils

/**
 * detail: 短信监听广播
 * @author Ttt
 * <pre>
 * 所需权限
 * <uses-permission android:name="android.permission.RECEIVE_SMS"></uses-permission>
</pre> *
 */
class SmsReceiver private constructor() : BroadcastReceiver() {
    public override fun onReceive(context: Context, intent: Intent) {
        try {
            val pdus: Array<*>? = intent.extras?.get("pdus") as Array<*>?
            var originatingAddress: String? = null
            var serviceCenterAddress: String? = null
            if (pdus != null) {
                // 消息内容
                var message: String? = ""
                // 循环拼接内容
                for (obj: Any? in pdus) {
                    val sms: SmsMessage = SmsMessage.createFromPdu(obj as ByteArray?)
                    message += sms.messageBody // 消息内容 - 多条消息, 合并成一条
                    originatingAddress = sms.originatingAddress
                    serviceCenterAddress = sms.serviceCenterAddress
                    // 触发事件
                    if (sListener != null) {
                        sListener!!.onMessage(sms)
                    }
                }
                // 触发事件
                if (sListener != null) {
                    sListener!!.onMessage(message, originatingAddress, serviceCenterAddress)
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "onReceive")
        }
    }

    /**
     * detail: 短信监听事件
     * @author Ttt
     */
    abstract class SmsListener constructor() {
        /**
         * 最终触发通知 ( 超过长度的短信消息, 最终合并成一条内容体 )
         * @param message              短信内容
         * @param originatingAddress   短信的原始地址 ( 发件人 )
         * @param serviceCenterAddress 短信服务中心地址
         */
        abstract fun onMessage(message: String?, originatingAddress: String?, serviceCenterAddress: String?)

        /**
         * 收到消息提醒 ( 超过长度的消息变成两条会触发多次 )
         * @param message [SmsMessage]
         */
        fun onMessage(message: SmsMessage?) {}
    }

    companion object {
        // 日志 TAG
        private val TAG: String = SmsReceiver::class.java.simpleName
        // ================
        // = 对外公开方法 =
        // ================
        /**
         * 获取消息数据
         * @param message [SmsMessage]
         * @return 消息数据
         */
        fun getMessageData(message: SmsMessage?): String {
            val builder: StringBuilder = StringBuilder()
            if (message != null) {
                builder.append("\ngetDisplayMessageBody: " + message.displayMessageBody)
                builder.append("\ngetDisplayOriginatingAddress: " + message.displayOriginatingAddress)
                builder.append("\ngetEmailBody: " + message.emailBody)
                builder.append("\ngetEmailFrom: " + message.emailFrom)
                builder.append("\ngetMessageBody: " + message.messageBody)
                builder.append("\ngetOriginatingAddress: " + message.originatingAddress)
                builder.append("\ngetPseudoSubject: " + message.pseudoSubject)
                builder.append("\ngetServiceCenterAddress: " + message.serviceCenterAddress)
                builder.append("\ngetIndexOnIcc: " + message.indexOnIcc)
                builder.append("\ngetMessageClass: " + message.messageClass)
                builder.append("\ngetUserData: " + String(message.userData))
            }
            return builder.toString()
        }

        // =
        // 短信监听广播
        private val sReceiver: SmsReceiver = SmsReceiver()

        /**
         * 注册短信监听广播
         */
        fun registerReceiver() {
            try {
                val filter: IntentFilter = IntentFilter()
                // 短信获取监听
                filter.addAction("android.provider.Telephony.SMS_RECEIVED")
                filter.priority = Int.MAX_VALUE
                // 注册广播
                AppUtils.registerReceiver(sReceiver, filter)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "registerReceiver")
            }
        }

        /**
         * 取消注册短信监听广播
         */
        fun unregisterReceiver() {
            try {
                AppUtils.unregisterReceiver(sReceiver)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "unregisterReceiver")
            }
        }

        // =
        // 短信监听事件
        private var sListener: SmsListener? = null

        /**
         * 设置短信监听事件
         * @param listener [SmsListener]
         * @return [SmsReceiver]
         */
        fun setSmsListener(listener: SmsListener?): SmsReceiver {
            sListener = listener
            return sReceiver
        }
    }
}