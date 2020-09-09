package com.example.mvvmapp.db

import com.blankj.ALog
import com.example.commlib.api.App
import com.example.mvvmapp.bean.ArticleBean
import com.primary.greendao.gen.ArticleBeanDao

/**
 * Created by yzh on 2020/6/9 15:18.
 */
class ArticleDao {
//    private val mManager: DaoManager?
//    init {
//        mManager = DaoManager.getInstance()
//        mManager!!.init(App.instance)
//    }
    private val mManager: DaoManager by lazy {
        DaoManager.instance.apply {
            init(App.instance)
        }
    }

    /**
     * 完成ArticleBean记录的插入，如果表未创建，先创建ArticleBean表
     * @param articleBean
     * @return
     */
    fun insertArticleBean(articleBean: ArticleBean): Boolean {
        var flag = false
        flag = mManager.daoSession.articleBeanDao.insert(articleBean) != -1L
        ALog.i(TAG, "insert ArticleBean :$flag-->$articleBean")
        return flag
    }

    /**
     * 插入多条数据，在子线程操作
     * @param articleBeanList
     * @return
     */
    fun insertMultArticleBean(articleBeanList: List<ArticleBean>): Boolean {
        var flag = false
        try {
            mManager.daoSession.runInTx {
                for (ArticleBean in articleBeanList) {
                    mManager.daoSession.insertOrReplace(ArticleBean)
                }
            }
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return flag
    }

    /**
     * 修改一条数据
     * @param articleBean
     * @return
     */
    fun updateArticleBean(articleBean: ArticleBean): Boolean {
        var flag = false
        try {
            mManager.daoSession.update(articleBean)
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return flag
    }

    /**
     * 删除单条记录
     * @param articleBean
     * @return
     */
    fun deleteArticleBean(articleBean: ArticleBean): Boolean {
        var flag = false
        try {
            //按照id删除
            mManager.daoSession.delete(articleBean)
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return flag
    }

    /**
     * 删除所有记录
     * @return
     */
    fun deleteAll(): Boolean {
        var flag = false
        try {
            //按照id删除
            mManager.daoSession.deleteAll(ArticleBean::class.java)
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return flag
    }

    /**
     * 查询所有记录
     * @return
     */
    fun queryAllArticleBean(): List<ArticleBean> {
        return mManager.daoSession.loadAll<ArticleBean, Any>(ArticleBean::class.java)
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    fun queryArticleBeanById(key: Long): ArticleBean {
        return mManager.daoSession.load(ArticleBean::class.java, key)
    }

    /**
     * 使用native sql进行查询操作
     */
    fun queryArticleBeanByNativeSql(sql: String?, conditions: Array<String?>): List<ArticleBean> {
        return mManager.daoSession.queryRaw<ArticleBean, Any>(ArticleBean::class.java, sql, *conditions)
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    fun queryArticleBeanByQueryBuilder(id: Long): List<ArticleBean> {
        val queryBuilder = mManager.daoSession.queryBuilder(ArticleBean::class.java)
        return queryBuilder.where(ArticleBeanDao.Properties._id.eq(id)).list()
        //        return queryBuilder.where(ArticleBeanDao.Properties._id.ge(id)).list();
    }

    companion object {
        private val TAG = ArticleDao::class.java.simpleName
    }

}