package com.example.commlib.utils

import android.os.Handler
import android.os.Looper
import java.util.*

/**
 * @author yeziheng
 * @date 2016-02-14
 */
object HandlerUtil {
    var runnables: MutableList<Runnable> = ArrayList()
    val HANDLER = Handler(Looper.getMainLooper())
    fun runOnUiThread(runnable: Runnable) {
        HANDLER.post(runnable)
        runnables.add(runnable)
    }

    fun runOnUiThreadDelay(runnable: Runnable, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
        runnables.add(runnable)
    }

    fun removeRunable(runnable: Runnable?) {
        HANDLER.removeCallbacks(runnable)
    }

    fun removeAllRunable() {
        if (runnables.size > 0) {
            for (runnable in runnables) {
                HANDLER.removeCallbacks(runnable)
            }
        }
    }
}