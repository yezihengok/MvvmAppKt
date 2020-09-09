package com.example.commlib.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.blankj.ALog
import com.example.commlib.utils.ResourceUtils.contentResolver
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

/**
 * detail: 设备相关工具类
 * @author Ttt
 * <pre>
 * @see [](http://blog.csdn.net/zhangcanyan/article/details/52817866)
 * android.os.Build.BOARD: 获取设备基板名称
 * android.os.Build.BOOTLOADER: 获取设备引导程序版本号
 * android.os.Build.BRAND: 获取设备品牌
 * android.os.Build.CPU_ABI: 获取设备指令集名称
</pre> */
object DeviceUtils {
    // 日志 TAG
    private val TAG = DeviceUtils::class.java.simpleName

    // 换行字符串
    private val NEW_LINE_STR = System.getProperty("line.separator")

    /**
     * 获取设备信息
     * @return [,][<]
     */
    val deviceInfo: Map<String?, String?>
        get() = getDeviceInfo(HashMap())

    /**
     * 获取设备信息
     * @param deviceInfoMap 设备信息 Map
     * @return [,][<]
     */
    fun getDeviceInfo(deviceInfoMap: MutableMap<String?, String?>): Map<String?, String?> {
        // 获取设备信息类的所有申明的字段, 即包括 public、private 和 proteced, 但是不包括父类的申明字段
        val fields = Build::class.java.declaredFields
        // 遍历字段
        for (field in fields) {
            try {
                // 取消 Java 的权限控制检查
                field.isAccessible = true
                // 转换当前设备支持的 ABI - CPU 指令集
                if (field.name.toLowerCase().startsWith("SUPPORTED".toLowerCase())) {
                    try {
                        val `object` = field[null]
                        val keywords = arrayOf("foo", "bar", "spam")
                        // 判断是否数组
                        if (`object` is Array<*>) {
                            deviceInfoMap[field.name] = `object`.contentToString()
                            continue
                        }
                    } catch (e: Exception) {
                    }
                }
                // 获取类型对应字段的数据, 并保存
                deviceInfoMap[field.name] = field[null].toString()
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getDeviceInfo")
            }
        }
        return deviceInfoMap
    }

