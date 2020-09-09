package com.example.commlib.utils

import android.util.Log

/**
 * Created by yzh
 */
object ButtonUtils {
    private var lastClickTime: Long = 0
    private const val DIFF: Long = 500
    private var lastButtonId = -1

    /**
     * 判断两次点击的间隔，如果小于500，则认为是多次无效点击
     *
     * @return
     */
    val isFastDoubleClick: Boolean
        get() = isFastDoubleClick(-1, DIFF)

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    fun isFastDoubleClick(buttonId: Int): Boolean {
        return isFastDoubleClick(buttonId, DIFF)
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    fun isFastDoubleClick(buttonId: Int, diff: Long): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            Log.v("isFastDoubleClick", "短时间内按钮多次触发")
            return true
        }
        lastClickTime = time
        lastButtonId = buttonId
        return false
    }
}