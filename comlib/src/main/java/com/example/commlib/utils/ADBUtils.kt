package com.example.commlib.utils

import android.content.Intent
import android.os.Build
import android.text.TextUtils
import androidx.annotation.IntRange
import com.blankj.ALog
import com.example.commlib.utils.AppUtils.packageName
import com.example.commlib.utils.AppUtils.powerManager
import com.example.commlib.utils.AppUtils.sendBroadcast
import com.example.commlib.utils.AppUtils.startActivity
import com.example.commlib.utils.FileUtils.getFileByPath
import com.example.commlib.utils.FileUtils.isFileExists
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * detail: ADB shell 工具类
 * @author Ttt
 * <pre>
 * Awesome ADB 一份超全超详细的 ADB 用法大全
 * @see [](https://github.com/mzlogin/awesome-adb)
 *
 *
 * 获取 APP 列表
 * @see [](https://blog.csdn.net/henni_719/article/details/62222439)
 *
 *
 * adb shell input
 *
 * @see [](https://blog.csdn.net/soslinken/article/details/49587497)
 *
 *
 * grep 是 linux 下的命令, windows 用 findstr
 * 开启 Thread 执行, 非主线程, 否则无响应并无效
</pre> *
 */
object ADBUtils {
    // 日志 TAG
    private val TAG = ADBUtils::class.java.simpleName

    // 正则表达式: 空格
    private const val REGEX_SPACE = "\\s"

    // 换行字符串
    private val NEW_LINE_STR = System.getProperty("line.separator")

    /**
     * 判断设备是否 root
     * @return `true` yes, `false` no
     */
    val isDeviceRooted: Boolean
        get() {
            val su = "su"
            val locations = arrayOf(
                "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"
            )
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
            return false
        }

    /**
     * 请求 Root 权限
     * @return `true` success, `false` fail
     */
    fun requestRoot(): Boolean {
        return ShellUtils.execCmd("exit", true).isSuccess
    }

    /**
     * 判断 APP 是否授权 Root 权限
     * @return `true` yes, `false` no
     */
    val isGrantedRoot: Boolean
        get() = ShellUtils.execCmd("exit", true).isSuccess2
    // ============
    // = 应用管理 =
    // ============
    // ============
    // = 应用列表 =
    // ============
    /**
     * 获取 APP 列表 ( 包名 )
     * @param type options
     * @return 对应选项的应用包名列表
     */
    fun getAppList(type: String?): List<String>? {
        // adb shell pm list packages [options]
        val typeStr = if (CommUtils.isEmpty(type)) "" else " $type"
        // 执行 shell
        val result = ShellUtils.execCmd("pm list packages$typeStr", false)
        if (result.isSuccess3) {
            try {
                val arrays = result.successMsg.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                return Arrays.asList(*arrays)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getAppList type: $typeStr")
            }
        }
        return null
    }

    /**
     * 获取 APP 安装列表 ( 包名 )
     * @return APP 安装列表 ( 包名 )
     */
    val installAppList: List<String>?
        get() = getAppList(null)

    /**
     * 获取用户安装的应用列表 ( 包名 )
     * @return 用户安装的应用列表 ( 包名 )
     */
    val userAppList: List<String>?
        get() = getAppList("-3")

    /**
     * 获取系统应用列表 ( 包名 )
     * @return 系统应用列表 ( 包名 )
     */
    val systemAppList: List<String>?
        get() = getAppList("-s")

    /**
     * 获取启用的应用列表 ( 包名 )
     * @return 启用的应用列表 ( 包名 )
     */
    val enableAppList: List<String>?
        get() = getAppList("-e")

    /**
     * 获取禁用的应用列表 ( 包名 )
     * @return 禁用的应用列表 ( 包名 )
     */
    val disableAppList: List<String>?
        get() = getAppList("-d")

    /**
     * 获取包名包含字符串 xxx 的应用列表
     * @param filter 过滤获取字符串
     * @return 包名包含字符串 xxx 的应用列表
     */
    fun getAppListToFilter(filter: String): List<String>? {
        return if (CommUtils.isEmpty(filter)) null else getAppList("| grep " + filter.trim { it <= ' ' })
    }

    /**
     * 判断是否安装应用
     * @param packageName 应用包名
     * @return `true` yes, `false` no
     */
    fun isInstalledApp(packageName: String): Boolean {
        return if (CommUtils.isEmpty(packageName)) false else ShellUtils.execCmd(
            "pm path $packageName",
            false
        ).isSuccess3
    }

    /**
     * 查看应用安装路径
     * @param packageName 应用包名
     * @return 应用安装路径
     */
    fun getAppInstallPath(packageName: String): String? {
        if (CommUtils.isEmpty(packageName)) return null
        // 执行 shell
        val result = ShellUtils.execCmd("pm path $packageName", false)
        return if (result.isSuccess3) {
            result.successMsg
        } else null
    }

    /**
     * 清除应用数据与缓存 - 相当于在设置里的应用信息界面点击了「清除缓存」和「清除数据」
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    fun clearAppDataCache(packageName: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        // adb shell pm clear <packagename>
        val cmd = "pm clear %s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, packageName), true)
        return result.isSuccess4("success")
    }
    // ============
    // = 应用信息 =
    // ============
    /**
     * 查看应用详细信息
     * <pre>
     * 输出中包含很多信息, 包括 Activity Resolver Table、Registered ContentProviders、
     * 包名、userId、安装后的文件资源代码等路径、版本信息、权限信息和授予状态、签名版本信息等
    </pre> *
     * @param packageName 应用包名
     * @return 应用详细信息
     */
    fun getAppMessage(packageName: String): String? {
        if (CommUtils.isEmpty(packageName)) return null
        // 执行 shell
        val result = ShellUtils.execCmd("dumpsys package $packageName", true)
        return if (result.isSuccess3) {
            result.successMsg
        } else null
    }

    /**
     * 获取 APP versionCode
     * @param packageName 应用包名
     * @return versionCode
     */
    fun getVersionCode(packageName: String): Int {
        if (CommUtils.isEmpty(packageName)) return 0
        try {
            // 执行 shell
            val result = ShellUtils.execCmd("dumpsys package $packageName | grep version", true)
            if (result.isSuccess3) {
                val arrays = result.successMsg.split(REGEX_SPACE.toRegex()).toTypedArray()
                for (str in arrays) {
                    if (!TextUtils.isEmpty(str)) {
                        try {
                            val datas = str.split("=".toRegex()).toTypedArray()
                            if (datas.size == 2) {
                                if (datas[0].toLowerCase() == "versionCode".toLowerCase()) {
                                    return datas[1].toInt()
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getVersionCode")
        }
        return 0
    }

    /**
     * 获取 APP versionName
     * @param packageName 应用包名
     * @return versionName
     */
    fun getVersionName(packageName: String): String? {
        if (CommUtils.isEmpty(packageName)) return null
        try {
            // 执行 shell
            val result = ShellUtils.execCmd("dumpsys package $packageName | grep version", true)
            if (result.isSuccess3) {
                val arrays = result.successMsg.split(REGEX_SPACE.toRegex()).toTypedArray()
                for (str in arrays) {
                    if (!TextUtils.isEmpty(str)) {
                        try {
                            val datas = str.split("=".toRegex()).toTypedArray()
                            if (datas.size == 2) {
                                if (datas[0].toLowerCase() == "versionName".toLowerCase()) {
                                    return datas[1]
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getVersionName")
        }
        return null
    }
    /**
     * 安装应用
     * <pre>
     * -l 将应用安装到保护目录 /mnt/asec
     * -r 允许覆盖安装
     * -t 允许安装 AndroidManifest.xml 里 application 指定 android:testOnly="true" 的应用
     * -s 将应用安装到 sdcard
     * -d 允许降级覆盖安装
     * -g 授予所有运行时权限
    </pre> *
     * @param filePath 文件路径
     * @param params   安装选项
     * @return `true` success, `false` fail
     */
    // ==============
    // = 安装、卸载 =
    // ==============
    /**
     * 安装应用
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun installApp(filePath: String?, params: String? = "-rtsd"): Boolean {
        if (CommUtils.isEmpty(params)) return false
        val isRoot = isDeviceRooted
        // adb install [-lrtsdg] <path_to_apk>
        val cmd = "adb install %s %s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, params, filePath), isRoot)
        // 判断是否成功
        return result.isSuccess4("success")
    }

    /**
     * 静默安装应用
     * @param filePath 文件路径
     * @return `true` success, `false` fail
     */
    fun installAppSilent(filePath: String?): Boolean {
        return installAppSilent(getFileByPath(filePath), null)
    }

    /**
     * 静默安装应用
     * @param filePath 文件路径
     * @param params   安装选项
     * @return `true` success, `false` fail
     */
    fun installAppSilent(filePath: String?, params: String?): Boolean {
        return installAppSilent(getFileByPath(filePath), params, isDeviceRooted)
    }
    /**
     * 静默安装应用
     * @param file     文件
     * @param params   安装选项
     * @param isRooted 是否 root
     * @return `true` success, `false` fail
     */
    /**
     * 静默安装应用
     * @param file   文件
     * @param params 安装选项
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
        isRooted: Boolean = isDeviceRooted
    ): Boolean {
        if (!isFileExists(file)) return false
        val filePath = '"'.toString() + file!!.absolutePath + '"'
        val command =
            "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " + (if (params == null) "" else "$params ") + filePath
        val result = ShellUtils.execCmd(command, isRooted)
        return result.isSuccess4("success")
    }
    /**
     * 卸载应用
     * @param packageName 应用包名
     * @param isKeepData  -k 参数可选, 表示卸载应用但保留数据和缓存目录
     * @return `true` success, `false` fail
     */
    // =
    /**
     * 卸载应用
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun uninstallApp(packageName: String?, isKeepData: Boolean = false): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        val isRoot = isDeviceRooted
        // adb uninstall [-k] <packagename>
        var cmd: String? = "adb uninstall "
        if (isKeepData) {
            cmd += " -k "
        }
        cmd += packageName
        // 执行 shell
        val result = ShellUtils.execCmd(cmd, isRoot)
        // 判断是否成功
        return result.isSuccess4("success")
    }
    /**
     * 静默卸载应用
     * @param packageName 应用包名
     * @param isKeepData  -k 参数可选, 表示卸载应用但保留数据和缓存目录
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
     * @param isKeepData  -k 参数可选, 表示卸载应用但保留数据和缓存目录
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun uninstallAppSilent(
        packageName: String,
        isKeepData: Boolean = false,
        isRooted: Boolean = isDeviceRooted
    ): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        val command =
            "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall " + (if (isKeepData) "-k " else "") + packageName
        val result = ShellUtils.execCmd(command, isRooted)
        return result.isSuccess4("success")
    }
    // ===========
    // = dumpsys =
    // ===========
    /**
     * 获取对应包名应用启动的 Activity
     * <pre>
     * android.intent.category.LAUNCHER (android.intent.action.MAIN)
    </pre> *
     * @param packageName 应用包名
     * @return package.xx.Activity.className
     */
    fun getActivityToLauncher(packageName: String): String? {
        if (CommUtils.isEmpty(packageName)) return null
        val cmd = "dumpsys package %s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, packageName), true)
        if (result.isSuccess3) {
            val mainStr = "android.intent.action.MAIN:"
            val start = result.successMsg.indexOf(mainStr)
            // 防止都为 null
            if (start != -1) {
                try {
                    // 进行裁剪字符串
                    val subData = result.successMsg.substring(start + mainStr.length)
                    // 进行拆分
                    val arrays = subData.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                    for (str in arrays) {
                        if (!TextUtils.isEmpty(str)) {
                            // 存在包名才处理
                            if (str.indexOf(packageName) != -1) {
                                val splitArys = str.split(REGEX_SPACE.toRegex()).toTypedArray()
                                for (strData in splitArys) {
                                    var str=strData
                                    if (!TextUtils.isEmpty(strData)) {
                                        // 属于 packageName/ 前缀的
                                        if (strData.indexOf("$packageName/") != -1) {
                                            // 防止属于 packageName/.xx.Main_Activity
                                            if (strData.indexOf("/.") != -1) {
                                                // packageName/.xx.Main_Activity
                                                // packageName/packageName.xx.Main_Activity
                                                str = strData.replace("/", "/$packageName")
                                            }
                                            return str
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "getActivityToLauncher $packageName")
                }
            }
        }
        return null
    }
    // ===================
    // = 获取当前 Window =
    // ===================// 执行 shell
    /**
     * 获取当前显示的 Window
     * <pre>
     * adb shell dumpsys window -h
    </pre> *
     * @return package.xx.Activity.className
     */
    val windowCurrent: String?
        get() {
            val cmd = "dumpsys window w | grep \\/  |  grep name="
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            if (result.isSuccess3) {
                try {
                    val nameStr = "name="
                    val arrays = result.successMsg.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                    for (str in arrays) {
                        if (!TextUtils.isEmpty(str)) {
                            val start = str.indexOf(nameStr)
                            if (start != -1) {
                                try {
                                    val subData = str.substring(start + nameStr.length)
                                    return if (subData.indexOf(")") != -1) {
                                        subData.substring(0, subData.length - 1)
                                    } else subData
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "getWindowCurrent")
                }
            }
            return null
        }// packageName/.xx.Main_Activity
    // packageName/packageName.xx.Main_Activity
// 获取裁剪数据
    // 防止属于 packageName/.xx.Main_Activity
// 拆分换行, 并循环// 执行 shell
    /**
     * 获取当前显示的 Window
     * @return package/package.xx.Activity.className
     */
    val windowCurrent2: String?
        get() {
            val cmd = "dumpsys window windows | grep Current"
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            if (result.isSuccess3) {
                try {
                    // 拆分换行, 并循环
                    val arrays = result.successMsg.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                    for (str in arrays) {
                        if (!TextUtils.isEmpty(str)) {
                            val splitArys = str.split(REGEX_SPACE.toRegex()).toTypedArray()
                            if (splitArys != null && splitArys.size != 0) {
                                for (splitStr in splitArys) {
                                    if (!TextUtils.isEmpty(splitStr)) {
                                        val start = splitStr.indexOf("/")
                                        val lastIndex = splitStr.lastIndexOf("}")
                                        if (start != -1 && lastIndex != -1) {
                                            // 获取裁剪数据
                                            var strData = splitStr.substring(0, lastIndex)
                                            // 防止属于 packageName/.xx.Main_Activity
                                            if (strData.indexOf("/.") != -1) {
                                                // packageName/.xx.Main_Activity
                                                // packageName/packageName.xx.Main_Activity
                                                strData = strData.replace(
                                                    "/",
                                                    "/" + splitStr.substring(0, start)
                                                )
                                            }
                                            return strData
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "getWindowCurrent2")
                }
            }
            return null
        }

    /**
     * 获取对应包名显示的 Window
     * @param packageName 应用包名
     * @return package/package.xx.Activity.className
     */
    fun getWindowCurrentToPackage(packageName: String): String? {
        if (CommUtils.isEmpty(packageName)) return null
        val cmd = "dumpsys window windows | grep %s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, packageName), true)
        if (result.isSuccess3) {
            try {
                // 拆分换行, 并循环
                val arrays = result.successMsg.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                for (str in arrays) {
                    if (!TextUtils.isEmpty(str)) {
                        val splitArys = str.split(REGEX_SPACE.toRegex()).toTypedArray()
                        if (splitArys != null && splitArys.size != 0) {
                            for (splitStr in splitArys) {
                                if (!TextUtils.isEmpty(splitStr)) {
                                    val start = splitStr.indexOf("/")
                                    val lastIndex = splitStr.lastIndexOf("}")
                                    if (start != -1 && lastIndex != -1 && splitStr.indexOf(
                                            packageName
                                        ) == 0
                                    ) {
                                        // 获取裁剪数据
                                        var strData = splitStr.substring(0, lastIndex)
                                        // 防止属于 packageName/.xx.Main_Activity
                                        if (strData.indexOf("/.") != -1) {
                                            // packageName/.xx.Main_Activity
                                            // packageName/packageName.xx.Main_Activity
                                            strData = strData.replace("/", "/$packageName")
                                        }
                                        return strData
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getWindowCurrentToPackage")
            }
        }
        return null
    }
    // =====================
    // = 获取当前 Activity =
    // =====================// packageName/.xx.Main_Activity
    // packageName/packageName.xx.Main_Activity
// 获取裁剪数据
    // 防止属于 packageName/.xx.Main_Activity
// 拆分换行, 并循环// 执行 shell
    /**
     * 获取当前显示的 Activity
     * @return package.xx.Activity.className
     */
    val activityCurrent: String?
        get() {
            var cmd = "dumpsys activity activities | grep mFocusedActivity"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cmd = "dumpsys activity activities | grep mResumedActivity"
            }
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            if (result.isSuccess3) {
                try {
                    // 拆分换行, 并循环
                    val arrays = result.successMsg.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                    for (str in arrays) {
                        if (!TextUtils.isEmpty(str)) {
                            val splitArys = str.split(REGEX_SPACE.toRegex()).toTypedArray()
                            if (splitArys != null && splitArys.size != 0) {
                                for (splitStr in splitArys) {
                                    if (!TextUtils.isEmpty(splitStr)) {
                                        val start = splitStr.indexOf("/")
                                        if (start != -1) {
                                            // 获取裁剪数据
                                            var strData = splitStr
                                            // 防止属于 packageName/.xx.Main_Activity
                                            if (strData.indexOf("/.") != -1) {
                                                // packageName/.xx.Main_Activity
                                                // packageName/packageName.xx.Main_Activity
                                                strData = strData.replace(
                                                    "/",
                                                    "/" + splitStr.substring(0, start)
                                                )
                                            }
                                            return strData
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "getActivityCurrent")
                }
            }
            return null
        }

    /**
     * 获取 Activity 栈
     * @return 当前全部 Activity 栈信息
     */
    val activitys: String?
        get() = getActivitys(null)

    /**
     * 获取 Activity 栈
     * @param append 追加筛选条件
     * @return 当前全部 Activity 栈信息
     */
    fun getActivitys(append: String?): String? {
        var cmd = "dumpsys activity activities"
        if (!CommUtils.isEmpty(append)) {
            cmd += " " + append!!.trim { it <= ' ' }
        }
        // 执行 shell
        val result = ShellUtils.execCmd(cmd, true)
        return if (result.isSuccess3) {
            result.successMsg
        } else null
    }

    /**
     * 获取对应包名的 Activity 栈
     * @param packageName 应用包名
     * @return 对应包名的 Activity 栈信息
     */
    fun getActivitysToPackage(packageName: String): String? {
        return if (CommUtils.isEmpty(packageName)) null else getActivitys("| grep $packageName")
    }

    /**
     * 获取对应包名的 Activity 栈 ( 处理成 List) - 最新的 Activity 越靠后
     * @param packageName 应用包名
     * @return 对应包名的 Activity 栈信息集合
     */
    fun getActivitysToPackageLists(packageName: String): List<String>? {
        // 获取对应包名的 Activity 数据结果
        val result = getActivitysToPackage(packageName)
        // 防止数据为 null
        if (!TextUtils.isEmpty(result)) {
            try {
                val lists: MutableList<String> = ArrayList()
                val dataSplit = result!!.split(NEW_LINE_STR!!.toRegex()).toTypedArray()
                // 拆分后, 数据长度
                val splitLength = dataSplit.size
                // 获取 Activity 栈字符串
                var activities: String? = null
                // 判断最后一行是否符合条件
                if (dataSplit[splitLength - 1].indexOf("Activities=") != -1) {
                    activities = dataSplit[splitLength - 1]
                } else {
                    for (str in dataSplit) {
                        if (str.indexOf("Activities=") != -1) {
                            activities = str
                            break
                        }
                    }
                }
                // 进行特殊处理 Activities=[ActivityRecord{xx},ActivityRecord{xx}];
                val startIndex = activities!!.indexOf("Activities=[")
                activities =
                    activities.substring(startIndex + "Activities=[".length, activities.length - 1)
                // 再次进行拆分
                val activityArys = activities.split("ActivityRecord".toRegex()).toTypedArray()
                for (data in activityArys) {
                    try {
                        val splitArys = data.split(REGEX_SPACE.toRegex()).toTypedArray()
                        if (splitArys != null && splitArys.size != 0) {
                            for (splitStr in splitArys) {
                                val start = splitStr.indexOf("$packageName/")
                                if (start != -1) {
                                    // 获取裁剪数据
                                    var strData = splitStr
                                    // 防止属于 packageName/.xx.XxxActivity
                                    if (strData.indexOf("/.") != -1) {
                                        // packageName/.xx.XxxActivity
                                        // packageName/packageName.xx.XxxActivity
                                        strData =
                                            strData.replace("/", "/" + splitStr.substring(0, start))
                                    }
                                    // 保存数据
                                    lists.add(strData)
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
                return lists
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getActivitysToPackageLists")
            }
        }
        return null
    }
    // =
    /**
     * 判断 Activity 栈顶是否重复
     * @param packageName 应用包名
     * @param activity    Activity Name
     * @return `true` yes, `false` no
     */
    fun isActivityTopRepeat(packageName: String, activity: String?): Boolean {
        if (TextUtils.isEmpty(packageName)) {
            return false
        } else if (TextUtils.isEmpty(activity)) {
            return false
        }
        // 判断是否重复
        val isRepeat = false
        // 获取
        val lists = getActivitysToPackageLists(packageName)
        // 数据长度
        var length = 0
        if (lists != null) {
            length = lists.size
        }
        // 防止数据为 null
        if (length >= 2) { // 两个页面以上, 才能够判断是否重复
            try {
                if (lists!![length - 1].endsWith(activity!!)) {
                    // 倒序遍历, 越后面是 Activity 栈顶
                    for (i in length - 2 downTo 0) {
                        val data = lists[i]
                        // 判断是否该页面结尾
                        return data.endsWith(activity)
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "isActivityTopRepeat")
            }
        }
        return isRepeat
    }

    /**
     * 判断 Activity 栈顶是否重复
     * @param packageName 应用包名
     * @param activitys   Activity Name 集合
     * @return `true` yes, `false` no
     */
    fun isActivityTopRepeat(packageName: String, activitys: List<String?>?): Boolean {
        if (TextUtils.isEmpty(packageName)) {
            return false
        } else if (activitys == null || activitys.size == 0) {
            return false
        }
        // 判断是否重复
        val isRepeat = false
        // 获取
        val lists = getActivitysToPackageLists(packageName)
        // 数据长度
        var length = 0
        if (lists != null) {
            length = lists.size
        }
        // 防止数据为 null
        if (length >= 2) { // 两个页面以上, 才能够判断是否重复
            // 循环判断
            for (activity in activitys) {
                try {
                    if (lists!![length - 1].endsWith(activity!!)) {
                        // 倒序遍历, 越后面是 Activity 栈顶
                        for (i in length - 2 downTo 0) {
                            val data = lists[i]
                            // 判断是否该页面结尾
                            return data.endsWith(activity!!)
                        }
                    }
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "isActivityTopRepeat")
                }
            }
        }
        return isRepeat
    }
    // =
    /**
     * 获取 Activity 栈顶重复总数
     * @param packageName 应用包名
     * @param activity    Activity Name
     * @return 指定 Activity 在栈顶重复总数
     */
    fun getActivityTopRepeatCount(packageName: String, activity: String?): Int {
        if (TextUtils.isEmpty(packageName)) {
            return 0
        } else if (TextUtils.isEmpty(activity)) {
            return 0
        }
        // 重复数量
        var number = 0
        // 获取
        val lists = getActivitysToPackageLists(packageName)
        // 数据长度
        var length = 0
        if (lists != null) {
            length = lists.size
        }
        // 防止数据为 null
        if (length >= 2) { // 两个页面以上, 才能够判断是否重复
            try {
                if (lists!![length - 1].endsWith(activity!!)) {
                    // 倒序遍历, 越后面是 Activity 栈顶
                    for (i in length - 2 downTo 0) {
                        val data = lists[i]
                        // 判断是否该页面结尾
                        if (data.endsWith(activity)) {
                            number++
                        } else {
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getActivityTopRepeatCount")
            }
        }
        return number
    }

    /**
     * 获取 Activity 栈顶重复总数
     * @param packageName 应用包名
     * @param activitys   Activity Name 集合
     * @return 指定 Activity 在栈顶重复总数
     */
    fun getActivityTopRepeatCount(packageName: String, activitys: List<String?>?): Int {
        if (TextUtils.isEmpty(packageName)) {
            return 0
        } else if (activitys == null || activitys.size == 0) {
            return 0
        }
        // 获取
        val lists = getActivitysToPackageLists(packageName)
        // 数据长度
        var length = 0
        if (lists != null) {
            length = lists.size
        }
        // 防止数据为 null
        if (length >= 2) { // 两个页面以上, 才能够判断是否重复
            // 循环判断
            for (activity in activitys) {
                try {
                    // 重复数量
                    var number = 0
                    // 判断是否对应页面结尾
                    if (lists!![length - 1].endsWith(activity!!)) {
                        // 倒序遍历, 越后面是 Activity 栈顶
                        for (i in length - 2 downTo 0) {
                            val data = lists[i]
                            // 判断是否该页面结尾
                            if (data.endsWith(activity!!)) {
                                number++
                            } else {
                                break
                            }
                        }
                        // 进行判断处理
                        return number
                    }
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "getActivityTopRepeatCount")
                }
            }
        }
        return 0
    }
    // =======================
    // = 正在运行的 Services =
    // =======================
    /**
     * 查看正在运行的 Services
     * @return 运行中的 Services 信息
     */
    val services: String?
        get() = getServices(null)

    /**
     * 查看正在运行的 Services
     * @param packageName 应用包名, 参数不是必须的, 指定 <packagename> 表示查看与某个包名相关的 Services,
     * 不指定表示查看所有 Services, <packagename> 不一定要给出完整的包名,
     * 比如运行 adb shell dumpsys activity services org.mazhuang
     * 那么包名 org.mazhuang.demo1、org.mazhuang.demo2 和 org.mazhuang123 等相关的 Services 都会列出来
     * @return 运行中的 Services 信息
    </packagename></packagename> */
    fun getServices(packageName: String?): String? {
        val cmd =
            "dumpsys activity services" + if (CommUtils.isEmpty(packageName)) "" else " $packageName"
        val result = ShellUtils.execCmd(cmd, true)
        return if (result.isSuccess3) {
            result.successMsg
        } else null
    }
    /**
     * 启动自身应用
     * @param closeActivity 是否关闭 Activity 所属的 APP 进程后再启动 Activity
     * @return `true` success, `false` fail
     */
    // ======
    // = am =
    // ======
    /**
     * 启动自身应用
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun startSelfApp(closeActivity: Boolean = false): Boolean {
        try {
            // 获取包名
            val packageName = packageName
            // 获取 Launcher Activity
            val activity = ActivityUtils.getLauncherActivity()
            // 跳转应用启动页 ( 启动应用 )
            return startActivity("$packageName/$activity", closeActivity)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startSelfApp")
        }
        return false
    }

    /**
     * 跳转页面 Activity
     * @param packageAndLauncher package/package.xx.Activity.className
     * @param closeActivity      是否关闭 Activity 所属的 APP 进程后再启动 Activity
     * @return `true` success, `false` fail
     */
    fun startActivity(packageAndLauncher: String, closeActivity: Boolean): Boolean {
        return startActivity(packageAndLauncher, null, closeActivity)
    }

    /**
     * 跳转页面 Activity
     * @param packageAndLauncher package/package.xx.Activity.className
     * @param append             追加的信息, 例如传递参数等
     * @param closeActivity      是否关闭 Activity 所属的 APP 进程后再启动 Activity
     * @return `true` success, `false` fail
     */
    fun startActivity(
        packageAndLauncher: String,
        append: String?,
        closeActivity: Boolean
    ): Boolean {
        if (CommUtils.isEmpty(packageAndLauncher)) return false
        try {
            // am start [options] <INTENT>
            var cmd = "am start %s"
            cmd = if (closeActivity) {
                String.format(cmd, "-S $packageAndLauncher")
            } else {
                String.format(cmd, packageAndLauncher)
            }
            // 判断是否追加
            if (!CommUtils.isEmpty(append)) {
                cmd += " " + append!!.trim { it <= ' ' }
            }
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startActivity")
        }
        return false
    }
    /**
     * 启动服务
     * @param packageAndService package/package.xx.Service.className
     * @param append            追加的信息, 例如传递参数等
     * @return `true` success, `false` fail
     */
    /**
     * 启动服务
     * @param packageAndService package/package.xx.Service.className
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun startService(packageAndService: String?, append: String? = null): Boolean {
        if (CommUtils.isEmpty(packageAndService)) return false
        try {
            // am startservice [options] <INTENT>
            var cmd: String = "am startservice $packageAndService"

            // 判断是否追加
            if (!CommUtils.isEmpty(append)) {
                cmd += " " + append!!.trim { it <= ' ' }
            }
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "startService")
        }
        return false
    }
    /**
     * 停止服务
     * @param packageAndService package/package.xx.Service.className
     * @param append            追加的信息, 例如传递参数等
     * @return `true` success, `false` fail
     */
    /**
     * 停止服务
     * @param packageAndService package/package.xx.Service.className
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun stopService(packageAndService: String?, append: String? = null): Boolean {
        if (CommUtils.isEmpty(packageAndService)) return false
        try {
            // am stopservice [options] <INTENT>
            var cmd = "am stopservice $packageAndService"

            // 判断是否追加
            if (!CommUtils.isEmpty(append)) {
                cmd += " " + append!!.trim { it <= ' ' }
            }
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            return result.isSuccess3
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "stopService")
        }
        return false
    }

    /**
     * 发送广播 ( 向所有组件发送 )
     * <pre>
     * 向所有组件广播 BOOT_COMPLETED
     * adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
    </pre> *
     * @param broadcast 广播 INTENT
     * @return `true` success, `false` fail
     */
    fun sendBroadcastToAll(broadcast: String?): Boolean {
        if (CommUtils.isEmpty(broadcast)) return false
        try {
            // am broadcast [options] <INTENT>
            val cmd = "am broadcast -a %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, broadcast), true)
            return result.isSuccess3
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "sendBroadcastAll")
        }
        return false
    }

    /**
     * 发送广播
     * <pre>
     * 只向 org.mazhuang.boottimemeasure/.BootCompletedReceiver 广播 BOOT_COMPLETED
     * adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -n org.mazhuang.boottimemeasure/.BootCompletedReceiver
    </pre> *
     * @param packageAndBroadcast package/package.xx.Receiver.className
     * @param broadcast           广播 INTENT
     * @return `true` success, `false` fail
     */
    fun sendBroadcast(packageAndBroadcast: String?, broadcast: String?): Boolean {
        if (CommUtils.isEmpty(packageAndBroadcast)) return false
        if (CommUtils.isEmpty(broadcast)) return false
        try {
            // am broadcast [options] <INTENT>
            val cmd = "am broadcast -a %s -n %s"
            // 执行 shell
            val result =
                ShellUtils.execCmd(String.format(cmd, broadcast, packageAndBroadcast), true)
            return result.isSuccess3
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "sendBroadcast")
        }
        return false
    }
    // =
    /**
     * 销毁进程
     * @param packageName 应用包名
     * @return `true` success, `false` fail
     */
    fun kill(packageName: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        try {
            val cmd = "am force-stop %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, packageName), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "kill")
        }
        return false
    }

    /**
     * 收紧内存
     * @param pid   进程 ID
     * @param level HIDDEN、RUNNING_MODERATE、BACKGROUND、RUNNING_LOW、MODERATE、RUNNING_CRITICAL、COMPLETE
     * @return `true` success, `false` fail
     */
    fun sendTrimMemory(pid: Int, level: String?): Boolean {
        if (CommUtils.isEmpty(level)) return false
        try {
            val cmd = "am send-trim-memory %s %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, pid, level), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "sendTrimMemory")
        }
        return false
    }
    //    // ============
    //    // = 文件管理 =
    //    // ============
    //
    //    /**
    //     * 复制设备里的文件到电脑
    //     * @param remote 设备里的文件路径
    //     * @param local  电脑上的目录
    //     * @return {@code true} success, {@code false} fail
    //     */
    //    public static boolean pull(final String remote, final String local) {
    //        if (CommUtils.isEmpty(remote)) return false;
    //        try {
    //            // adb pull <设备里的文件路径> [电脑上的目录]
    //            String cmd = "adb pull %s";
    //            // 判断是否存到默认地址
    //            if (!CommUtils.isEmpty(local)) {
    //                cmd += " " + local;
    //            }
    //            // 执行 shell
    //            ShellUtils.CommandResult result = ShellUtils.execCmd(String.format(cmd, remote), true);
    //            return result.isSuccess2();
    //        } catch (Exception e) {
    //            ALog.eTag(TAG, e, "pull");
    //        }
    //        return false;
    //    }
    //
    //    /**
    //     * 复制电脑里的文件到设备
    //     * @param local  电脑上的文件路径
    //     * @param remote 设备里的目录
    //     * @return {@code true} success, {@code false} fail
    //     */
    //    public static boolean push(final String local, final String remote) {
    //        if (CommUtils.isEmpty(local)) return false;
    //        if (CommUtils.isEmpty(remote)) return false;
    //        try {
    //            // adb push <电脑上的文件路径> <设备里的目录>
    //            String cmd = "adb push %s %s";
    //            // 执行 shell
    //            ShellUtils.CommandResult result = ShellUtils.execCmd(String.format(cmd, local, remote), true);
    //            return result.isSuccess2();
    //        } catch (Exception e) {
    //            ALog.eTag(TAG, e, "push");
    //        }
    //        return false;
    //    }
    // =========
    // = Input =
    // =========
    // ===============================
    // = tap - 模拟 touch 屏幕的事件 =
    // ===============================
    /**
     * 点击某个区域
     * @param x X 轴坐标
     * @param y Y 轴坐标
     * @return `true` success, `false` fail
     */
    fun tap(x: Float, y: Float): Boolean {
        try {
            // input [touchscreen|touchpad|touchnavigation] tap <x> <y>
            // input [ 屏幕、触摸板、导航键 ] tap
            val cmd = "input touchscreen tap %s %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, x.toInt(), y.toInt()), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "tap")
        }
        return false
    }
    // ====================
    // = swipe - 滑动事件 =
    // ====================
    /**
     * 按压某个区域 ( 点击 )
     * @param x X 轴坐标
     * @param y Y 轴坐标
     * @return `true` success, `false` fail
     */
    fun swipeClick(x: Float, y: Float): Boolean {
        return swipe(x, y, x, y, 100L)
    }

    /**
     * 按压某个区域 time 大于一定时间变成长按
     * @param x    X 轴坐标
     * @param y    Y 轴坐标
     * @param time 按压时间
     * @return `true` success, `false` fail
     */
    fun swipeClick(x: Float, y: Float, time: Long): Boolean {
        return swipe(x, y, x, y, time)
    }

    /**
     * 滑动到某个区域
     * @param x    X 轴坐标
     * @param y    Y 轴坐标
     * @param toX  滑动到 X 轴坐标
     * @param toY  滑动到 Y 轴坐标
     * @param time 滑动时间 ( 毫秒 )
     * @return `true` success, `false` fail
     */
    fun swipe(x: Float, y: Float, toX: Float, toY: Float, time: Long): Boolean {
        try {
            // input [touchscreen|touchpad|touchnavigation] swipe <x1> <y1> <x2> <y2> [duration(ms)]
            val cmd = "input touchscreen swipe %s %s %s %s %s"
            // 执行 shell
            val result = ShellUtils.execCmd(
                String.format(
                    cmd,
                    x.toInt(),
                    y.toInt(),
                    toX.toInt(),
                    toY.toInt(),
                    time
                ), true
            )
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "swipe")
        }
        return false
    }
    // ===================
    // = text - 模拟输入 =
    // ===================
    /**
     * 输入文本 - 不支持中文
     * @param txt 文本内容
     * @return `true` success, `false` fail
     */
    fun text(txt: String?): Boolean {
        if (CommUtils.isEmpty(txt)) return false
        try {
            // input text <string>
            val cmd = "input text %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, txt), true) // false 可以执行
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "text")
        }
        return false
    }
    // =======================
    // = keyevent - 按键操作 =
    // =======================
    /**
     * 触发某些按键
     * @param keyCode KeyEvent.xxx 如: KeyEvent.KEYCODE_BACK ( 返回键 )
     * @return `true` success, `false` fail
     */
    fun keyevent(keyCode: Int): Boolean {
        try {
            // input keyevent <key code number or name>
            val cmd = "input keyevent %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, keyCode), true) // false 可以执行
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "keyevent")
        }
        return false
    }
    /**
     * 屏幕截图
     * @param path      保存路径
     * @param displayId -d display-id 指定截图的显示屏编号 ( 有多显示屏的情况下 ) 默认 0
     * @return `true` success, `false` fail
     */
    // ============
    // = 实用功能 =
    // ============
    /**
     * 屏幕截图
     * @param path 保存路径
     * @return `true` success, `false` fail
     */
    @JvmOverloads
    fun screencap(path: String?, displayId: Int = 0): Boolean {
        if (CommUtils.isEmpty(path)) return false
        try {
            val cmd = "screencap -p -d %s %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, Math.max(displayId, 0), path), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "screencap")
        }
        return false
    }

    /**
     * 录制屏幕 ( 以 mp4 格式保存 )
     * @param path 保存路径
     * @param time 录制时长, 单位秒 ( 默认 / 最长 180 秒 )
     * @return `true` success, `false` fail
     */
    fun screenrecord(path: String, time: Int): Boolean {
        return screenrecord(path, null, -1, time)
    }

    /**
     * 录制屏幕 ( 以 mp4 格式保存到 )
     * @param path 保存路径
     * @param size 视频的尺寸, 比如 1280x720, 默认是屏幕分辨率
     * @param time 录制时长, 单位秒 ( 默认 / 最长 180 秒 )
     * @return `true` success, `false` fail
     */
    fun screenrecord(path: String, size: String?, time: Int): Boolean {
        return screenrecord(path, size, -1, time)
    }
    /**
     * 录制屏幕 ( 以 mp4 格式保存到 )
     * @param path    保存路径
     * @param size    视频的尺寸, 比如 1280x720, 默认是屏幕分辨率
     * @param bitRate 视频的比特率, 默认是 4Mbps
     * @param time    录制时长, 单位秒 ( 默认 / 最长 180 秒 )
     * @return `true` success, `false` fail
     */
    /**
     * 录制屏幕 ( 以 mp4 格式保存 )
     * @param path 保存路径
     * @return `true` success, `false` fail
     */

    @JvmName("screenrecord1")
    @JvmOverloads
    fun screenrecord(
        path: String,
        size: String? = null,
        bitRate: Int = -1,
        time: Int = -1
    ): Boolean {
        if (CommUtils.isEmpty(path)) return false
        try {
            val builder = StringBuilder()
            builder.append("screenrecord")
            if (!CommUtils.isEmpty(size)) {
                builder.append(" --size $size")
            }
            if (bitRate > 0) {
                builder.append(" --bit-rate $bitRate")
            }
            if (time > 0) {
                builder.append(" --time-limit $time")
            }
            builder.append(" $path")
            // 执行 shell
            val result = ShellUtils.execCmd(builder.toString(), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "screenrecord")
        }
        return false
    }

    /**
     * 查看连接过的 Wifi 密码
     * @return 连接过的 Wifi 密码
     */
    fun wifiConf(): String? {
        try {
            val cmd = "cat /data/misc/wifi/*.conf"
            // 执行 shell
            val result = ShellUtils.execCmd(cmd, true)
            if (result.isSuccess3) {
                return result.successMsg
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "wifiConf")
        }
        return null
    }

    /**
     * 开启 / 关闭 Wifi
     * @param open 是否开启
     * @return `true` success, `false` fail
     */
    fun wifiSwitch(open: Boolean): Boolean {
        val cmd = "svc wifi %s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, if (open) "enable" else "disable"), true)
        return result.isSuccess2
    }

    /**
     * 设置系统时间
     * @param time yyyyMMdd.HHmmss 20160823.131500
     * 表示将系统日期和时间更改为 2016 年 08 月 23 日 13 点 15 分 00 秒
     * @return `true` success, `false` fail
     */
    fun setSystemTime(time: String?): Boolean {
        if (CommUtils.isEmpty(time)) return false
        try {
            val cmd = "date -s %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, time), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setSystemTime")
        }
        return false
    }

    /**
     * 设置系统时间
     * @param time MMddHHmmyyyy.ss 082313152016.00
     * 表示将系统日期和时间更改为 2016 年 08 月 23 日 13 点 15 分 00 秒
     * @return `true` success, `false` fail
     */
    fun setSystemTime2(time: String?): Boolean {
        if (CommUtils.isEmpty(time)) return false
        try {
            val cmd = "date %s"
            // 执行 shell
            val result = ShellUtils.execCmd(String.format(cmd, time), true)
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setSystemTime2")
        }
        return false
    }

    /**
     * 设置系统时间
     * @param time 时间戳转换 MMddHHmmyyyy.ss
     * @return `true` success, `false` fail
     */
    fun setSystemTime2(time: Long): Boolean {
        if (time < 0) return false
        try {
            val cmd = "date %s"
            // 执行 shell
            val result = ShellUtils.execCmd(
                String.format(
                    cmd,
                    SimpleDateFormat("MMddHHmmyyyy.ss").format(time)
                ), true
            )
            return result.isSuccess2
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setSystemTime2")
        }
        return false
    }
    // ================
    // = 刷机相关命令 =
    // ================
    /**
     * 关机 ( 需要 root 权限 )
     * @return `true` success, `false` fail
     */
    fun shutdown(): Boolean {
        try {
            ShellUtils.execCmd("reboot -p", true)
            val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            return startActivity(intent)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "shutdown")
        }
        return false
    }

    /**
     * 重启设备 ( 需要 root 权限 )
     * @return `true` success, `false` fail
     */
    fun reboot(): Boolean {
        try {
            ShellUtils.execCmd("reboot", true)
            val intent = Intent(Intent.ACTION_REBOOT)
            intent.putExtra("nowait", 1)
            intent.putExtra("interval", 1)
            intent.putExtra("window", 0)
            return sendBroadcast(intent)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "reboot")
        }
        return false
    }

    /**
     * 重启设备 ( 需要 root 权限 ) - 并进行特殊的引导模式 (recovery、Fastboot)
     * @param reason 传递给内核来请求特殊的引导模式, 如 "recovery"
     * 重启到 Fastboot 模式 bootloader
     * @return `true` success, `false` fail
     */
    fun reboot(reason: String?): Boolean {
        if (CommUtils.isEmpty(reason)) return false
        try {
            powerManager.reboot(reason)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "reboot")
        }
        return false
    }

    /**
     * 重启引导到 recovery ( 需要 root 权限 )
     * @return `true` success, `false` fail
     */
    fun rebootToRecovery(): Boolean {
        val result = ShellUtils.execCmd("reboot recovery", true)
        return result.isSuccess2
    }

    /**
     * 重启引导到 bootloader ( 需要 root 权限 )
     * @return `true` success, `false` fail
     */
    fun rebootToBootloader(): Boolean {
        val result = ShellUtils.execCmd("reboot bootloader", true)
        return result.isSuccess2
    }
    // ============
    // = 滑动方法 =
    // ============
    /**
     * 发送事件滑动
     * @param x      X 轴坐标
     * @param y      Y 轴坐标
     * @param toX    滑动到 X 轴坐标
     * @param toY    滑动到 Y 轴坐标
     * @param number 循环次数
     */
    fun sendEventSlide(x: Float, y: Float, toX: Float, toY: Float, number: Int) {
        val lists: MutableList<String> = ArrayList()
        // = 开头 =
        lists.add("sendevent /dev/input/event1 3 57 109")
        lists.add("sendevent /dev/input/event1 3 53 $x")
        lists.add("sendevent /dev/input/event1 3 54 $y")
        // 发送 touch 事件 ( 必须使用 0 0 0 配对 )
        lists.add("sendevent /dev/input/event1 1 330 1")
        lists.add("sendevent /dev/input/event1 0 0 0")

        // 判断方向 ( 手势是否从左到右 ) - View 往左滑, 手势操作往右滑
        val isLeftToRight = toX > x
        // 判断方向 ( 手势是否从上到下 ) - View 往上滑, 手势操作往下滑
        val isTopToBottom = toY > y

        // 计算差数
        var diffX = if (isLeftToRight) toX - x else x - toX
        var diffY = if (isTopToBottom) toY - y else y - toY
        if (!isLeftToRight) {
            diffX = -diffX
        }
        if (!isTopToBottom) {
            diffY = -diffY
        }

        // 平均值
        val averageX = diffX / number
        val averageY = diffY / number
        // 上次位置
        var oldX = x.toInt()
        var oldY = y.toInt()

        // 循环处理
        for (i in 0..number) {
            if (averageX != 0f) {
                // 进行判断处理
                val calcX = (x + averageX * i).toInt()
                if (oldX != calcX) {
                    oldX = calcX
                    lists.add("sendevent /dev/input/event1 3 53 $calcX")
                }
            }
            if (averageY != 0f) {
                // 进行判断处理
                val calcY = (y + averageY * i).toInt()
                if (oldY != calcY) {
                    oldY = calcY
                    lists.add("sendevent /dev/input/event1 3 54 $calcY")
                }
            }
            // 每次操作结束发送
            lists.add("sendevent /dev/input/event1 0 0 0")
        }
        // = 结尾 =
        lists.add("sendevent /dev/input/event1 3 57 4294967295")
        // 释放 touch 事件 ( 必须使用 0 0 0 配对 )
        lists.add("sendevent /dev/input/event1 1 330 0")
        lists.add("sendevent /dev/input/event1 0 0 0")

        // 执行 shell
        ShellUtils.execCmd(lists, true)
    }
    // ================
    // = 查看设备信息 =
    // ================
    /**
     * 获取 SDK 版本
     * @return SDK 版本
     */
    val sDKVersion: String?
        get() {
            val result = ShellUtils.execCmd("getprop ro.build.version.sdk", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取 Android 系统版本
     * @return Android 系统版本
     */
    val androidVersion: String?
        get() {
            val result = ShellUtils.execCmd("getprop ro.build.version.release", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }// android.os.Build 内部有信息 android.os.Build.MODEL

    /**
     * 获取设备型号 - 如 RedmiNote4X
     * @return 设备型号
     */
    val model: String?
        get() {
            // android.os.Build 内部有信息 android.os.Build.MODEL
            val result = ShellUtils.execCmd("getprop ro.product.model", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取设备品牌
     * @return 设备品牌
     */
    val brand: String?
        get() {
            val result = ShellUtils.execCmd("getprop ro.product.brand", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取设备名
     * @return 设备名
     */
    val deviceName: String?
        get() {
            val result = ShellUtils.execCmd("getprop ro.product.name", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取 CPU 支持的 abi 列表
     * @return CPU 支持的 abi 列表
     */
    val cpuAbiList: String?
        get() {
            val result =
                ShellUtils.execCmd("cat /system/build.prop | grep ro.product.cpu.abi", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取每个应用程序的内存上限
     * @return 每个应用程序的内存上限
     */
    val appHeapsize: String?
        get() {
            val result = ShellUtils.execCmd("getprop dalvik.vm.heapsize", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }// scale 代表最大电量, level 代表当前电量

    /**
     * 获取电池状况
     * @return 电池状况
     */
    val battery: String?
        get() {
            val result = ShellUtils.execCmd("dumpsys battery", true)
            return if (result.isSuccess3) { // scale 代表最大电量, level 代表当前电量
                result.successMsg
            } else null
        }

    /**
     * 获取屏幕密度
     * @return 屏幕密度
     */
    val density: String?
        get() {
            val result = ShellUtils.execCmd("getprop ro.sf.lcd_density", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }// 正常返回 Physical size: 1080 x 1920
    // 如果使用命令修改过, 那输出可能是
    // Physical size: 1080 x 1920
    // Override size: 480 x 1024
    // 表明设备的屏幕分辨率原本是 1080px * 1920px, 当前被修改为 480px * 1024px
    /**
     * 获取屏幕分辨率
     * @return 屏幕分辨率
     */
    val screenSize: String?
        get() {
            val result = ShellUtils.execCmd("wm size", true)
            return if (result.isSuccess3) {
                // 正常返回 Physical size: 1080 x 1920
                // 如果使用命令修改过, 那输出可能是
                // Physical size: 1080 x 1920
                // Override size: 480 x 1024
                // 表明设备的屏幕分辨率原本是 1080px * 1920px, 当前被修改为 480px * 1024px
                result.successMsg
            } else null
        }

    /**
     * 获取显示屏参数
     * @return 显示屏参数
     */
    val displays: String?
        get() {
            val result = ShellUtils.execCmd("dumpsys window displays", true)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取 Android id
     * @return Android id
     */
    val androidId: String?
        get() {
            val result = ShellUtils.execCmd("settings get secure android_id", true)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }// 进行拆分// 返回值中的 Device ID 就是 IMEI// 在 Android 4.4 及以下版本可通过如下命令获取 IMEI// 添加数据
    // 从指定索引开始
    // 再次裁剪
    // 添加数据
    // 从指定索引开始
    // 再次裁剪
    // 最后进行添加
    // 返回对应的数据
    /**
     * 获取 IMEI 码
     * @return IMEI 码
     */
    val iMEI: String?
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val result = ShellUtils.execCmd("service call iphonesubinfo 1", true)
                if (result.isSuccess3) {
                    try {
                        var index = 0
                        val builder = StringBuilder()
                        var subStr = result.successMsg.replace("\\.".toRegex(), "")
                        subStr = subStr.substring(subStr.indexOf("'") + 1, subStr.indexOf("')"))
                        // 添加数据
                        builder.append(subStr.substring(0, subStr.indexOf("'")))
                        // 从指定索引开始
                        index = subStr.indexOf("'", builder.toString().length + 1)
                        // 再次裁剪
                        subStr = subStr.substring(index + 1)
                        // 添加数据
                        builder.append(subStr.substring(0, subStr.indexOf("'")))
                        // 从指定索引开始
                        index = subStr.indexOf("'", builder.toString().length + 1)
                        // 再次裁剪
                        subStr = subStr.substring(index + 1)
                        // 最后进行添加
                        builder.append(subStr.split(REGEX_SPACE.toRegex()).toTypedArray()[0])
                        // 返回对应的数据
                        return builder.toString()
                    } catch (e: Exception) {
                        ALog.eTag(TAG, e, "getIMEI")
                    }
                }
            } else {
                // 在 Android 4.4 及以下版本可通过如下命令获取 IMEI
                val result = ShellUtils.execCmd("dumpsys iphonesubinfo", true)
                if (result.isSuccess3) { // 返回值中的 Device ID 就是 IMEI
                    try {
                        val splitArys = result.successMsg.split(
                            NEW_LINE_STR!!.toRegex()
                        ).toTypedArray()
                        for (str in splitArys) {
                            if (!TextUtils.isEmpty(str)) {
                                if (str.toLowerCase().indexOf("device") != -1) {
                                    // 进行拆分
                                    val arrays = str.split(REGEX_SPACE.toRegex()).toTypedArray()
                                    return arrays[arrays.size - 1]
                                }
                            }
                        }
                    } catch (e: Exception) {
                        ALog.eTag(TAG, e, "getIMEI")
                    }
                }
            }
            return null
        }// 可以看到网络连接名称、启用状态、IP 地址和 Mac 地址等信息// 如果设备连着 Wifi, 可以使用如下命令来查看局域网 IP

    /**
     * 获取 IP 地址
     * @return IP 地址
     */
    val iPAddress: String?
        get() {
            val isRoot = false
            var result = ShellUtils.execCmd("ifconfig | grep Mask", isRoot)
            if (result.isSuccess3) {
                return result.successMsg
            } else { // 如果设备连着 Wifi, 可以使用如下命令来查看局域网 IP
                result = ShellUtils.execCmd("ifconfig wlan0", isRoot)
                if (result.isSuccess3) {
                    return result.successMsg
                } else {
                    // 可以看到网络连接名称、启用状态、IP 地址和 Mac 地址等信息
                    result = ShellUtils.execCmd("netcfg", isRoot)
                    if (result.isSuccess3) {
                        return result.successMsg
                    }
                }
            }
            return null
        }

    /**
     * 获取 Mac 地址
     * @return Mac 地址
     */
    val mac: String?
        get() {
            val result = ShellUtils.execCmd("cat /sys/class/net/wlan0/address", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取 CPU 信息
     * @return CPU 信息
     */
    val cPU: String?
        get() {
            val result = ShellUtils.execCmd("cat /proc/cpuinfo", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 获取内存信息
     * @return 内存信息
     */
    val meminfo: String?
        get() {
            val result = ShellUtils.execCmd("cat /proc/meminfo", false)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }
    // ============
    // = 修改设置 =
    // ============
    /**
     * 设置屏幕大小
     * @param width  屏幕宽度
     * @param height 屏幕高度
     * @return `true` success, `false` fail
     */
    fun setScreenSize(width: Int, height: Int): Boolean {
        val cmd = "wm size %sx%s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, width, height), true)
        return result.isSuccess2
    }

    /**
     * 恢复原分辨率命令
     * @return `true` success, `false` fail
     */
    fun resetScreen(): Boolean {
        // 执行 shell
        val result = ShellUtils.execCmd("wm size reset", true)
        return result.isSuccess2
    }

    /**
     * 设置屏幕密度
     * @param density 屏幕密度
     * @return `true` success, `false` fail
     */
    fun setDensity(density: Int): Boolean {
        // 执行 shell
        val result = ShellUtils.execCmd("wm density $density", true)
        return result.isSuccess2
    }

    /**
     * 恢复原屏幕密度
     * @return `true` success, `false` fail
     */
    fun resetDensity(): Boolean {
        // 执行 shell
        val result = ShellUtils.execCmd("wm density reset", true)
        return result.isSuccess2
    }

    /**
     * 显示区域 ( 设置留白边距 )
     * @param left   left padding
     * @param top    top padding
     * @param right  right padding
     * @param bottom bottom padding
     * @return `true` success, `false` fail
     */
    fun setOverscan(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        val cmd = "wm overscan %s,%s,%s,%s"
        // 执行 shell
        val result = ShellUtils.execCmd(String.format(cmd, left, top, right, bottom), true)
        return result.isSuccess2
    }

    /**
     * 恢复原显示区域
     * @return `true` success, `false` fail
     */
    fun resetOverscan(): Boolean {
        // 执行 shell
        val result = ShellUtils.execCmd("wm overscan reset", true)
        return result.isSuccess2
    }// 执行 shell

    /**
     * 获取亮度是否为自动获取 ( 自动调节亮度 )
     * @return 1 开启、0 未开启、-1 未知
     */
    val screenBrightnessMode: Int
        get() {
            // 执行 shell
            val result = ShellUtils.execCmd("settings get system screen_brightness_mode", true)
            if (result.isSuccess3) {
                try {
                    return result.successMsg.toInt()
                } catch (e: Exception) {
                }
            }
            return -1
        }

    /**
     * 设置亮度是否为自动获取 ( 自动调节亮度 )
     * @param isAuto 是否自动调节
     * @return `true` success, `false` fail
     */
    fun setScreenBrightnessMode(isAuto: Boolean): Boolean {
        // 执行 shell
        val result = ShellUtils.execCmd(
            "settings put system screen_brightness_mode " + if (isAuto) 1 else 0,
            true
        )
        return result.isSuccess3
    }// 执行 shell

    /**
     * 获取屏幕亮度值
     * @return 屏幕亮度值
     */
    val screenBrightness: String?
        get() {
            // 执行 shell
            val result = ShellUtils.execCmd("settings get system screen_brightness", true)
            if (result.isSuccess3) {
                var suc = result.successMsg
                if (suc.startsWith("\"")) {
                    suc = suc.substring(1)
                }
                if (suc.endsWith("\"")) {
                    suc = suc.substring(0, suc.length - 1)
                }
                return suc
            }
            return null
        }

    /**
     * 更改屏幕亮度值 ( 亮度值在 0-255 之间 )
     * @param brightness 亮度值
     * @return `true` success, `false` fail
     */
    fun setScreenBrightness(@IntRange(from = 0, to = 255) brightness: Int): Boolean {
        if (brightness < 0) {
            return false
        } else if (brightness > 255) {
            return false
        }
        // 执行 shell
        val result = ShellUtils.execCmd("settings put system screen_brightness $brightness", true)
        return result.isSuccess2
    }// 执行 shell

    /**
     * 获取自动锁屏休眠时间 ( 单位毫秒 )
     * @return 自动锁屏休眠时间
     */
    val screenOffTimeout: String?
        get() {
            // 执行 shell
            val result = ShellUtils.execCmd("settings get system screen_off_timeout", true)
            return if (result.isSuccess3) {
                result.successMsg
            } else null
        }

    /**
     * 设置自动锁屏休眠时间 ( 单位毫秒 )
     * <pre>
     * 设置永不休眠 Integer.MAX_VALUE
    </pre> *
     * @param time 休眠时间 ( 单位毫秒 )
     * @return `true` success, `false` fail
     */
    fun setScreenOffTimeout(time: Long): Boolean {
        if (time <= 0) {
            return false
        }
        // 执行 shell
        val result = ShellUtils.execCmd("settings put system screen_off_timeout $time", true)
        return result.isSuccess2
    }// 执行 shell

    /**
     * 获取日期时间选项中通过网络获取时间的状态
     * @return 1 允许、0 不允许、-1 未知
     */
    val globalAutoTime: Int
        get() {
            // 执行 shell
            val result = ShellUtils.execCmd("settings get global auto_time", true)
            if (result.isSuccess3) {
                try {
                    return result.successMsg.toInt()
                } catch (e: Exception) {
                }
            }
            return -1
        }

    /**
     * 修改日期时间选项中通过网络获取时间的状态, 设置是否开启
     * @param isOpen 是否设置通过网络获取时间
     * @return `true` success, `false` fail
     */
    fun setGlobalAutoTime(isOpen: Boolean): Boolean {
        // 执行 shell
        val result =
            ShellUtils.execCmd("settings put global auto_time " + if (isOpen) 1 else 0, true)
        return result.isSuccess3
    }

    /**
     * 关闭 USB 调试模式
     * @return `true` success, `false` fail
     */
    fun disableADB(): Boolean {
        // 执行 shell
        val result = ShellUtils.execCmd("settings put global adb_enabled 0", true)
        return result.isSuccess2
    }

    /**
     * 允许访问非 SDK API
     * <pre>
     * 不需要设备获得 Root 权限
    </pre> *
     * @return 执行结果
     */
    fun putHiddenApi(): Int {
        val cmds = arrayOfNulls<String>(2)
        cmds[0] = "settings put global hidden_api_policy_pre_p_apps 1"
        cmds[1] = "settings put global hidden_api_policy_p_apps 1"
        // 执行 shell
        val result = ShellUtils.execCmd(cmds, true)
        return result.result
    }

    /**
     * 禁止访问非 SDK API
     * <pre>
     * 不需要设备获得 Root 权限
    </pre> *
     * @return 执行结果
     */
    fun deleteHiddenApi(): Int {
        val cmds = arrayOfNulls<String>(2)
        cmds[0] = "settings delete global hidden_api_policy_pre_p_apps"
        cmds[1] = "settings delete global hidden_api_policy_p_apps"
        // 执行 shell
        val result = ShellUtils.execCmd(cmds, true)
        return result.result
    }

    /**
     * 开启无障碍辅助功能
     * @param packageName              应用包名
     * @param accessibilityServiceName 无障碍服务名
     * @return `true` success, `false` fail
     */
    fun openAccessibility(packageName: String?, accessibilityServiceName: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        if (CommUtils.isEmpty(accessibilityServiceName)) return false
        val cmd = "settings put secure enabled_accessibility_services %s/%s"
        // 格式化 shell 命令
        val cmds = arrayOfNulls<String>(2)
        cmds[0] = String.format(cmd, packageName, accessibilityServiceName)
        cmds[1] = "settings put secure accessibility_enabled 1"
        // 执行 shell
        val result = ShellUtils.execCmd(cmds, true)
        return result.isSuccess2
    }

    /**
     * 关闭无障碍辅助功能
     * @param packageName              应用包名
     * @param accessibilityServiceName 无障碍服务名
     * @return `true` success, `false` fail
     */
    fun closeAccessibility(packageName: String?, accessibilityServiceName: String?): Boolean {
        if (CommUtils.isEmpty(packageName)) return false
        if (CommUtils.isEmpty(accessibilityServiceName)) return false
        val cmd = "settings put secure enabled_accessibility_services %s/%s"
        // 格式化 shell 命令
        val cmds = arrayOfNulls<String>(2)
        cmds[0] = String.format(cmd, packageName, accessibilityServiceName)
        cmds[1] = "settings put secure accessibility_enabled 0"
        // 执行 shell
        val result = ShellUtils.execCmd(cmds, true)
        return result.isSuccess2
    }
}