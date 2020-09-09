package com.example.commlib.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import com.example.commlib.api.App.Companion.instance

/**
 * @ClassName: (今日头条适配方案) 根据设计图 宽高的dp去设置（现在主流的UI设计图都使用蓝湖-设计图px会标注换算成dp）
 * @Description: 用来做屏幕适配，在application或者是baseactivity里面设置，需要在setcontentview前设置
 * 动态的设置屏幕的density。
 * @CreateDate: 2019/8/30 14:48
 * @Version: 1.0
 */
object DensityUtil {
    private var WIDTH = 600f //参考设备的宽，单位是dp
    private var appDensity //表示屏幕密度
            = 0f
    private var appScaleDensity //字体缩放比列，默认AppDensity
            = 0f

    fun setDensity(application: Application, activity: Activity, width: Float) {
        WIDTH = width
        val displayMetrics = application.resources.displayMetrics
        if (appDensity == 0f) {
            //初始化赋值操作
            appDensity = displayMetrics.density
            appScaleDensity = displayMetrics.scaledDensity

            //添加字体变化监听回调
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    //字体发生更改，重新对ScaleDensity进行赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {}
            })
        }

        //计算目标值density,scaleDensity,densityDpi
        val targetDensity = displayMetrics.widthPixels / WIDTH //1080/360=3.0
        val targetScaleDensity = targetDensity * (appScaleDensity / appDensity)
        val targetDensityDpi = (targetDensity * 160).toInt()

        //替换Activity的density，scaleDensity,densityDpi
        val dm = activity.resources.displayMetrics
        dm.density = targetDensity
        dm.scaledDensity = targetScaleDensity
        dm.densityDpi = targetDensityDpi
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(dpValue: Float): Int {
        val scale = instance.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(pxValue: Float): Int {
        val scale = instance.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale = instance.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    fun sp2px(spValue: Float): Int {
        val fontScale = instance.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    @JvmStatic
    val screenWidth: Int
        get() = instance.resources.displayMetrics.widthPixels
    val screenHeight: Int
        get() = instance.resources.displayMetrics.heightPixels
}