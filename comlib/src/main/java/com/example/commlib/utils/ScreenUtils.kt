package com.example.commlib.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Surface
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.blankj.ALog
import com.example.commlib.api.App.Companion.instance
import com.example.commlib.utils.ResourceUtils.configuration
import com.example.commlib.utils.ResourceUtils.contentResolver
import com.example.commlib.utils.ResourceUtils.getIdentifier
import com.example.commlib.utils.ResourceUtils.resources
import java.text.DecimalFormat

/**
 * detail: 屏幕相关工具类
 * @author Ttt
 * <pre>
 * 计算屏幕尺寸
 * @see [](https://blog.csdn.net/lincyang/article/details/42679589)
 *
 *
 * 所需权限
 * <uses-permission android:name="android.permission.WRITE_SETTINGS"></uses-permission>
</pre> *
 */
object ScreenUtils {
    // 日志 TAG
    private val TAG = ScreenUtils::class.java.simpleName

    /**
     * 获取 DisplayMetrics
     * @return [DisplayMetrics]
     */
    val displayMetrics: DisplayMetrics?
        get() {
            try {
                val windowManager = AppUtils.windowManager
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                return displayMetrics
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getDisplayMetrics")
            }
            return null
        }
    // ============
    // = 宽高获取 =
    // ============
    /**
     * 获取屏幕宽度
     * @return 屏幕宽度
     */
    val screenWidth: Int
        get() = screenWidthHeight[0]

    /**
     * 获取屏幕高度
     * @return 屏幕高度
     */
    val screenHeight: Int
        get() = screenWidthHeight[1]
    // =//            DisplayMetrics displayMetrics = ResourceUtils.getDisplayMetrics();
//            return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    /**
     * 获取屏幕宽高
     * @return int[], 0 = 宽度, 1 = 高度
     */
    val screenWidthHeight: IntArray
        get() {
            try {
//            DisplayMetrics displayMetrics = ResourceUtils.getDisplayMetrics();
//            return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
                val windowManager = AppUtils.windowManager
                val point = Point()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    windowManager.defaultDisplay.getRealSize(point)
                } else {
                    windowManager.defaultDisplay.getSize(point)
                }
                return intArrayOf(point.x, point.y)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getScreenWidthHeight")
            }
            return intArrayOf(0, 0)
        }//            DisplayMetrics displayMetrics = ResourceUtils.getDisplayMetrics();
