package com.example.mvvmapp.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.blankj.ALog
import com.example.commlib.base.mvvm.BaseViewModel
import com.example.commlib.event.SingleLiveEvent
import com.example.commlib.utils.GsonUtil.getBeanToJson
import com.example.commlib.utils.GsonUtil.parseJsonToBean
import com.example.commlib.utils.ToastUtils
import com.example.mvvmapp.bean.ArticleBean
import com.example.mvvmapp.bean.TestBean
import com.example.mvvmapp.db.CommonDao
import com.example.mvvmapp.db.DaoUtilsStore

/**
 * Created by yzh on 2020/6/9 15:08.
 */
class GreenDaoViewModel constructor(application: Application) : BaseViewModel(application) {
    @kotlin.jvm.JvmField
    var mContent: ObservableField<String> = ObservableField()

    //在onBindingClick里调用了call，
    @kotlin.jvm.JvmField
    var updateEvent: SingleLiveEvent<Void> = SingleLiveEvent()
    @kotlin.jvm.JvmField
    var deleteEvent: SingleLiveEvent<Void> = SingleLiveEvent()

    //文本更新Event
    var contentChangeEvent: SingleLiveEvent<Void> = SingleLiveEvent()
    var addEvent: SingleLiveEvent<Void> = SingleLiveEvent()

    // ArticleDao daoUtil;
    private var mDaoUtil: CommonDao<ArticleBean> = DaoUtilsStore.instance.mArticleBeanUtil

    init {
        // daoUtil=new ArticleDao();
    }

     override fun onBundle(bundle: Bundle?) {}
    private val json: String = """
        {
            "articleId": "5eb3d5735e15a63ac692ef21",
            "collected": true,
            "contentType": "cnViewChapter",
            "keyWords": [
                "上",
                "下",
                "小"
            ],
            "level": "2",
            "paragraphList": [
                {
                    "coverPosition": "up",
                    "sentenceList": [{
                        "content": "章节书书1",
                        "sentenceId": "0",
                        "voice": "http://test.pub.muyuhuajiaoyu.com/1094799970789236736.m3u8",
                        "voiceDesc": "",
                        "voiceOver": ""
                    }]
                },
                {
                    "coverPosition": "down",
                    "sentenceList": [{
                        "content": "\u201c什么啊？\u201d小猪皮杰跳了起来，为了表明自己刚才并不是被吓到了。",
                        "sentenceId": "3",
                        "voice": "http://test.pub.muyuhuajiaoyu.com/1094799970789236736.m3u8",
                        "voiceDesc": "",
                        "voiceOver": ""
                    }]
                }
            ],
            "recordInterval": 0,
            "rewardCredits": 5,
            "subject": "EN",
            "title": "章节书书1",
            "totalCredits": 318857
        }
    """.trimIndent()

    val testData: MutableLiveData<List<ArticleBean?>?>
        get() {
            val data: MutableLiveData<List<ArticleBean?>?> = MutableLiveData()
            data.setValue(mDaoUtil.queryAll())
            return data
        }

    /**
     * 更新显示
     */
    fun updateContent() {
        //List<ArticleBean> beanList=daoUtil.queryAllArticleBean();
        val beanList: List<ArticleBean?>? = mDaoUtil.queryAll()
        val builder: StringBuilder = StringBuilder()
        for (bean: ArticleBean? in beanList!!) {
            ALog.i(getBeanToJson(bean))
            builder.append(getBeanToJson(bean)).append("\n").append("\n")
        }
        mContent.set(builder.toString())
        contentChangeEvent.call()

        //测试TestBean
        val beans: List<TestBean> = DaoUtilsStore.instance.mTestBeanUtil.queryAll()
        for (bean: TestBean? in beans) {
            ALog.v(getBeanToJson(bean))
        }
    }

    fun add() {
        val bean: ArticleBean? = parseJsonToBean(json, ArticleBean::class.java)
        // daoUtil.insertArticleBean(bean);
        mDaoUtil.insert((bean)!!)
        updateContent()
        addEvent.call()

        //测试TestBean
        val testBean: TestBean = TestBean("111", "222", "aaa")
        DaoUtilsStore.instance.mTestBeanUtil.insert(testBean)
    }

    fun deleteAll() {
        mDaoUtil.deleteAll()
        updateContent()
    }

    /**
     * 根据id修改
     * @param _id
     */
    fun update(_id: Int, content: String?) {
        //ArticleBean bean= daoUtil.queryArticleBeanById(id);
        val bean: ArticleBean? = mDaoUtil.queryById(_id.toLong())
        if (bean == null) {
            ToastUtils.showShort("$_id 不存在")
            return
        }
        bean.contentType = content
        //daoUtil.updateArticleBean(bean);
        mDaoUtil.update(bean)
        updateContent()
    }

    /**
     * 根据id删除
     * @param _id
     */
    fun delete(_id: Int) {
        val bean: ArticleBean? = mDaoUtil.queryById(_id.toLong())
        if (bean == null) {
            ToastUtils.showShort("$_id 不存在")
            return
        }
        mDaoUtil.delete(bean)
        updateContent()
    }

}