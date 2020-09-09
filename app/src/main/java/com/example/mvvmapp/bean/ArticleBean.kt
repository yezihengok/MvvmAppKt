//package com.example.mvvmapp.bean
//
//import com.example.mvvmapp.db.converter.MaterialReleaseInfoConverter
//import com.example.mvvmapp.db.converter.ParagrapBeanConvent
//import com.example.mvvmapp.db.converter.StringConverter
//import com.google.gson.annotations.SerializedName
//import org.greenrobot.greendao.annotation.Convert
//import org.greenrobot.greendao.annotation.Entity
//import org.greenrobot.greendao.annotation.Generated
//import org.greenrobot.greendao.annotation.Id
//import java.io.Serializable
//
///**
// * 文章
// */
//// @SerializedName 注解与 greenDao无关，可以不需要.
//@Entity
//class ArticleBean : BaseBean, Serializable {
//    @Id(autoincrement = true)
//    @SerializedName("_id")
//    private var _id: Long? = null
//
//    @SerializedName("title")
//    var title: String? = null
//
//    /**
//     * 文章等级
//     */
//    @SerializedName("level")
//    var level: String? = null
//
//    /**
//     * 文章id
//     */
//    @SerializedName("articleId")
//    var articleId: String? = null
//
//    /**
//     * 文章图片
//     */
//    @SerializedName("cover")
//    var cover: String? = null
//
//    /**
//     * 等级关键次
//     */
//    @Convert(converter = StringConverter::class, columnType = String::class)
//    @SerializedName("keyWords")
//    var keyWords: List<String>? = null
//
//    /**
//     * 段落
//     */
//    @Convert(converter = ParagrapBeanConvent::class, columnType = String::class)
//    @SerializedName("paragraphList")
//    var paragraphList: List<ParagrapBean>? = null
//
//    /**
//     * 文章显示类型
//     */
//    @SerializedName("style")
//    var style: String? = null
//
//    /**
//     * 是否收藏这篇文章
//     */
//    @SerializedName("collected")
//    var isCollected = false
//
//    /**
//     * 文章类型
//     */
//    @SerializedName("contentType")
//    var contentType: String? = null
//    fun get_id(): Long? {
//        return _id
//    }
//
//    fun set_id(_id: Long?) {
//        this._id = _id
//    }
//    //文章类型
//    //    英文绘本以一级类：enView_category
//    //    二级类：
//    //    默认绘本 enView
//    //    好词好句 enViewSentence
//    //
//    //    中文绘本_一级分类 cnView_category
//    //    二级类：
//    //    普通绘本 cnView
//    //    小古文 cnViewProse
//    //    名言警句 cnViewAphorism
//    //    好句好段 cnViewSentence
//    //    章节书 cnViewChapter
//    //
//    //    中文韵文_一级分类 cnVerse_category
//    //    二级类：
//    //    普通韵文 cnVerse
//    //    古诗词 cnVerseProse
//    /**
//     * 总金币
//     */
//    @SerializedName("totalCredits")
//    var totalCredits = 0
//
//    /**
//     * 将要奖励的金币
//     */
//    @SerializedName("rewardCredits")
//    var rewardCredits = 0
//
//    /**
//     * 跟读间隔时间
//     */
//    @SerializedName("recordInterval")
//    var recordInterval = 0.0
//
//    @Convert(
//        converter = MaterialReleaseInfoConverter::class,
//        columnType = String::class
//    ) // @Convert(converter = CommBeanConvent<>.class , columnType = String.class)
//    @SerializedName("releaseInfo")
//    var releaseInfo //小古文&名言警句 标题来源作者等信息
//            : MaterialReleaseInfo? = null
//
//    /**
//     * (目前接口无此字段自己手动设值区分)文章内容的具体细分如   小古文&名言警句cnViewAphorism_cnViewProse
//     */
//    @SerializedName("category")
//    var category: String? = null
//
//    @SerializedName("subject")
//    var subject //内容语言 CN EN （接口无返回手动设值）
//            : String? = null
//
//    /**
//     * 背景音乐
//     */
//    @SerializedName("musicOver")
//    var musicOver: String? = null
//
//    @Generated(hash = 1048575158)
//    constructor(
//        _id: Long?, title: String?, level: String?, articleId: String?, cover: String?,
//        keyWords: List<String>?, paragraphList: List<ParagrapBean>?, style: String?,
//        collected: Boolean, contentType: String?, totalCredits: Int, rewardCredits: Int,
//        recordInterval: Double, releaseInfo: MaterialReleaseInfo?, category: String?,
//        subject: String?, musicOver: String?
//    ) {
//        this._id = _id
//        this.title = title
//        this.level = level
//        this.articleId = articleId
//        this.cover = cover
//        this.keyWords = keyWords
//        this.paragraphList = paragraphList
//        this.style = style
//        isCollected = collected
//        this.contentType = contentType
//        this.totalCredits = totalCredits
//        this.rewardCredits = rewardCredits
//        this.recordInterval = recordInterval
//        this.releaseInfo = releaseInfo
//        this.category = category
//        this.subject = subject
//        this.musicOver = musicOver
//    }
//
//    @Generated(hash = 392728754)
//    constructor() {
//    }
//
//    fun getCollected(): Boolean {
//        return isCollected
//    }
//
//    class MaterialReleaseInfo : Serializable {
//        @SerializedName("author")
//        var author //作者
//                : String? = null
//
//        @SerializedName("authorVoice")
//        var authorVoice: String? = null
//
//        @SerializedName("source")
//        var source //来源
//                : String? = null
//
//        @SerializedName("sourceVoice")
//        var sourceVoice: String? = null
//
//        @SerializedName("dynasty")
//        var dynasty //朝代
//                : String? = null
//
//        @SerializedName("dynastyVoice")
//        var dynastyVoice: String? = null
//
//        @SerializedName("contentLayout")
//        var contentLayout: String? = null
//
//        companion object {
//            private const val serialVersionUID = -7668916722391784402L
//        }
//    }
//
//    companion object {
//        private const val serialVersionUID = 8144016205336010992L
//    }
//}