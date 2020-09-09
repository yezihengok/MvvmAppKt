package com.example.mvvmapp.db

import android.content.Context
import com.example.mvvmapp.BuildConfig
import com.example.mvvmapp.db.upgrade.DBHelper
import com.primary.greendao.gen.DaoMaster
import com.primary.greendao.gen.DaoSession
import org.greenrobot.greendao.query.QueryBuilder

/**
 * Created by yzh on 2020/8/27 11:14.
 */
class DaoManager {
    init {
        setDebug()
    }
    companion object {
        //使用 by lazy 实现 单例
        val instance: DaoManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DaoManager()
        }

        private val TAG = DaoManager::class.java.simpleName
        private const val DB_NAME = "greendaotest.db"

        private var sHelper: DBHelper? = null
        //private var daoSession: DaoSession? = null
        //private var sDaoMaster: DaoMaster? = null
    }
    private var context: Context? = null
    fun init(context: Context?) {
        this.context = context
    }


     private val sDaoMaster: DaoMaster by lazy {
         sHelper = DBHelper(context, DB_NAME)
         DaoMaster(sHelper?.writableDatabase)
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     */
    val daoSession:DaoSession by lazy {
        sDaoMaster.newSession()
    }

    /**
     * 打开输出日志，默认关闭
     */
    private fun setDebug() {
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true
            QueryBuilder.LOG_VALUES = true
        }
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    fun closeConnection() {
        closeHelper()
        closeDaoSession()
    }

    fun closeHelper() {
        sHelper?.close()
        sHelper = null
    }

    fun closeDaoSession() {
        daoSession.clear()
    }
}