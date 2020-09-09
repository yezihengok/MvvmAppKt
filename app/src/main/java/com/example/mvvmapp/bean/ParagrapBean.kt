package com.example.mvvmapp.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 段落
 */
class ParagrapBean : BaseBean(), Serializable {
    @SerializedName("cover")
    var cover //段落图片
            : String? = null

    @SerializedName("sentenceList")
    var sentenceList //句子列表
            : List<SentenceBean>? = null

    @SerializedName("axis")
    var axis: List<String>? = null

    @SerializedName("width")
    var width: String? = null

    @SerializedName("coverPosition")
    var coverPosition //  up  down  图片位置
            : String? = null

    @SerializedName("readId") // modify morong
    var readId: String? = null

    companion object {
        private const val serialVersionUID = -7031853677375013655L
    }
}