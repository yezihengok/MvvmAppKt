package com.example.commlib.weight

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.blankj.ALog
import com.example.commlib.R

/**
 * @Author: yzh
 * @CreateDate: 2019/11/1 11:39
 */
class LoadDialog constructor(context: Context,var msg: String="",private var cancelAble: Boolean=true) : Dialog(context, R.style.comm_load_dialog) {

//    init {
//        this.msg = msg
//        this.cancelAble = cancelAble
//    }
// private var msg: String? = ""
//private var cancelAble: Boolean = true
    lateinit var tvMessage: TextView

    private var jump: JumpingBeans? = null
    private var cancelTime //避免异常情况下Dialog
            : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comm_load_dialogs)
        tvMessage = findViewById(R.id.tvLoadDialog_Message)
        if (TextUtils.isEmpty(msg)) {
            tvMessage.visibility = View.GONE
        } else {
            tvMessage.visibility = View.VISIBLE
            tvMessage.text = msg
            if (msg.length > 3) {
                jump = JumpingBeans.with(tvMessage)
                        .makeTextJump(msg.length - 3, msg.length)
                        .setIsWave(true)
                        .setLoopDuration(1500)
                        .build()
            }
        }
    }

    override fun dismiss() {
        if (jump != null) {
            jump!!.stopJumping()
        }
        cancelTime = 0
        super.dismiss()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        ALog.v("cancelTime $cancelTime")
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                cancelTime++
                if (cancelTime > 9) {
                    return super.onTouchEvent(event)
                }
            }
        }
        if (!cancelAble) {
            return false
        }
        return super.onTouchEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!cancelAble) return false
        }
        return super.onKeyDown(keyCode, event)
    }

}