//            return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
    /**
     * 获取屏幕宽高
     * @return [Point], point.x 宽, point.y 高
     */
    val screenWidthHeightToPoint: Point?
        get() {
            try {
//            DisplayMetrics displayMetrics = ResourceUtils.getDisplayMetrics();
//            return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
                val windowManager = AppUtils.windowManager
                val point = Point()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    windowManager.defaultDisplay.getRealSize(point)
                } else {
                    windowManager.defaultDisplay.getSize(point)
                }
                return point
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getScreenWidthHeightToPoint")
            }
            return null
        }
    // =
    /**
     * 获取屏幕分辨率
     * @return 屏幕分辨率
     */
    val screenSize: String
        get() = getScreenSize("x")

    /**
     * 获取屏幕分辨率
     * @param symbol 拼接符号
     * @return 屏幕分辨率
     */
    fun getScreenSize(symbol: String): String {
        // 获取分辨率
        val widthHeight = screenWidthHeight
        // 返回分辨率信息
        return widthHeight[1].toString() + symbol + widthHeight[0]
    }// 计算尺寸
    // 转换大小
    /**
     * 获取屏幕英寸 - 例 5.5 英寸
     * @return 屏幕英寸
     */
    @get:RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    val screenSizeOfDevice: String
        get() {
            try {
                val point = Point()
                val displayMetrics = DisplayMetrics()
                val windowManager = AppUtils.windowManager
                windowManager.defaultDisplay.getRealSize(point)
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                // 计算尺寸
                val x = Math.pow(point.x / displayMetrics.xdpi.toDouble(), 2.0)
                val y = Math.pow(point.y / displayMetrics.ydpi.toDouble(), 2.0)
                val screenInches = Math.sqrt(x + y)
                // 转换大小
                return DecimalFormat("#.0").format(screenInches)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getScreenSizeOfDevice")
            }
            return "unknown"
        }
    // =// 屏幕密度, 如 (0.75 / 1.0 / 1.5 / 2.0)
    /**
     * 获取屏幕密度
     * @return 屏幕密度
     */
    val density: Float
        get() {
            val displayMetrics = displayMetrics
            // 屏幕密度, 如 (0.75 / 1.0 / 1.5 / 2.0)
            return displayMetrics?.density ?: 0f
        }// 屏幕密度 DPI, 如 (120 / 160 / 240 / 320)

    /**
     * 获取屏幕密度 dpi
     * @return 屏幕密度 dpi
     */
    val densityDpi: Int
        get() {
            val displayMetrics = displayMetrics
            // 屏幕密度 DPI, 如 (120 / 160 / 240 / 320)
            return displayMetrics?.densityDpi ?: 0
        }

    /**
     * 获取屏幕缩放密度
     * @return 屏幕缩放密度
     */
    val scaledDensity: Float
        get() {
            val displayMetrics = displayMetrics
            return displayMetrics?.scaledDensity ?: 0f
        }

    /**
     * 获取 X 轴 dpi
     * @return X 轴 dpi
     */
    val xDpi: Float
        get() {
            val displayMetrics = displayMetrics
            return displayMetrics?.xdpi ?: 0f
        }

    /**
     * 获取 Y 轴 dpi
     * @return Y 轴 dpi
     */
    val yDpi: Float
        get() {
            val displayMetrics = displayMetrics
            return displayMetrics?.ydpi ?: 0f
        }

    /**
     * 获取宽度比例 dpi 基准
     * @return 宽度比例 dpi 基准
     */
    val widthDpi: Float
        get() {
            val displayMetrics = displayMetrics
            return if (displayMetrics != null) displayMetrics.widthPixels / displayMetrics.density else 0f
        }

    /**
     * 获取高度比例 dpi 基准
     * @return 高度比例 dpi 基准
     */
    val heightDpi: Float
        get() {
            val displayMetrics = displayMetrics
            return if (displayMetrics != null) displayMetrics.heightPixels / displayMetrics.density else 0f
        }// =

    /**
     * 获取屏幕信息
     * @return 屏幕信息
     */
    val screenInfo: String
        get() {
            val builder = StringBuilder()
            val displayMetrics = displayMetrics
            if (displayMetrics != null) {
                try {
                    val heightPixels = displayMetrics.heightPixels
                    val widthPixels = displayMetrics.widthPixels
                    val xdpi = displayMetrics.xdpi
                    val ydpi = displayMetrics.ydpi
                    val densityDpi = displayMetrics.densityDpi
                    val density = displayMetrics.density
                    val scaledDensity = displayMetrics.scaledDensity
                    val heightDpi = heightPixels / density
                    val widthDpi = widthPixels / density
                    // =
                    builder.append("heightPixels: " + heightPixels + "px")
                    builder.append(
                        """

    widthPixels: ${widthPixels}px
    """.trimIndent()
                    )
                    builder.append(
                        """

    xdpi: ${xdpi}dpi
    """.trimIndent()
                    )
                    builder.append(
                        """

    ydpi: ${ydpi}dpi
    """.trimIndent()
                    )
                    builder.append(
                        """

    densityDpi: ${densityDpi}dpi
    """.trimIndent()
                    )
                    builder.append("\ndensity: $density")
                    builder.append("\nscaledDensity: $scaledDensity")
                    builder.append(
                        """

    heightDpi: ${heightDpi}dpi
    """.trimIndent()
                    )
                    builder.append(
                        """

    widthDpi: ${widthDpi}dpi
    """.trimIndent()
                    )
                    return builder.toString()
                } catch (e: Exception) {
                    ALog.eTag(TAG, e, "getScreenInfo")
                }
            }
            return builder.toString()
        }
    // =
    /**
     * 设置禁止截屏
     * @param activity [Activity]
     * @return `true` success, `false` fail
     */
    fun setWindowSecure(activity: Activity): Boolean {
        try {
            // 禁止截屏
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setWindowSecure")
        }
        return false
    }

    /**
     * 设置屏幕为全屏
     * @param activity [Activity]
     * @return `true` success, `false` fail
     */
    fun setFullScreen(activity: Activity): Boolean {
        try {
            // 隐藏标题
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
            // 设置全屏
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setFullScreen")
        }
        return false
    }

    /**
     * 设置屏幕为横屏
     * <pre>
     * 还有一种就是在 Activity 中加属性 android:screenOrientation="landscape"
     * 不设置 Activity 的 android:configChanges 时
     * 切屏会重新调用各个生命周期, 切横屏时会执行一次, 切竖屏时会执行两次
     * 设置 Activity 的 android:configChanges="orientation" 时
     * 切屏还是会重新调用各个生命周期, 切横、竖屏时只会执行一次
     * 设置 Activity 的 android:configChanges="orientation|keyboardHidden|screenSize"
     * 4.0 以上必须带最后一个参数时
     * 切屏不会重新调用各个生命周期, 只会执行 onConfigurationChanged 方法
    </pre> *
     * @param activity [Activity]
     * @return `true` success, `false` fail
     */
    fun setLandscape(activity: Activity): Boolean {
        try {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setLandscape")
        }
        return false
    }

    /**
     * 设置屏幕为竖屏
     * @param activity [Activity]
     * @return `true` success, `false` fail
     */
    fun setPortrait(activity: Activity): Boolean {
        try {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setPortrait")
        }
        return false
    }

    /**
     * 判断是否横屏
     * @return `true` yes, `false` no
     */
    val isLandscape: Boolean
        get() = isLandscape(instance)

    /**
     * 判断是否横屏
     * @param context [Context]
     * @return `true` yes, `false` no
     */
    fun isLandscape(context: Context): Boolean {
        try {
            return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isLandscape")
        }
        return false
    }

    /**
     * 判断是否竖屏
     * @return `true` yes, `false` no
     */
    val isPortrait: Boolean
        get() = isPortrait(instance)

    /**
     * 判断是否竖屏
     * @param context [Context]
     * @return `true` yes, `false` no
     */
    fun isPortrait(context: Context): Boolean {
        try {
            return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "isPortrait")
        }
        return false
    }

    /**
     * 切换屏幕方向
     * @param activity [Activity]
     * @return `true` 横屏, `false` 竖屏
     */
    fun toggleScreenOrientation(activity: Activity): Boolean {
        try {
            // 判断是否竖屏
            return if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                true // 切换横屏, 并且表示属于横屏
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                false // 切换竖屏, 并且表示属于竖屏
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "toggleScreenOrientation")
        }
        return false
    }
    // =
    /**
     * 获取屏幕旋转角度
     * @param activity [Activity]
     * @return 屏幕旋转角度
     */
    fun getScreenRotation(activity: Activity): Int {
        try {
            when (activity.windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> return 0
                Surface.ROTATION_90 -> return 90
                Surface.ROTATION_180 -> return 180
                Surface.ROTATION_270 -> return 270
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getScreenRotation")
        }
        return 0
    }

    /**
     * 判断是否锁屏
     * @return `true` yes, `false` no
     */
    val isScreenLock: Boolean
        get() {
            try {
                val keyguardManager = AppUtils.keyguardManager
                return keyguardManager != null && keyguardManager.inKeyguardRestrictedInputMode()
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "isScreenLock")
            }
            return false
        }

    /**
     * 判断是否是平板
     * @return `true` yes, `false` no
     */
    val isTablet: Boolean
        get() {
            try {
                return configuration!!.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "isTablet")
            }
            return false
        }
    // =
    /**
     * 获取状态栏的高度 ( 无关 android:theme 获取状态栏高度 )
     * @return 状态栏的高度
     */
    val statusHeight: Int
        get() {
            try {
                val id = getIdentifier("status_bar_height", "dimen", "android")
                return resources!!.getDimensionPixelSize(id)
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getStatusHeight")
            }
            return 0
        }

    /**
     * 获取应用区域 TitleBar 高度 ( 顶部灰色 TitleBar 高度, 没有设置 android:theme 的 NoTitleBar 时会显示 )
     * @param activity [Activity]
     * @return 应用区域 TitleBar 高度
     */
    fun getStatusBarHeight(activity: Activity): Int {
        try {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)
            return rect.top
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getStatusBarHeight")
        }
        return 0
    }

    /**
     * 设置进入休眠时长
     * @param duration 时长
     * @return `true` success, `false` fail
     */
    @RequiresPermission(Manifest.permission.WRITE_SETTINGS)
    fun setSleepDuration(duration: Int): Boolean {
        try {
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, duration)
            return true
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "setSleepDuration")
        }
        return false
    }

    /**
     * 获取进入休眠时长
     * @return 进入休眠时长
     */
    val sleepDuration: Int
        get() = try {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "getSleepDuration")
            -1
        }
    // =// 获取对应方向字符串
    // 获取对应的 id
    /**
     * 获取底部导航栏高度
     * @return 底部导航栏高度
     */
    val navigationBarHeight: Int
        get() {
            try {
                val resources = resources
                // 获取对应方向字符串
                val orientation =
                    if (resources!!.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape"
                // 获取对应的 id
                val resourceId = resources.getIdentifier(orientation, "dimen", "android")
                if (resourceId > 0 && checkDeviceHasNavigationBar()) {
                    return resources.getDimensionPixelSize(resourceId)
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "getNavigationBarHeight")
            }
            return 0
        }

    /**
     * 检测是否具有底部导航栏
     * <pre>
     * 一加手机上判断不准确
    </pre> *
     * @return `true` yes, `false` no
     */
    fun checkDeviceHasNavigationBar(): Boolean {
        var hasNavigationBar = false
        try {
            val resources = resources
            val id = resources!!.getIdentifier("config_showNavigationBar", "bool", "android")
            if (id > 0) {
                hasNavigationBar = resources.getBoolean(id)
            }
            try {
                val systemPropertiesClass = Class.forName("android.os.SystemProperties")
                val method = systemPropertiesClass.getMethod("get", String::class.java)
                val navBarOverride =
                    method.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
                if ("1" == navBarOverride) {
                    hasNavigationBar = false
                } else if ("0" == navBarOverride) {
                    hasNavigationBar = true
                }
            } catch (e: Exception) {
                ALog.eTag(TAG, e, "checkDeviceHasNavigationBar - SystemProperties")
            }
        } catch (e: Exception) {
            ALog.eTag(TAG, e, "checkDeviceHasNavigationBar")
        }
        return hasNavigationBar
    }
}