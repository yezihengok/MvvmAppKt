package com.example.commlib.utils

import com.blankj.ALog
import java.io.Closeable

/**
 * detail: 关闭 (IO 流 ) 工具类
 * @author Ttt
 */
object CloseUtils {
    // 日志 TAG
    private val TAG = CloseUtils::class.java.simpleName

    /**
     * 关闭 IO
     * @param closeables Closeable[]
     */
    fun closeIO(vararg closeables: Closeable?) {
        if (closeables == null) return
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "closeIO")
                }
            }
        }
    }

    /**
     * 安静关闭 IO
     * @param closeables Closeable[]
     */
    @JvmStatic
    fun closeIOQuietly(vararg closeables: Closeable?) {
        if (closeables == null) return
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (ignore: Exception) {
                }
            }
        }
    }
}