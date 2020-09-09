package com.example.commlib.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.usage.UsageStatsManager
import android.content.*
import android.content.pm.*
import android.graphics.drawable.Drawable
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.os.Vibrator
import android.os.storage.StorageManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresPermission
import com.blankj.ALog
import com.example.commlib.api.App.Companion.instance
import com.example.commlib.utils.FileUtils.getFileByPath
import com.example.commlib.utils.FileUtils.isFileExists
import com.example.commlib.utils.IntentUtils.getInstallAppIntent
import com.example.commlib.utils.IntentUtils.getIntent
import com.example.commlib.utils.IntentUtils.getLaunchAppDetailIntent
import com.example.commlib.utils.IntentUtils.getLaunchAppDetailsSettingsIntent
import com.example.commlib.utils.IntentUtils.getLaunchAppIntent
import com.example.commlib.utils.IntentUtils.getUninstallAppIntent
import com.example.commlib.utils.StringUtil.colonSplit
import com.example.commlib.utils.encrypt.EncryptUtils
import java.io.File

/**
 * detail: APP (Android) 工具类
 * @author Ttt
 * <pre>
 * MimeType
 * @see [](https://www.jianshu.com/p/f3fcf033be5c)
 *
 *
 * 所需权限
 * <uses-permission android:name="android.permission.INSTALL_PACKAGES"></uses-permission>
</pre> *
 */
object AppUtils {
    // 日志 TAG
    private val TAG = AppUtils::class.java.simpleName

    /**
     * 获取 WindowManager
     * @return [WindowManager]
     */
    val windowManager: WindowManager
        get() = getSystemService<WindowManager>(Context.WINDOW_SERVICE)!!

    /**
     * 获取 AudioManager
     * @return [AudioManager]
     */
    val audioManager: AudioManager
        get() = getSystemService<AudioManager>(Context.AUDIO_SERVICE)!!

    /**
     * 获取 SensorManager
     * @return [SensorManager]
     */
    val sensorManager: SensorManager
        get() = getSystemService<SensorManager>(Context.SENSOR_SERVICE)!!

    /**
     * 获取 StorageManager
     * @return [StorageManager]
     */
    val storageManager: StorageManager
        get() = getSystemService<StorageManager>(Context.STORAGE_SERVICE)!!

    /**
     * 获取 WifiManager
     * @return [WifiManager]
     */
    @JvmStatic
    @get:SuppressLint("WifiManagerLeak")
    val wifiManager: WifiManager
        get() = getSystemService<WifiManager>(Context.WIFI_SERVICE)!!

    /**
     * 获取 ConnectivityManager
     * @return [ConnectivityManager]
     */
    @JvmStatic
    val connectivityManager: ConnectivityManager
        get() = getSystemService<ConnectivityManager>(Context.CONNECTIVITY_SERVICE)!!

    /**
     * 获取 TelephonyManager
     * @return [TelephonyManager]
     */
    val telephonyManager: TelephonyManager
        get() = getSystemService<TelephonyManager>(Context.TELEPHONY_SERVICE)!!

    /**
     * 获取 AppOpsManager
     * @return [AppOpsManager]
     */
    val appOpsManager: AppOpsManager
        get() = getSystemService<AppOpsManager>(Context.APP_OPS_SERVICE)!!

    /**
     * 获取 NotificationManager
     * @return [NotificationManager]
     */
    val notificationManager: NotificationManager
        get() = getSystemService<NotificationManager>(Context.NOTIFICATION_SERVICE)!!

    /**
     * 获取 ShortcutManager
     * @return [ShortcutManager]
     */
    val shortcutManager: ShortcutManager
        get() = getSystemService<ShortcutManager>(Context.SHORTCUT_SERVICE)!!

    /**
     * 获取 ActivityManager
     * @return [ActivityManager]
     */
    val activityManager: ActivityManager
        get() = getSystemService<ActivityManager>(Context.ACTIVITY_SERVICE)!!

    /**
     * 获取 PowerManager
     * @return [PowerManager]
     */
    @JvmStatic
    val powerManager: PowerManager
        get() = getSystemService<PowerManager>(Context.POWER_SERVICE)!!

