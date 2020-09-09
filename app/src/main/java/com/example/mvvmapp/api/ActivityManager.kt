package com.example.mvvmapp.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.commlib.listener.Listener
import com.example.mvvmapp.main.MainNewActivity

import java.util.*
import kotlin.system.exitProcess

class ActivityManager private constructor() {

    companion object {
        private var manager: ActivityManager? = null
        val instance: ActivityManager?
            get() {
                if (manager == null) {
                    synchronized(ActivityManager::class.java) {
                        if (manager == null) {
                            manager = ActivityManager()
                        }
                    }
                }
                return manager
            }

    }
    private val activities: Stack<Activity>?
    init {
        activities = Stack()
    }

    fun addActivity(activity: Activity) {
        activities?.add(activity)
    }

    fun removeActivity(act: Activity) {
        activities?.remove(act)
    }

    /**
     *
     * @description 结束给定的Activity实例
     */
    fun removeFinishActivity(activity: Activity) {
        activities?.remove(activity)
        activity.finish()
    }

    /**
     *
     * @description 结束当前的Activity实例
     * @date: 2019/10/25 10:12
     * @return activity的实例
     */
    fun finishCurrentActivity() {
        val activity = activities?.lastElement()
        activity?.let { removeFinishActivity(it) }
    }

    /**
     *
     * @description 获取当前的Activity实例
     * @date: 2019/10/25 10:12
     * @return activity的实例
     */
    val currentActivity: Activity?
        get() = if (!activities!!.empty()) {
            activities.lastElement()
        } else null

    /**
     *
     * @description 结束指定类名的Activity
     * @date: 2019/10/25 10:12
     * @param cls
     */
    fun finishTargetActivity(cls: Class<*>) {
        for (activity in activities!!) {
            if (activity.javaClass.name == cls.name) {
                removeFinishActivity(activity)
            }
        }
    }

    fun finishAllActivity() {
        for (activity in activities!!) {
            activity?.finish()
        }
        activities.clear()
    }

    fun exitApp(context: Context?) {
        try {
            finishAllActivity()
            System.exit(0)
        } catch (e: Exception) {
            Log.e("error", "退出程序失败")
        }
    }

    /**
     * 结束指定的activity
     * @param activityName
     */
    fun finishActivity(listener: Listener, vararg activityName: Class<*>) {
        if (activityName == null || activities == null || activities.isEmpty()) {
            listener.onResult()
            return
        }
        var j = 0
        while (j < activities.size) {
            val activity = activities[j]
            for (element in activityName) {
                if (element.simpleName == activity.javaClass.simpleName) {
                    if (!activity.isFinishing) {
                        Log.d("-", activity.javaClass.simpleName + " finish!!!")
                        activity.finish()
                        activities.remove(activity)
                        j--
                    }
                }
            }
            if (j == activities.size - 1) {
                listener.onResult()
                return
            }
            j++
        }
    }

    /**
     * 结束指定的activity
     * @param activityName
     */
    fun finishActivity(vararg activityName: Class<*>) {
        if (activityName == null || activities == null || activities.isEmpty()) {
            return
        }
        var j = 0
        while (j < activities.size) {
            val activity = activities[j]
            for (element in activityName) {
                if (element.simpleName == activity.javaClass.simpleName) {
                    if (!activity.isFinishing) {
                        Log.d("-", activity.javaClass.simpleName + " finish!!!")
                        activity.finish()
                        activities.remove(activity)
                        j--
                    }
                }
            }
            j++
        }
    }



    /**
     * 重启app
     * @param context
     */
    fun restartApp(context: Context) {
        val intent = Intent(context, MainNewActivity::class.java)
        val restartIntent = Intent.makeRestartActivityTask(intent.component)
        context.startActivity(restartIntent)
        exitProcess(0)
    }
}