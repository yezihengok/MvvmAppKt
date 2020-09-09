package com.example.commlib.downloadapk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.blankj.ALog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

/**
 * 下载工具类（开发中一般用于APK应用升级）
 * 需要断点续传的可使用这个库 ：https://github.com/ssseasonnn/RxDownload
 */
class DownloadAPk {

    /**
     * 判断8.0 安装权限
     */
    fun downApk(context: Context, url: String?, listener: DownLoadListener) {
        mContext = context
        mListener = listener
        if (Build.VERSION.SDK_INT >= 26) {
            val b = context.packageManager.canRequestPackageInstalls()
            if (b) {
                downloadAPK(url, null)
            } else {
                //请求安装未知应用来源的权限
                startInstallPermissionSettingActivity()
            }
        } else {
            downloadAPK(url, null)
        }
    }

    /**
     * 开启安装APK权限(适配8.0)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        val packageURI = Uri.parse("package:" + mContext?.packageName)
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        mContext?.startActivity(intent)
    }

    /**
     * 下载APK文件
     */
    private fun downloadAPK(url: String?, localAddress: String?) {
        // 下载
        if (localAddress != null) {
            APK_UPGRADE = localAddress
        }
        UpgradeTask().execute(url)
    }

    internal class UpgradeTask : AsyncTask<String?, Int?, Void?>() {
        override fun onPreExecute() {}
        override fun doInBackground(vararg params: String?): Void? {
            val apkUrl = params[0]
            var `is`: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                val url = URL(apkUrl)
                val conn = url
                    .openConnection() as HttpURLConnection
                // 设置连接超时时间
                conn.connectTimeout = 20000
                // 设置下载数据超时时间
                conn.readTimeout = 25000
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    return null // 服务端错误响应
                }
                `is` = conn.inputStream
                FILE_LEN = conn.contentLength
                val apkFile = File(APK_UPGRADE)
                // 如果文件夹不存在则创建
                if (!apkFile.parentFile.exists()) {
                    apkFile.parentFile.mkdirs()
                }
                fos = FileOutputStream(apkFile)
                val buffer = ByteArray(8024)
                var len = 0
                var loadedLen = 0 // 当前已下载文件大小
                // 更新100次onProgressUpdate 回調次數
                val updateSize = FILE_LEN / 100
                var num = 0
                while (-1 != `is`.read(buffer).also { len = it }) {
                    loadedLen += len
                    fos.write(buffer, 0, len)
                    if (loadedLen > updateSize * num) {
                        num++
                        publishProgress(loadedLen)
                    }
                }
                fos.flush()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: SocketTimeoutException) {
                // 处理超时异常，提示用户在网络良好情况下重试
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            val progress: Int = if (values[0] == FILE_LEN) {
                100
            } else {
                (values[0]?:0) * 100 / FILE_LEN
                //进度显示2位小数：
                // double progress= ArithUtils.round((values[0] * 100 / (double) FILE_LEN),2);
            }
            Log.w("DownloadAPk", progress.toString() + "%    总大小：" + FILE_LEN + "已下载大小：" + values[0]
            )
            mListener?.onProgressUpdate(progress)
        }

        override fun onPostExecute(result: Void?) {
            Log.v("DownloadAPk", "下载完成")
            mListener?.finish(APK_UPGRADE)
            mContext?.startActivity(getInstallAppIntent(APK_UPGRADE))
        }

    }

    interface DownLoadListener {
        fun onProgressUpdate(progress: Int)
        fun finish(filePath: String?)
    }

    companion object {
        var mListener: DownLoadListener? = null
        private var FILE_LEN = 0
        var APK_UPGRADE =
            Environment.getExternalStorageDirectory().toString() + "/DownLoad/apk/downloadApp.apk"
        private var mContext: Context? = null


        val instance:DownloadAPk by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DownloadAPk()}
//        @Volatile
//        var downloadAPk: DownloadAPk? = null
//        val instance: DownloadAPk?
//            get() {
//                if (downloadAPk == null) {
//                    synchronized(DownloadAPk::class.java) {
//                        if (downloadAPk == null) {
//                            downloadAPk = DownloadAPk()
//                        }
//                    }
//                }
//                return downloadAPk
//            }

        /**
         * 调往系统APK安装界面（适配7.0）
         *
         * @return
         */
        fun getInstallAppIntent(filePath: String?): Intent? {
            //apk文件的本地路径
            val apkfile = File(filePath)
            if (!apkfile.exists()) {
                return null
            }
            val intent = Intent(Intent.ACTION_VIEW)
            val contentUri = getUriForFile(apkfile)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            return intent
        }

        /**
         * 将文件转换成uri
         *
         * @return
         */
        fun getUriForFile(file: File): Uri? {
            ALog.v(file.path)
            var fileUri: Uri? = null
            fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    mContext!!,
                    mContext?.packageName + ".fileprovider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
            ALog.v(fileUri.toString())
            return fileUri
        }
    }
}