    /**
     * 获取 KeyguardManager
     * @return [KeyguardManager]
     */
    val keyguardManager: KeyguardManager
        get() = getSystemService<KeyguardManager>(Context.KEYGUARD_SERVICE)!!

    /**
     * 获取 InputMethodManager
     * @return [InputMethodManager]
     */
    val inputMethodManager: InputMethodManager
        get() = getSystemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE)!!

    /**
     * 获取 ClipboardManager
     * @return [ClipboardManager]
     */
    val clipboardManager: ClipboardManager
        get() = getSystemService<ClipboardManager>(Context.CLIPBOARD_SERVICE)!!

    /**
     * 获取 UsageStatsManager
     * @return [UsageStatsManager]
     */
    val usageStatsManager: UsageStatsManager
        get() = getSystemService<UsageStatsManager>(Context.USAGE_STATS_SERVICE)!!

    /**
     * 获取 AlarmManager
     * @return [AlarmManager]
     */
    val alarmManager: AlarmManager
        get() = getSystemService<AlarmManager>(Context.ALARM_SERVICE)!!

    /**
     * 获取 LocationManager
     * @return [LocationManager]
     */
    val locationManager: LocationManager
        get() = getSystemService<LocationManager>(Context.LOCATION_SERVICE)!!

    /**
     * 获取 Vibrator
     * @return [Vibrator]
     */
    val vibrator: Vibrator
        get() = getSystemService<Vibrator>(Context.VIBRATOR_SERVICE)!!

    /**
     * 获取 SystemService
     * @param name 服务名
     * @param <T>  泛型
     * @return SystemService Object
    </T> */
    fun <T> getSystemService(name: String?): T? {
        if (CommUtils.isEmpty(name)) return null
        try {
            return instance.getSystemService(name!!) as T
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getSystemService")
        }
        return null
    }

    /**
     * 获取 PackageManager
     * @return [PackageManager]
     */
    @JvmStatic
    val packageManager: PackageManager=instance.packageManager


    /**
     * 获取 ApplicationInfo
     * @return [ApplicationInfo]
     */
    val applicationInfo: ApplicationInfo?
        get() {
            try {
                return instance.applicationInfo
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getApplicationInfo")
            }
            return null
        }

    /**
     * 获取 ApplicationInfo
     * @param packageName 应用包名
     * @param flags       application flags
     * @return [ApplicationInfo]
     */
    fun getApplicationInfo(packageName: String?, flags: Int): ApplicationInfo? {
        try {
            return instance.packageManager.getApplicationInfo(packageName, flags)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getApplicationInfo - $packageName")
        }
        return null
    }

    /**
     * 获取 PackageInfo
     * @param flags package flags
     * @return [ApplicationInfo]
     */
    fun getPackageInfo(flags: Int): PackageInfo? {
        return getPackageInfo(packageName, flags)
    }

    /**
     * 获取 PackageInfo
     * @param packageName 应用包名
     * @param flags       package flags
     * @return [ApplicationInfo]
     */
    fun getPackageInfo(packageName: String?, flags: Int): PackageInfo? {
        try {
            return instance.packageManager.getPackageInfo(packageName, flags)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getPackageInfo - $packageName")
        }
        return null
    }

    /**
     * 获取 SharedPreferences
     * @param fileName 文件名
     * @return [SharedPreferences]
     */
    fun getSharedPreferences(fileName: String): SharedPreferences? {
        return getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    /**
     * 获取 SharedPreferences
     * @param fileName 文件名
     * @param mode     SharedPreferences 操作模式
     * @return [SharedPreferences]
     */
    fun getSharedPreferences(fileName: String, mode: Int): SharedPreferences? {
        try {
            return instance.getSharedPreferences(fileName, mode)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getSharedPreferences - $fileName")
        }
        return null
    }
    // ============
    // = APP 相关 =
    // ============
    /**
     * 根据名称清除数据库
     * @param dbName 数据库名
     * @return `true` success, `false` fail
     */
    fun deleteDatabase(dbName: String?): Boolean {
        try {
            return instance.deleteDatabase(dbName)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "deleteDatabase")
        }
        return false
    }

    /**
     * 获取 APP 包名
     * @return APP 包名
     */
    @JvmStatic
    val packageName: String
        get() {
            try {
                return instance.packageName
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getPackageName")
            }
            return "null"
        }

    /**
     * 获取 APP 图标
     * @return [Drawable]
     */
    val appIcon: Drawable?
        get() = getAppIcon(packageName)

    /**
     * 获取 APP 图标
     * @param packageName 应用包名
     * @return [Drawable]
     */
    fun getAppIcon(packageName: String?): Drawable? {
        return if (CommUtils.isEmpty(packageName)) null else try {
            val packageManager = packageManager
            val packageInfo = packageManager!!.getPackageInfo(packageName, 0)
            packageInfo?.applicationInfo?.loadIcon(packageManager)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getAppIcon")
            null
        }
    }

    /**
     * 获取 APP 应用名
     * @return APP 应用名
     */
    val appName: String?
        get() = getAppName(packageName)

    /**
     * 获取 APP 应用名
     * @param packageName 应用包名
     * @return APP 应用名
     */
    fun getAppName(packageName: String?): String? {
        return if (CommUtils.isEmpty(packageName)) null else try {
            val packageManager = packageManager
            val packageInfo = packageManager!!.getPackageInfo(packageName, 0)
            packageInfo?.applicationInfo?.loadLabel(packageManager)?.toString()
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getAppName")
            null
        }
    }

    /**
     * 获取 APP versionName
     * @return APP versionName
     */
    val appVersionName: String?
        get() = getAppVersionName(packageName)

    /**
     * 获取 APP versionName
     * @param packageName 应用包名
     * @return APP versionName
     */
    fun getAppVersionName(packageName: String?): String? {
        return if (CommUtils.isEmpty(packageName)) null else try {
            val packageInfo = getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            packageInfo?.versionName
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getAppVersionName")
            null
        }
    }

    /**
     * 获取 APP versionCode
     * @return APP versionCode
     */
    val appVersionCode: Long
        get() = getAppVersionCode(packageName)

    /**
     * 获取 APP versionCode
     * @param packageName 应用包名
     * @return APP versionCode
     */
    fun getAppVersionCode(packageName: String?): Long {
        if (CommUtils.isEmpty(packageName)) return -1;
        try {
            val packageInfo = getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packageInfo?.longVersionCode?:-1
            } else {
                return packageInfo?.versionCode?.toLong()?:-1
            }
        } catch (e:Exception) {
            ALog.eTag(TAG, e, "getAppVersionCode");
            return -1;
        }
    }

    /**
     * 获取 APP 安装包路径 /data/data/packageName/.apk
     * @return APP 安装包路径
     */
    val appPath: String?
        get() = getAppPath(packageName)

    /**
     * 获取 APP 安装包路径 /data/data/packageName/.apk
     * @param packageName 应用包名
     * @return APP 安装包路径
     */
    fun getAppPath(packageName: String?): String? {
        return if (CommUtils.isEmpty(packageName)) null else try {
            val packageInfo = getPackageInfo(packageName, 0)
            packageInfo?.applicationInfo?.sourceDir
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getAppPath")
            null
        }
    }
    // =
    /**
     * 获取 APP Signature
     * @return [Signature] 数组
     */
    val appSignature: Array<Signature>?
        get() = getAppSignature(packageName)

    /**
     * 获取 APP Signature
     * @param packageName 应用包名
     * @return [Signature] 数组
     */
    fun getAppSignature(packageName: String?): Array<Signature>? {
        return if (CommUtils.isEmpty(packageName)) null else try {
            val packageInfo = getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            packageInfo?.signatures
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getAppSignature")
            null
        }
    }
    // =
    /**
     * 获取 APP 签名 MD5 值
     * @return APP 签名 MD5 值
     */
    val appSignatureMD5: String?
        get() = getAppSignatureMD5(packageName)

    /**
     * 获取 APP 签名 MD5 值
     * @param packageName 应用包名
     * @return APP 签名 MD5 值
     */
    fun getAppSignatureMD5(packageName: String?): String? {
        return getAppSignatureHash(packageName, "MD5")
    }

    /**
     * 获取 APP 签名 SHA1 值
     * @return APP 签名 SHA1 值
     */
    val appSignatureSHA1: String?
        get() = getAppSignatureSHA1(packageName)

    /**
     * 获取 APP 签名 SHA1 值
     * @param packageName 应用包名
     * @return APP 签名 SHA1 值
     */
    fun getAppSignatureSHA1(packageName: String?): String? {
        return getAppSignatureHash(packageName, "SHA1")
    }

    /**
     * 获取 APP 签名 SHA256 值
     * @return APP 签名 SHA256 值
     */
    val appSignatureSHA256: String?
        get() = getAppSignatureSHA256(packageName)

    /**
     * 获取 APP 签名 SHA256 值
     * @param packageName 应用包名
     * @return APP 签名 SHA256 值
     */
    fun getAppSignatureSHA256(packageName: String?): String? {
        return getAppSignatureHash(packageName, "SHA256")
    }

    /**
     * 获取应用签名 Hash 值
     * @param packageName 应用包名
     * @param algorithm   算法
     * @return 对应算法处理后的签名信息
     */
    fun getAppSignatureHash(packageName: String?, algorithm: String): String? {
        return if (CommUtils.isEmpty(packageName)) null else try {
            val signature = getAppSignature(packageName)
            if (signature == null || signature.size == 0) null else colonSplit(
                ConvertUtils.toHexString(
                    EncryptUtils.hashTemplate(
                        signature[0].toByteArray(), algorithm
                    )
                )
            )
        } catch (e: Exception) {
            ALog.eTag(
                TAG,
                e,
                "getAppSignatureHash - packageName: $packageName, algorithm: $algorithm"
            )
            null
        }
    }
    // =
    /**
     * 判断 APP 是否 debug 模式
     * @return `true` yes, `false` no
     */
    val isAppDebug: Boolean
        get() = isAppDebug(packageName)

    /**
     * 判断 APP 是否 debug 模式
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    fun isAppDebug(packageName: String?): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else try {
            val appInfo = getApplicationInfo(packageName, 0)
            appInfo != null && appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isAppDebug")
            false
        }
    }

    /**
     * 判断 APP 是否 release 模式
     * @return `true` yes, `false` no
     */
    val isAppRelease: Boolean
        get() = isAppRelease(packageName)

    /**
     * 判断 APP 是否 release 模式
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    fun isAppRelease(packageName: String?): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else try {
            val appInfo = getApplicationInfo(packageName, 0)
            !(appInfo != null && appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isAppRelease")
            false
        }
    }
    // =
    /**
     * 判断 APP 是否系统 app
     * @return `true` yes, `false` no
     */
    val isAppSystem: Boolean
        get() = isAppSystem(packageName)

    /**
     * 判断 APP 是否系统 app
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    fun isAppSystem(packageName: String?): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else try {
            val appInfo = getApplicationInfo(packageName, 0)
            appInfo != null && appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isAppSystem")
            false
        }
    }

    /**
     * 判断 APP 是否在前台
     * @return `true` yes, `false` no
     */
    @get:RequiresPermission(Manifest.permission.PACKAGE_USAGE_STATS)
    val isAppForeground: Boolean
        get() = isAppForeground(packageName)

    /**
     * 判断 APP 是否在前台
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    @RequiresPermission(Manifest.permission.PACKAGE_USAGE_STATS)
    fun isAppForeground(packageName: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        try {
            val lists = activityManager.runningAppProcesses
            if (lists != null && lists.size > 0) {
                for (appProcess in lists) {
                    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return appProcess.processName == packageName
                    }
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isAppForeground")
        }
        return false
    }
    // =
    /**
     * 判断是否安装了 APP
     * @param action   Action
     * @param category Category
     * @return `true` yes, `false` no
     */
    fun isInstalledApp(action: String?, category: String?): Boolean {
        return try {
            val intent = Intent(action)
            intent.addCategory(category)
            val resolveinfo = packageManager!!.resolveActivity(intent, 0)
            resolveinfo != null
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isInstalledApp")
            false
        }
    }

    /**
     * 判断是否安装了 APP
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    fun isInstalledApp(packageName: String?): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else try {
            val appInfo = getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
            appInfo != null
        } catch (e: Exception) { // 未安装, 则会抛出异常
            ALog.eTag(TAG, e, "isInstalledApp")
            false
        }
    }

    /**
     * 判断是否安装了 APP
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    fun isInstalledApp2(packageName: String?): Boolean {
        return !CommUtils.isEmpty(packageName) && getLaunchAppIntent(packageName) != null
    }
    // =================
    // = Activity 跳转 =
    // =================
    /**
     * Activity 跳转
     * @param intent [Intent]
     * @return `true` success, `false` fail
     */
    @JvmStatic
    fun startActivity(intent: Intent?): Boolean {
        if (intent == null) return false
        try {
            instance.startActivity(getIntent(intent, true))
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startActivity")
        }
        return false
    }

    /**
     * Activity 跳转回传
     * @param activity    [Activity]
     * @param intent      [Intent]
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun startActivityForResult(activity: Activity?, intent: Intent?, requestCode: Int): Boolean {
        if (activity == null || intent == null) return false
        try {
            activity.startActivityForResult(intent, requestCode)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startActivityForResult")
        }
        return false
    }
    // ========
    // = 广播 =
    // ========
    /**
     * 注册广播监听
     * @param receiver {@linkBroadcastReceiver}
     * @param filter   [IntentFilter]
     * @return `true` success, `false` fail
     */
    fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Boolean {
        if (receiver == null || filter == null) return false
        try {
            instance.registerReceiver(receiver, filter)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "registerReceiver")
        }
        return false
    }

    /**
     * 注销广播监听
     * @param receiver {@linkBroadcastReceiver}
     * @return `true` success, `false` fail
     */
    fun unregisterReceiver(receiver: BroadcastReceiver?): Boolean {
        if (receiver == null) return false
        try {
            instance.unregisterReceiver(receiver)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "unregisterReceiver")
        }
        return false
    }
    // ============
    // = 发送广播 =
    // ============
    /**
     * 发送广播
     * @param intent [Intent]
     * @return `true` success, `false` fail
     */
    fun sendBroadcast(intent: Intent?): Boolean {
        if (intent == null) return false
        try {
            instance.sendBroadcast(intent)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "sendBroadcast")
        }
        return false
    }

    /**
     * 发送广播
     * @param intent             [Intent]
     * @param receiverPermission 广播权限
     * @return `true` success, `false` fail
     */
    fun sendBroadcast(intent: Intent?, receiverPermission: String?): Boolean {
        if (intent == null || receiverPermission == null) return false
        try {
            instance.sendBroadcast(intent, receiverPermission)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "sendBroadcast")
        }
        return false
    }
    // ========
    // = 服务 =
    // ========
    /**
     * 启动服务
     * @param intent [Intent]
     * @return `true` success, `false` fail
     */
    fun startService(intent: Intent?): Boolean {
        if (intent == null) return false
        try {
            instance.startService(intent)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startService")
        }
        return false
    }

    /**
     * 停止服务
     * @param intent [Intent]
     * @return `true` success, `false` fail
     */
    fun stopService(intent: Intent?): Boolean {
        if (intent == null) return false
        try {
            instance.stopService(intent)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "stopService")
        }
        return false
    }
    // ==============
    // = 安装、卸载 =
    // ==============
    /**
     * 安装 APP( 支持 8.0) 的意图
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun installApp(filePath: String?): Boolean {
        return installApp(getFileByPath(filePath))
    }

    /**
     * 安装 APP( 支持 8.0) 的意图
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun installApp(file: File?): Boolean {
        return if (!isFileExists(file)) false else try {
            startActivity(getInstallAppIntent(file, true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "installApp")
            false
        }
    }

    /**
     * 安装 APP( 支持 8.0) 的意图
     * @param activity    [Activity]
     * @param filePath    文件路径
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun installApp(activity: Activity, filePath: String?, requestCode: Int): Boolean {
        return installApp(activity, getFileByPath(filePath), requestCode)
    }

    /**
     * 安装 APP( 支持 8.0) 的意图
     * @param activity    [Activity]
     * @param file        文件
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun installApp(activity: Activity, file: File?, requestCode: Int): Boolean {
        return if (!isFileExists(file)) false else try {
            activity.startActivityForResult(getInstallAppIntent(file), requestCode)
            true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "installApp")
            false
        }
    }
    /**
     * 静默安装应用
     * @param filePath 文件路径
     * @param params   安装参数
     * @return `true` success, `false` fail
     */
    // =
    /**
     * 静默安装应用
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun installAppSilent(filePath: String?, params: String? = null): Boolean {
        return installAppSilent(getFileByPath(filePath), params, ADBUtils.isDeviceRooted)
    }
    /**
     * 静默安装应用
     * @param file     文件
     * @param params   安装参数
     * @param isRooted 是否 root
     * @return `true` success, `false` fail
     */
    /**
     * 静默安装应用
     * @param file   文件
     * @param params 安装参数
     * @return `true` success, `false` fail
     */
    /**
     * 静默安装应用
     * @param file 文件
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun installAppSilent(
        file: File?,
        params: String? = null,
        isRooted: Boolean = ADBUtils.isDeviceRooted
    ): Boolean {
        if (!isFileExists(file)) return false
        val filePath = '"'.toString() + file!!.absolutePath + '"'
        val command =
            "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " + (if (params == null) "" else "$params ") + filePath
        val result = ShellUtils.execCmd(command, isRooted)
        return result.isSuccess4("success")
    }
    // =
    /**
     * 卸载应用
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    fun uninstallApp(packageName: String?): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else try {
            startActivity(getUninstallAppIntent(packageName!!, true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "uninstallApp")
            false
        }
    }

    /**
     * 卸载应用
     * @param activity    [Activity]
     * @param packageName 应用包名
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun uninstallApp(activity: Activity, packageName: String?, requestCode: Int): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else try {
            activity.startActivityForResult(getUninstallAppIntent(packageName!!), requestCode)
            true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "uninstallApp")
            false
        }
    }
    /**
     * 静默卸载应用
     * @param packageName 应用包名
     * @param isKeepData  true 表示卸载应用但保留数据和缓存目录
     * @param isRooted    是否 root
     * @return `true` success, `false` fail
     */
    /**
     * 静默卸载应用
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    /**
     * 静默卸载应用
     * @param packageName 应用包名
     * @param isKeepData  true 表示卸载应用但保留数据和缓存目录
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun uninstallAppSilent(
        packageName: String,
        isKeepData: Boolean = false,
        isRooted: Boolean = ADBUtils.isDeviceRooted
    ): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        val command =
            "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall " + (if (isKeepData) "-k " else "") + packageName
        val result = ShellUtils.execCmd(command, isRooted)
        return result.isSuccess4("success")
    }
    // ============
    // = 操作相关 =
    // ============
    /**
     * 打开 APP
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    fun launchApp(packageName: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        try {
            return startActivity(getLaunchAppIntent(packageName, true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "launchApp")
        }
        return false
    }

    /**
     * 打开 APP
     * @param activity    [Activity]
     * @param packageName 应用包名
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun launchApp(activity: Activity, packageName: String?, requestCode: Int): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        try {
            activity.startActivityForResult(getLaunchAppIntent(packageName), requestCode)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "launchApp")
        }
        return false
    }
    /**
     * 跳转到 APP 设置详情页面
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    // =
    /**
     * 跳转到 APP 设置详情页面
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun launchAppDetailsSettings(packageName: String? = this.packageName): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        try {
            return startActivity(getLaunchAppDetailsSettingsIntent(packageName!!, true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "launchAppDetailsSettings")
        }
        return false
    }

    /**
     * 跳转到 APP 应用商城详情页面
     * @param marketPkg 应用商店包名, 如果为 ""  则由系统弹出应用商店列表供用户选择, 否则调转到目标市场的应用详情界面, 某些应用商店可能会失败
     * @return `true` success, `false` fail
     */
    fun launchAppDetails(marketPkg: String?): Boolean {
        return launchAppDetails(packageName, marketPkg)
    }

    /**
     * 跳转到 APP 应用商城详情页面
     * @param packageName 应用包名
     * @param marketPkg   应用商店包名, 如果为 ""  则由系统弹出应用商店列表供用户选择, 否则调转到目标市场的应用详情界面, 某些应用商店可能会失败
     * @return `true` success, `false` fail
     */
    fun launchAppDetails(packageName: String?, marketPkg: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        try {
            return startActivity(getLaunchAppDetailIntent(packageName!!, marketPkg, true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "launchAppDetails")
        }
        return false
    }
    // ============
    // = 其他功能 =
    // ============
    /**
     * 打开文件
     * @param filePath 文件路径
     * @param dataType 数据类型
     * @return `true` success, `false` fail
     */
    fun openFile(filePath: String?, dataType: String?): Boolean {
        return openFile(getFileByPath(filePath), dataType)
    }

    /**
     * 打开文件
     * @param file     文件
     * @param dataType 数据类型
     * @return `true` success, `false` fail
     */
    fun openFile(file: File?, dataType: String?): Boolean {
        if (!isFileExists(file)) return false
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 临时授权 ( 必须 )
            intent.setDataAndType(CommUtils.getUriForFile(file), dataType)
            return startActivity(intent)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openFile")
        }
        return false
    }
    // =
    /**
     * 打开文件 - 指定应用
     * @param filePath    文件路径
     * @param packageName 应用包名
     * @param className   Activity.class.getCanonicalName()
     * @return `true` success, `false` fail
     */
    fun openFileByApp(filePath: String?, packageName: String, className: String): Boolean {
        return openFileByApp(getFileByPath(filePath), packageName, className)
    }

    /**
     * 打开文件 - 指定应用
     * @param file        文件
     * @param packageName 应用包名
     * @param className   Activity.class.getCanonicalName()
     * @return `true` success, `false` fail
     */
    fun openFileByApp(file: File?, packageName: String, className: String): Boolean {
        if (!isFileExists(file)) return false
        try {
            val intent = Intent()
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.fromFile(file)
            intent.setClassName(packageName, className)
            return startActivity(intent)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openFile")
        }
        return false
    }
    // =
    /**
     * 打开 PDF 文件
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun openPDFFile(filePath: String?): Boolean {
        return openPDFFile(getFileByPath(filePath))
    }

    /**
     * 打开 PDF 文件
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun openPDFFile(file: File?): Boolean {
        return openFile(file, "application/pdf")
    }
    // =
    /**
     * 打开 Word 文件
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun openWordFile(filePath: String?): Boolean {
        return openWordFile(getFileByPath(filePath))
    }

    /**
     * 打开 Word 文件
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun openWordFile(file: File?): Boolean {
        return openFile(file, "application/msword")
    }
    // =
    /**
     * 调用 WPS 打开 office 文档
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun openOfficeByWPS(filePath: String?): Boolean {
        return openOfficeByWPS(getFileByPath(filePath))
    }

    /**
     * 调用 WPS 打开 office 文档
     * @param file 文件
     * @return `true` success, `false` fail
     */
    fun openOfficeByWPS(file: File?): Boolean {
        val wpsPackage = "cn.wps.moffice_eng" // 普通版与英文版一样
        // String wpsActivity = "cn.wps.moffice.documentmanager.PreStartActivity";
        val wpsActivity2 = "cn.wps.moffice.documentmanager.PreStartActivity2"
        // 打开文件
        return openFileByApp(file, wpsPackage, wpsActivity2)
    }
    // ============
    // = 系统页面 =
    // ============
    /**
     * 跳转到系统设置页面
     * @return `true` success, `false` fail
     */
    fun startSysSetting(): Boolean {
        try {
            return startActivity(getIntent(Intent(Settings.ACTION_SETTINGS), true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startSysSetting")
        }
        return false
    }

    /**
     * 跳转到系统设置页面
     * @param activity    [Activity]
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun startSysSetting(activity: Activity, requestCode: Int): Boolean {
        try {
            activity.startActivityForResult(Intent(Settings.ACTION_SETTINGS), requestCode)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startSysSetting")
        }
        return false
    }

    /**
     * 打开网络设置界面
     * @return `true` success, `false` fail
     */
    fun openWirelessSettings(): Boolean {
        try {
            return startActivity(getIntent(Intent(Settings.ACTION_WIRELESS_SETTINGS), true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openWirelessSettings")
        }
        return false
    }

    /**
     * 打开网络设置界面
     * @param activity    [Activity]
     * @param requestCode 请求 code
     * @return `true` success, `false` fail
     */
    fun openWirelessSettings(activity: Activity, requestCode: Int): Boolean {
        try {
            activity.startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), requestCode)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openWirelessSettings")
        }
        return false
    }

    /**
     * 打开 GPS 设置界面
     * @return `true` success, `false` fail
     */
    fun openGpsSettings(): Boolean {
        try {
            return startActivity(getIntent(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), true))
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "openGpsSettings")
        }
        return false
    }
}