    /**
     * 处理设备信息
     * @param deviceInfoMap 设备信息 Map
     * @param errorInfo     错误提示信息, 如获取设备信息失败
     * @return 拼接后的设备信息字符串
     */
    fun handlerDeviceInfo(deviceInfoMap: Map<String?, String?>, errorInfo: String): String {
        try {
            // 初始化 Builder, 拼接字符串
            val builder = StringBuilder()
            // 获取设备信息
            val mapIter = deviceInfoMap.entries.iterator()
            // 遍历设备信息
            while (mapIter.hasNext()) {
                // 获取对应的 key - value
                val rnEntry = mapIter.next()
                val rnKey = rnEntry.key // key
                val rnValue = rnEntry.value // value
                // 保存设备信息
                builder.append(rnKey)
                builder.append(" = ")
                builder.append(rnValue)
                builder.append(NEW_LINE_STR)
            }
            return builder.toString()
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "handlerDeviceInfo")
        }
        return errorInfo
    }
    // =
    /**
     * 获取设备基板名称
     * @return 设备基板名称
     */
    val board: String
        get() = Build.BOARD

    /**
     * 获取设备引导程序版本号
     * @return 设备引导程序版本号
     */
    val bootloader: String
        get() = Build.BOOTLOADER

    /**
     * 获取设备品牌
     * @return 设备品牌
     */
    val brand: String
        get() = Build.BRAND

    /**
     * 获取支持的第一个指令集
     * @return 支持的第一个指令集
     */
    val cPU_ABI: String
        get() = Build.CPU_ABI

    /**
     * 获取支持的第二个指令集
     * @return 支持的第二个指令集
     */
    val cPU_ABI2: String
        get() = Build.CPU_ABI2

    /**
     * 获取支持的指令集 如: [arm64-v8a, armeabi-v7a, armeabi]
     * @return 支持的指令集
     */
    val aBIs: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
            } else arrayOf(Build.CPU_ABI)
        }

    /**
     * 获取支持的 32 位指令集
     * @return 支持的 32 位指令集
     */
    val sUPPORTED_32_BIT_ABIS: Array<String>?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_32_BIT_ABIS
        } else null

    /**
     * 获取支持的 64 位指令集
     * @return 支持的 64 位指令集
     */
    val sUPPORTED_64_BIT_ABIS: Array<String>?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_64_BIT_ABIS
        } else null

    /**
     * 获取设备驱动名称
     * @return 设备驱动名称
     */
    val device: String
        get() = Build.DEVICE

    /**
     * 获取设备显示的版本包 ( 在系统设置中显示为版本号 ) 和 ID 一样
     * @return 设备显示的版本包
     */
    val display: String
        get() = Build.DISPLAY

    /**
     * 获取设备的唯一标识, 由设备的多个信息拼接合成
     * @return 设备的唯一标识, 由设备的多个信息拼接合成
     */
    val fingerprint: String
        get() = Build.FINGERPRINT

    /**
     * 获取设备硬件名称, 一般和基板名称一样 (BOARD)
     * @return 设备硬件名称, 一般和基板名称一样 (BOARD)
     */
    val hardware: String
        get() = Build.HARDWARE

    /**
     * 获取设备主机地址
     * @return 设备主机地址
     */
    val host: String
        get() = Build.HOST

    /**
     * 获取设备版本号
     * @return 设备版本号
     */
    val iD: String
        get() = Build.ID

    /**
     * 获取设备型号 如 RedmiNote4X
     * @return 设备型号
     */
    val model: String
        get() {
            var model = Build.MODEL
            model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
            return model
        }

    /**
     * 获取设备厂商 如 Xiaomi
     * @return 设备厂商
     */
    val manufacturer: String
        get() = Build.MANUFACTURER

    /**
     * 获取整个产品的名称
     * @return 整个产品的名称
     */
    val product: String
        get() = Build.PRODUCT

    /**
     * 获取无线电固件版本号, 通常是不可用的 显示 unknown
     * @return 无线电固件版本号
     */
    val radio: String
        get() = Build.RADIO

    /**
     * 获取设备标签, 如 release-keys 或测试的 test-keys
     * @return 设备标签
     */
    val tags: String
        get() = Build.TAGS

    /**
     * 获取设备时间
     * @return 设备时间
     */
    val time: Long
        get() = Build.TIME

    /**
     * 获取设备版本类型 主要为 "user" 或 "eng".
     * @return 设备版本类型
     */
    val type: String
        get() = Build.TYPE

    /**
     * 获取设备用户名 基本上都为 android-build
     * @return 设备用户名
     */
    val user: String
        get() = Build.USER
    // =
    /**
     * 获取 SDK 版本号
     * @return SDK 版本号
     */
    val sDKVersion: Int
        get() = Build.VERSION.SDK_INT

    /**
     * 获取系统版本号, 如 4.1.2 或 2.2 或 2.3 等
     * @return 系统版本号
     */
    val release: String
        get() = Build.VERSION.RELEASE

    /**
     * 获取设备当前的系统开发代号, 一般使用 REL 代替
     * @return 设备当前的系统开发代号
     */
    val codename: String
        get() = Build.VERSION.CODENAME

    /**
     * 获取系统源代码控制值, 一个数字或者 git hash 值
     * @return 系统源代码控制值
     */
    val incremental: String
        get() = Build.VERSION.INCREMENTAL

    /**
     * 获取 Android id
     * <pre>
     * 在设备首次启动时, 系统会随机生成一个 64 位的数字, 并把这个数字以十六进制字符串的形式保存下来,
     * 这个十六进制的字符串就是 ANDROID_ID, 当设备被 wipe 后该值会被重置
    </pre> *
     * @return Android id
     */
    val androidId: String?
        get() {
            var androidId: String? = null
            try {
                androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getAndroidId")
            }
            return androidId
        }

    /**
     * 获取基带版本 BASEBAND-VER
     * @return 基带版本 BASEBAND-VER
     */
    val baseband_Ver: String?
        get() {
            var basebandVersion = ""
            try {
                val clazz = Class.forName("android.os.SystemProperties")
                val invoker = clazz.newInstance()
                val method = clazz.getMethod("get", String::class.java, String::class.java)
                val result = method.invoke(invoker, "gsm.version.baseband", "no message")
                basebandVersion = result as String
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getBaseband_Ver")
            }
            return basebandVersion
        }

    /**
     * 获取内核版本 CORE-VER
     * @return 内核版本 CORE-VER
     */
    val linuxCore_Ver: String
        get() {
            var kernelVersion = ""
            try {
                val process = Runtime.getRuntime().exec("cat /proc/version")
                val `is` = process.inputStream
                val isr = InputStreamReader(`is`)
                val br = BufferedReader(isr, 8 * 1024)
                var line: String
                val builder = StringBuilder()
                while (br.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                val result = builder.toString()
                if (result !== "") {
                    val keyword = "version "
                    var index = result.indexOf(keyword)
                    line = result.substring(index + keyword.length)
                    index = line.indexOf(" ")
                    kernelVersion = line.substring(0, index)
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getLinuxCore_Ver")
            }
            return kernelVersion
        }
    // =
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
     * 获取是否启用 ADB
     * @return `true` yes, `false` no
     */
    @get:RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    val isAdbEnabled: Boolean
        get() {
            try {
                return Settings.Secure.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) > 0
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "isAdbEnabled")
            }
            return false
        }

    // =
    // Default MAC address reported to a client that does not have the android.permission.LOCAL_MAC_ADDRESS permission.
    private const val DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00"

    /**
     * 获取设备 MAC 地址
     * <pre>
     * 没有打开 Wifi, 则获取 WLAN MAC 地址失败
    </pre> *
     * @return 设备 MAC 地址
     */
    @get:RequiresPermission(allOf = [Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE])
    val macAddress: String?
        get() {
            var macAddress = macAddressByWifiInfo
            if (DEFAULT_MAC_ADDRESS != macAddress) {
                return macAddress
            }
            macAddress = macAddressByNetworkInterface
            if (DEFAULT_MAC_ADDRESS != macAddress) {
                return macAddress
            }
            macAddress = macAddressByInetAddress
            if (DEFAULT_MAC_ADDRESS != macAddress) {
                return macAddress
            }
            macAddress = getMacAddressByFile()
            return if (DEFAULT_MAC_ADDRESS != macAddress) {
                macAddress
            } else null
        }

    /**
     * 获取 MAC 地址
     * @return MAC 地址
     */
    @get:RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    private val macAddressByWifiInfo: String
        private get() {
            try {
                @SuppressLint("WifiManagerLeak") val wifiManager = AppUtils.wifiManager
                if (wifiManager != null) {
                    val wifiInfo = wifiManager.connectionInfo
                    if (wifiInfo != null) return wifiInfo.macAddress
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getMacAddressByWifiInfo")
            }
            return DEFAULT_MAC_ADDRESS
        }

    /**
     * 获取 MAC 地址
     * @return MAC 地址
     */
    @get:RequiresPermission(Manifest.permission.INTERNET)
    private val macAddressByNetworkInterface: String
        private get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (ni == null || !ni.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.size > 0) {
                        val builder = StringBuilder()
                        for (b in macBytes) {
                            builder.append(String.format("%02x:", b))
                        }
                        return builder.substring(0, builder.length - 1)
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getMacAddressByNetworkInterface")
            }
            return DEFAULT_MAC_ADDRESS
        }

    /**
     * 通过 InetAddress 获取 Mac 地址
     * @return Mac 地址
     */
    private val macAddressByInetAddress: String
        private get() {
            try {
                val inetAddress = inetAddress
                if (inetAddress != null) {
                    val ni = NetworkInterface.getByInetAddress(inetAddress)
                    if (ni != null) {
                        val macBytes = ni.hardwareAddress
                        if (macBytes != null && macBytes.size > 0) {
                            val builder = StringBuilder()
                            for (b in macBytes) {
                                builder.append(String.format("%02x:", b))
                            }
                            return builder.substring(0, builder.length - 1)
                        }
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getMacAddressByInetAddress")
            }
            return DEFAULT_MAC_ADDRESS
        }// To prevent phone of xiaomi return "10.0.2.15"

    /**
     * 获取 InetAddress
     * @return [InetAddress]
     */
    val inetAddress: InetAddress?
         get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val inetAddress = addresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val hostAddress = inetAddress.hostAddress
                            if (hostAddress.indexOf(':') < 0) return inetAddress
                        }
                    }
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getInetAddress")
            }
            return null
        }

    /**
     * 获取 MAC 地址
     * @return MAC 地址
     */
    private fun getMacAddressByFile(): String {
        var result = ShellUtils.execCmd("getprop wifi.interface", false)
        if (result.isSuccess) {
            val name = result.successMsg
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/$name/address", false)
                if (result.result == 0) {
                    val address = result.successMsg
                    if (address != null && address.length > 0) {
                        return address
                    }
                }
            }
        }
        return DEFAULT_MAC_ADDRESS
    }
    // =
    /**
     * 关机 ( 需要 root 权限 )
     * @return `true` success, `false` fail
     */
    fun shutdown(): Boolean {
        try {
            ShellUtils.execCmd("reboot -p", true)
            val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            return AppUtils.startActivity(intent)
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
            return AppUtils.sendBroadcast(intent)
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
        try {
            AppUtils.powerManager.reboot(reason)
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
}