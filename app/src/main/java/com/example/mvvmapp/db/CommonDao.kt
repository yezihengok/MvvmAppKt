package com.example.mvvmapp.db

import android.util.Log
import com.primary.greendao.gen.DaoSession
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.query.WhereCondition

/**
 * 公用DaoUtil ,避免使用一个对象就创建一个DaoUtil
 * Created by yzh on 2020/6/10 11:03.
 */
class CommonDao<T> constructor(pEntityClass: Class<T>, pEntityDao: AbstractDao<T, Long>) {


    companion object {
        private val TAG = CommonDao::class.java.simpleName
    }

    private val daoSession: DaoSession?
    private val entityClass: Class<T>
    private val entityDao: AbstractDao<T, Long>


    init {
        val mManager: DaoManager = DaoManager.instance
        daoSession = mManager.daoSession
        entityClass = pEntityClass
        entityDao = pEntityDao
    }

    /**
     * 插入记录，如果表未创建，先创建表
     *
     * @param pEntity
     * @return
     */
    fun insert(pEntity: T): Boolean {
        val flag = entityDao.insert(pEntity) != -1L
        Log.i(TAG, "insert Meizi :" + flag + "-->" + pEntity.toString())
        return flag
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param pEntityList
     * @return
     */
    fun insertMulti(pEntityList: List<T>): Boolean {
        try {
            daoSession?.runInTx {
                for (meiji in pEntityList) {
                    daoSession.insertOrReplace(meiji)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 修改一条数据
     *
     * @param pEntity
     * @return
     */
    fun update(pEntity: T): Boolean {
        try {
            daoSession?.update(pEntity)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 删除单条记录
     *
     * @param pEntity
     * @return
     */
    fun delete(pEntity: T): Boolean {
        try {
            //按照id删除
            daoSession?.delete(pEntity)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    fun deleteAll(): Boolean {
        try {
            //按照id删除
            daoSession?.deleteAll(entityClass)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    fun queryAll(): List<T> {
       // daoSession?.loadAll<T,Any>(entityClass)
        return daoSession?.loadAll<T,Any>(entityClass)?: emptyList()
    }

    /**
     * 根据主键_id查询记录
     *
     * @param key
     * @return
     */
    fun queryById(key: Long): T? {
        return daoSession?.load(entityClass, key)
    }

    /**
     * 使用native sql进行查询操作
     */
    fun queryByNativeSql(sql: String?, conditions: Array<String?>): List<T>? {
        return daoSession?.queryRaw<T,Any>(entityClass, sql, *conditions)
    }
    //    public List<T> queryData(String s) {
    //        List<Student> students = daoSession.queryRaw(Student.class, " where id = ?", s);
    //    }
    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    fun queryByQueryBuilder(cond: WhereCondition?, vararg condMore: WhereCondition?): List<T> {
        val queryBuilder = daoSession!!.queryBuilder(entityClass)
        return queryBuilder.where(cond, *condMore).list()
    }

}