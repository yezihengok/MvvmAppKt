package com.example.mvvmapp.db

import com.example.mvvmapp.bean.ArticleBean
import com.example.mvvmapp.bean.TestBean

/**
 * 存放DaoUtils
 * Created by yzh on 2020/6/10 11:05.
 */
class DaoUtilsStore private constructor() {
    private val mManager: DaoManager =DaoManager.instance

    companion object {
        val instance: DaoUtilsStore by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DaoUtilsStore()
        }
    }

    val mArticleBeanUtil: CommonDao<ArticleBean> by lazy {
        val articleBeanDao = mManager.daoSession.articleBeanDao
        CommonDao(ArticleBean::class.java, articleBeanDao)
    }

    val mTestBeanUtil:CommonDao<TestBean> by lazy {
        val testBeanDao = mManager.daoSession.testBeanDao
         CommonDao(TestBean::class.java, testBeanDao)
    }



//    var mArticleBeanUtil: CommonDao<ArticleBean>? = null
//    val articleBeanUtil: CommonDao<ArticleBean>
//        get() {
//            if (mArticleBeanUtil == null) {
//                val articleBeanDao = mManager.daoSession.articleBeanDao
//                mArticleBeanUtil = CommonDao(ArticleBean::class.java, articleBeanDao)
//            }
//            return mArticleBeanUtil!!
//        }

//    var mTestBeanUtil: CommonDao<TestBean>? = null
//    val testBeanUtil: CommonDao<TestBean>
//        get() {
//            if (mTestBeanUtil == null) {
//                val testBeanDao = mManager.daoSession.testBeanDao
//                mTestBeanUtil = CommonDao(TestBean::class.java, testBeanDao)
//            }
//            return mTestBeanUtil!!
//        }


}