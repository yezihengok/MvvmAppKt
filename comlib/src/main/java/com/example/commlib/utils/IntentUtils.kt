package com.example.commlib.utils

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import com.blankj.ALog
import com.example.commlib.api.App.Companion.instance
import java.io.File
import java.util.*

/**
 * detail: Intent 相关工具类
 * @author Ttt
 * <pre>
 * 所需权限
 * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"></uses-permission>
 * <uses-permission android:name="android.permission.SHUTDOWN"></uses-permission>
 * <uses-permission android:name="android.permission.CALL_PHONE"></uses-permission>
</pre> *
 */
object IntentUtils {
    // 日志 TAG
    private val TAG = IntentUtils::class.java.simpleName

    /**
     * 获取 Intent
     * @param intent    [Intent]
     * @param isNewTask 是否开启新的任务栈 (Context 非 Activity 则需要设置 FLAG_ACTIVITY_NEW_TASK)
     * @return [Intent]
     */
    @JvmStatic
    fun getIntent(intent: Intent?, isNewTask: Boolean): Intent? {
        return if (intent != null) {
            if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
        } else null
    }

    /**
     * 判断 Intent 是否可用
     * @param intent [Intent]
     * @return `true` yes, `false` no
     */
    fun isIntentAvailable(intent: Intent?): Boolean {
        if (intent != null) {
            try {
                return AppUtils.packageManager
                    .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "isIntentAvailable")
            }
        }
        return false
    }

    /**
     * 获取安装 APP( 支持 8.0) 的意图
     * @param filePath 文件路径
     * @return 安装 APP( 支持 8.0) 的意图
     */
    fun getInstallAppIntent(filePath: String?): Intent? {
        return getInstallAppIntent(FileUtils.getFileByPath(filePath))
    }

    /**
     * 获取安装 APP( 支持 8.0) 的意图
     * @param file 文件
     * @return 安装 APP( 支持 8.0) 的意图
     */
    fun getInstallAppIntent(file: File?): Intent? {
        return getInstallAppIntent(file, false)
    }

    /**
     * 获取安装 APP( 支持 8.0) 的意图
     * @param file      文件
     * @param isNewTask 是否开启新的任务栈
     * @return 安装 APP( 支持 8.0) 的意图
     */
    @JvmStatic
    fun getInstallAppIntent(file: File?, isNewTask: Boolean): Intent? {
        if (file == null) return null
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val data: Uri
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                data = Uri.fromFile(file)
            } else {
                data = FileProvider.getUriForFile(
                    instance,
                    instance.packageName + ".fileprovider",
                    file
                )
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            intent.setDataAndType(data, "application/vnd.android.package-archive")
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getInstallAppIntent")
        }
        return null
    }

    /**
     * 获取卸载 APP 的意图
     * @param packageName 应用包名
     * @return 卸载 APP 的意图
     */
    fun getUninstallAppIntent(packageName: String): Intent? {
        return getUninstallAppIntent(packageName, false)
    }

    /**
     * 获取卸载 APP 的意图
     * @param packageName 应用包名
     * @param isNewTask   是否开启新的任务栈
     * @return 卸载 APP 的意图
     */
    @JvmStatic
    fun getUninstallAppIntent(packageName: String, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getUninstallAppIntent")
        }
        return null
    }

    /**
     * 获取打开 APP 的意图
     * @param packageName 应用包名
     * @return 打开 APP 的意图
     */
    fun getLaunchAppIntent(packageName: String?): Intent? {
        return getLaunchAppIntent(packageName, false)
    }

    /**
     * 获取打开 APP 的意图
     * @param packageName 应用包名
     * @param isNewTask   是否开启新的任务栈
     * @return 打开 APP 的意图
     */
    @JvmStatic
    fun getLaunchAppIntent(packageName: String?, isNewTask: Boolean): Intent? {
        try {
            val intent = AppUtils.packageManager.getLaunchIntentForPackage(packageName!!)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getLaunchAppIntent")
        }
        return null
    }

    /**
     * 获取跳转到系统设置的意图
     * @param isNewTask 是否开启新的任务栈
     * @return 跳转到系统设置的意图
     */
    fun getSystemSettingIntent(isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getSystemSettingIntent")
        }
        return null
    }

    /**
     * 获取 APP 安装权限设置的意图
     * @return APP 安装权限设置的意图
     */
    val launchAppInstallPermissionSettingsIntent: Intent?
        get() = getLaunchAppInstallPermissionSettingsIntent(AppUtils.packageName, false)

    /**
     * 获取 APP 安装权限设置的意图
     * @param packageName 应用包名
     * @return APP 安装权限设置的意图
     */
    fun getLaunchAppInstallPermissionSettingsIntent(packageName: String): Intent? {
        return getLaunchAppInstallPermissionSettingsIntent(packageName, false)
    }

    /**
     * 获取 APP 安装权限设置的意图
     * @param packageName 应用包名
     * @param isNewTask   是否开启新的任务栈
     * @return APP 安装权限设置的意图
     */
    fun getLaunchAppInstallPermissionSettingsIntent(
        packageName: String,
        isNewTask: Boolean
    ): Intent? {
        try {
            val uri = Uri.parse("package:$packageName")
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getLaunchAppInstallPermissionSettingsIntent")
        }
        return null
    }

    /**
     * 获取 APP 通知权限设置的意图
     * @return APP 通知权限设置的意图
     */
    val launchAppNotificationSettingsIntent: Intent?
        get() = getLaunchAppNotificationSettingsIntent(AppUtils.packageName, false)

    /**
     * 获取 APP 通知权限设置的意图
     * @param packageName 应用包名
     * @return APP 通知权限设置的意图
     */
    fun getLaunchAppNotificationSettingsIntent(packageName: String?): Intent? {
        return getLaunchAppNotificationSettingsIntent(packageName, false)
    }

    /**
     * 获取 APP 通知权限设置的意图
     * @param packageName 应用包名
     * @param isNewTask   是否开启新的任务栈
     * @return APP 通知权限设置的意图
     */
    fun getLaunchAppNotificationSettingsIntent(packageName: String?, isNewTask: Boolean): Intent? {
        try {
            val applicationInfo = AppUtils.getPackageInfo(packageName, 0)?.applicationInfo
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            // 这种方案适用于 API 26 即 8.0 ( 含 8.0) 以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo?.uid)
            // 这种方案适用于 API 21 - 25 即 5.0 - 7.1 之间的版本可以使用
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo?.uid)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getLaunchAppNotificationSettingsIntent")
        }
        return null
    }

    /**
     * 获取 APP 通知使用权页面
     * @return APP 通知使用权页面
     */
    val launchAppNotificationListenSettingsIntent: Intent?
        get() = getLaunchAppNotificationListenSettingsIntent(false)

    /**
     * 获取 APP 通知使用权页面
     * @param isNewTask 是否开启新的任务栈
     * @return APP 通知使用权页面
     */
    fun getLaunchAppNotificationListenSettingsIntent(isNewTask: Boolean): Intent? {
        val intent: Intent
        intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        } else {
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        }
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取 APP 悬浮窗口权限详情页的意图
     * @return APP 悬浮窗口权限详情页的意图
     */
    val manageOverlayPermissionIntent: Intent?
        get() = getManageOverlayPermissionIntent(false)

    /**
     * 获取 APP 悬浮窗口权限详情页的意图
     * @param isNewTask 是否开启新的任务栈
     * @return APP 悬浮窗口权限详情页的意图
     */
    fun getManageOverlayPermissionIntent(isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + AppUtils.packageName)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getManageOverlayPermissionIntent")
        }
        return null
    }

    /**
     * 获取 APP 具体设置的意图
     * @return APP 具体设置的意图
     */
    @JvmStatic
    val launchAppDetailsSettingsIntent: Intent?
        get() = getLaunchAppDetailsSettingsIntent(AppUtils.packageName, false)

    /**
     * 获取 APP 具体设置的意图
     * @param packageName 应用包名
     * @return APP 具体设置的意图
     */
    fun getLaunchAppDetailsSettingsIntent(packageName: String): Intent? {
        return getLaunchAppDetailsSettingsIntent(packageName, false)
    }

    /**
     * 获取 APP 具体设置的意图
     * @param packageName 应用包名
     * @param isNewTask   是否开启新的任务栈
     * @return APP 具体设置的意图
     */
    fun getLaunchAppDetailsSettingsIntent(packageName: String, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getLaunchAppDetailsSettingsIntent")
        }
        return null
    }

    /**
     * 获取到应用商店 APP 详情界面的意图
     * @param packageName 应用包名
     * @param marketPkg   应用商店包名, 如果为 ""  则由系统弹出应用商店列表供用户选择, 否则调转到目标市场的应用详情界面, 某些应用商店可能会失败
     * @return 到应用商店 APP 详情界面的意图
     */
    fun getLaunchAppDetailIntent(packageName: String, marketPkg: String?): Intent? {
        return getLaunchAppDetailIntent(packageName, marketPkg, false)
    }

    /**
     * 获取到应用商店 APP 详情界面的意图
     * @param packageName 应用包名
     * @param marketPkg   应用商店包名, 如果为 ""  则由系统弹出应用商店列表供用户选择, 否则调转到目标市场的应用详情界面, 某些应用商店可能会失败
     * @param isNewTask   是否开启新的任务栈
     * @return 到应用商店 APP 详情界面的意图
     */
    @JvmStatic
    fun getLaunchAppDetailIntent(
        packageName: String,
        marketPkg: String?,
        isNewTask: Boolean
    ): Intent? {
        try {
            if (TextUtils.isEmpty(packageName)) return null
            val uri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg)
            }
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getLaunchAppDetailIntent")
        }
        return null
    }
    // =
    /**
     * 获取分享文本的意图
     * @param content 分享文本
     * @return 分享文本的意图
     */
    fun getShareTextIntent(content: String?): Intent? {
        return getShareTextIntent(content, false)
    }

    /**
     * 获取分享文本的意图
     * @param content   分享文本
     * @param isNewTask 是否开启新的任务栈
     * @return 分享文本的意图
     */
    fun getShareTextIntent(content: String?, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, content)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getShareTextIntent")
        }
        return null
    }

    /**
     * 获取分享图片的意图
     * @param content   文本
     * @param imagePath 图片文件路径
     * @return 分享图片的意图
     */
    fun getShareImageIntent(content: String?, imagePath: String?): Intent? {
        return getShareImageIntent(content, imagePath, false)
    }

    /**
     * 获取分享图片的意图
     * @param content   文本
     * @param imagePath 图片文件路径
     * @param isNewTask 是否开启新的任务栈
     * @return 分享图片的意图
     */
    fun getShareImageIntent(content: String?, imagePath: String?, isNewTask: Boolean): Intent? {
        try {
            return getShareImageIntent(content, FileUtils.getFileByPath(imagePath), isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getShareImageIntent")
        }
        return null
    }

    /**
     * 获取分享图片的意图
     * @param content 文本
     * @param image   图片文件
     * @return 分享图片的意图
     */
    fun getShareImageIntent(content: String?, image: File?): Intent? {
        return getShareImageIntent(content, image, false)
    }

    /**
     * 获取分享图片的意图
     * @param content   文本
     * @param image     图片文件
     * @param isNewTask 是否开启新的任务栈
     * @return 分享图片的意图
     */
    fun getShareImageIntent(content: String?, image: File?, isNewTask: Boolean): Intent? {
        try {
            return getShareImageIntent(content, Uri.fromFile(image), isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getShareImageIntent")
        }
        return null
    }

    /**
     * 获取分享图片的意图
     * @param content 分享文本
     * @param uri     图片 uri
     * @return 分享图片的意图
     */
    fun getShareImageIntent(content: String?, uri: Uri?): Intent? {
        return getShareImageIntent(content, uri, false)
    }

    /**
     * 获取分享图片的意图
     * @param content   分享文本
     * @param uri       图片 uri
     * @param isNewTask 是否开启新的任务栈
     * @return 分享图片的意图
     */
    fun getShareImageIntent(content: String?, uri: Uri?, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, content)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/*"
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getShareImageIntent")
        }
        return null
    }

    /**
     * 获取其他应用组件的意图
     * @param packageName 应用包名
     * @param className   class.getCanonicalName()
     * @return 其他应用组件的意图
     */
    fun getComponentIntent(packageName: String, className: String): Intent? {
        return getComponentIntent(packageName, className, null, false)
    }

    /**
     * 获取其他应用组件的意图
     * @param packageName 应用包名
     * @param className   class.getCanonicalName()
     * @param isNewTask   是否开启新的任务栈
     * @return 其他应用组件的意图
     */
    fun getComponentIntent(packageName: String, className: String, isNewTask: Boolean): Intent? {
        return getComponentIntent(packageName, className, null, isNewTask)
    }

    /**
     * 获取其他应用组件的意图
     * @param packageName 应用包名
     * @param className   class.getCanonicalName()
     * @param bundle      [Bundle]
     * @return 其他应用组件的意图
     */
    fun getComponentIntent(packageName: String, className: String, bundle: Bundle?): Intent? {
        return getComponentIntent(packageName, className, bundle, false)
    }

    /**
     * 获取其他应用组件的意图
     * @param packageName 应用包名
     * @param className   class.getCanonicalName()
     * @param bundle      [Bundle]
     * @param isNewTask   是否开启新的任务栈
     * @return 其他应用组件的意图
     */
    fun getComponentIntent(
        packageName: String,
        className: String,
        bundle: Bundle?,
        isNewTask: Boolean
    ): Intent? {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            if (bundle != null) intent.putExtras(bundle)
            val componentName = ComponentName(packageName, className)
            intent.component = componentName
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getComponentIntent")
        }
        return null
    }



    /**
     * 获取关机的意图
     * @return 关机的意图
     */
    val shutdownIntent: Intent?
        get() = getShutdownIntent(false)

    /**
     * 获取关机的意图
     * @param isNewTask 是否开启新的任务栈
     * @return 关机的意图
     */
    fun getShutdownIntent(isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Intent.ACTION_SHUTDOWN)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getShutdownIntent")
        }
        return null
    }

    /**
     * 获取跳至拨号界面意图
     * @param phoneNumber 电话号码
     * @return 跳至拨号界面意图
     */
    fun getDialIntent(phoneNumber: String): Intent? {
        return getDialIntent(phoneNumber, false)
    }

    /**
     * 获取跳至拨号界面意图
     * @param phoneNumber 电话号码
     * @param isNewTask   是否开启新的任务栈
     * @return 跳至拨号界面意图
     */
    fun getDialIntent(phoneNumber: String, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getDialIntent")
        }
        return null
    }

    /**
     * 获取拨打电话意图
     * @param phoneNumber 电话号码
     * @return 拨打电话意图
     */
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun getCallIntent(phoneNumber: String): Intent? {
        return getCallIntent(phoneNumber, false)
    }

    /**
     * 获取拨打电话意图
     * @param phoneNumber 电话号码
     * @param isNewTask   是否开启新的任务栈
     * @return 拨打电话意图
     */
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun getCallIntent(phoneNumber: String, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getCallIntent")
        }
        return null
    }

    /**
     * 获取发送短信界面的意图
     * @param phoneNumber 接收号码
     * @param content     短信内容
     * @return 发送短信界面的意图
     */
    fun getSendSmsIntent(phoneNumber: String, content: String?): Intent? {
        return getSendSmsIntent(phoneNumber, content, false)
    }

    /**
     * 获取跳至发送短信界面的意图
     * @param phoneNumber 接收号码
     * @param content     短信内容
     * @param isNewTask   是否开启新的任务栈
     * @return 发送短信界面的意图
     */
    fun getSendSmsIntent(phoneNumber: String, content: String?, isNewTask: Boolean): Intent? {
        try {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", content)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getSendSmsIntent")
        }
        return null
    }

    /**
     * 获取拍照的意图
     * @param outUri 输出的 uri ( 保存地址 )
     * @return 拍照的意图
     */
    fun getCaptureIntent(outUri: Uri?): Intent? {
        return getCaptureIntent(outUri, false)
    }

    /**
     * 获取拍照的意图
     * @param outUri    输出的 uri ( 保存地址 )
     * @param isNewTask 是否开启新的任务栈
     * @return 拍照的意图
     */
    fun getCaptureIntent(outUri: Uri?, isNewTask: Boolean): Intent? {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getCaptureIntent")
        }
        return null
    }

    /**
     * 获取存储访问框架的意图
     * @return 存储访问框架的意图
     */
    val openDocumentIntent: Intent?
        get() = getOpenDocumentIntent("*/*")

    /**
     * 获取存储访问框架的意图
     * @param type 跳转类型
     * @return 存储访问框架的意图
     */
    fun getOpenDocumentIntent(type: String?): Intent? {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = type
            return intent
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getOpenDocumentIntent")
        }
        return null
    }

    /**
     * 获取创建文件的意图
     * <pre>
     * getCreateDocumentIntent("text/plain", "foobar.txt");
     * getCreateDocumentIntent("image/png", "mypicture.png");
     *
     *
     * 创建后在 onActivityResult 中获取到 Uri, 对 Uri 进行读写
    </pre> *
     * @param mimeType 资源类型
     * @param fileName 文件名
     * @return 创建文件的意图
     */
    fun getCreateDocumentIntent(mimeType: String?, fileName: String?): Intent? {
        try {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = mimeType
            intent.putExtra(Intent.EXTRA_TITLE, fileName)
            return intent
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getCreateDocumentIntent")
        }
        return null
    }

    /**
     * 获取打开浏览器的意图
     * <pre>
     * Uri uri = Uri.parse("https://www.baidu.com")
     * 如果手机本身安装了多个浏览器而又没有设置默认浏览器的话, 系统将让用户选择使用哪个浏览器来打开链接
    </pre> *
     * @param uri       链接地址
     * @param isNewTask 是否开启新的任务栈
     * @return 打开浏览器的意图
     */
    fun getOpenBrowserIntent(uri: Uri?, isNewTask: Boolean): Intent? {
        return getOpenBrowserIntent(uri, "", "", isNewTask)
    }

    /**
     * 获取打开 Android 浏览器的意图
     * @param uri       链接地址
     * @param isNewTask 是否开启新的任务栈
     * @return 打开 Android 浏览器的意图
     */
    fun getOpenAndroidBrowserIntent(uri: Uri?, isNewTask: Boolean): Intent? {
        return getOpenBrowserIntent(
            uri,
            "com.android.browser",
            "com.android.browser.BrowserActivity",
            isNewTask
        )
    }

    /**
     * 获取打开指定浏览器的意图
     * <pre>
     * 打开指定浏览器, 如:
     * intent.setClassName("com.UCMobile", "com.uc.browser.InnerUCMobile"); // 打开 UC 浏览器
     * intent.setClassName("com.tencent.mtt", "com.tencent.mtt.MainActivity"); // 打开 QQ 浏览器
     * intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity"); // 系统指定浏览器
    </pre> *
     * @param uri         链接地址
     * @param packageName 应用包名
     * @param className   完整类名 ( 可不传 )
     * @param isNewTask   是否开启新的任务栈
     * @return 打开指定浏览器的意图
     */
    fun getOpenBrowserIntent(
        uri: Uri?,
        packageName: String,
        className: String,
        isNewTask: Boolean
    ): Intent? {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            if (!TextUtils.isEmpty(packageName)) {
//                intent.setClassName(packageName, className);
                val lists = AppUtils.packageManager
                    .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                val browsers = HashMap<String, String>()
                for (resolveInfo in lists) {
                    val activityInfo = resolveInfo.activityInfo
                    if (activityInfo != null) { // 包名, Activity Name
                        browsers[activityInfo.packageName] = activityInfo.targetActivity
                    }
                }
                if (browsers.containsKey(packageName)) {
                    if (TextUtils.isEmpty(className)) {
                        intent.component = ComponentName(packageName, browsers[packageName]?:"")
                    } else {
                        intent.component = ComponentName(packageName, className)
                    }
                }
            }
            return getIntent(intent, isNewTask)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getOpenBrowserIntent")
        }
        return null
    }
}