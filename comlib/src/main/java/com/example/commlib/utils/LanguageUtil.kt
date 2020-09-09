package com.example.commlib.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.ALog
import java.util.*

/**
 * 语言工具类
 */
class LanguageUtil private constructor() {
    private val receiver: BroadcastReceiver = LocaleChangeReceiver()

    /**
     * 中英文切换
     *
     * @param context context必须是activity的context
     */
    fun changeEnglishOrChineseLanguage(context: Context) {
        val appLanguage = getAppLanguage(context)
        ALog.d("======================$appLanguage")
        when (appLanguage) {
            "en" -> {
                //如果是英文的话就切换中文
                changeAppLanguage(context, SIMPLIFIED_CHINESE)
            }
            "zh" -> {
                //如果是中文就切换英文
                changeAppLanguage(context, ENGLISH)
            }
            else -> {
                //如果既不是中文也不是英文就切换中文
                changeAppLanguage(context, SIMPLIFIED_CHINESE)
            }
        }
    }

    /**
     * 修改语言设置
     */
    fun changeAppLanguage(context: Context, type: Int) {
        val resources = context.resources
        val config = resources.configuration
        val dm = resources.displayMetrics
        when (type) {
            FOLLOW_SYSTEM -> config.locale = Locale.getDefault()
            ENGLISH -> config.locale = Locale.US
            SIMPLIFIED_CHINESE -> config.locale = Locale.SIMPLIFIED_CHINESE
        }

        //注册广播 暂时不需要这么做
//        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
        saveLanguageSetting(context, type, config.locale.language)
        resources.updateConfiguration(config, dm)
    }

    /**
     * 判断是否与设定的语言相同.
     *
     * @param context
     * @return
     */
    fun isSameWithSetting(context: Context?): Boolean {
        return true
    }

    /**
     * 设置当前的语言的自定义编号
     */
    private fun saveLanguageSetting(context: Context, type: Int, name: String) {
        val sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt(LANGUAGETYPE, type)
            .apply()
        sharedPreferences.edit()
            .putString(LANGUAGE, name)
            .apply()
    }

    /**
     * 得到当前的语言的自定义编号
     */
    fun getAppLanguageSetting(context: Context): Int {
        return context
            .getSharedPreferences(spName, Context.MODE_PRIVATE)
            .getInt(LANGUAGETYPE, FOLLOW_SYSTEM)
    }

    /**
     * 得到当前的语言名称
     */
    fun getAppLanguage(context: Context): String? {
        val preferences = context.getSharedPreferences(
            spName,
            Context.MODE_PRIVATE
        )
        return preferences.getString(
            LANGUAGE,
            Locale.getDefault().language
        )
    }

    internal inner class LocaleChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //改变系统语言会接收到 ACTION_LOCALE_CHANGED
            //改变应用内部语言不会收到 ACTION_LOCALE_CHANGED
            if (Intent.ACTION_LOCALE_CHANGED != intent.action) {
                val languageType =
                    context.getSharedPreferences(spName, Context.MODE_PRIVATE).getInt(
                        LANGUAGETYPE, FOLLOW_SYSTEM
                    )
                //                languageType=FOLLOW_SYSTEM 表示用户重来没有设置过语言 这时候我们就要根据系统的变化来显示语言
                if (languageType == FOLLOW_SYSTEM) {
                    changeAppLanguage(context, languageType)
                }
            }
        }
    }

    companion object {
        //单利模式获取LanguageUtil
        var instance: LanguageUtil? = null
            get() {
                //单利模式获取LanguageUtil
                if (field == null) {
                    synchronized(LanguageUtil::class.java) { field = LanguageUtil() }
                }
                return field
            }
            private set
        const val spName = "LanguageSetting" //sp文件名
        private const val LANGUAGE = "Language" //语言名key
        private const val LANGUAGETYPE = "LanguageType" //语言type  key
        const val FOLLOW_SYSTEM = 0 //系统默认语言
        const val SIMPLIFIED_CHINESE = 1 //中文
        const val ENGLISH = 2 //英文
    }
}