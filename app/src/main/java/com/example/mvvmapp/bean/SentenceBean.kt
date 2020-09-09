package com.example.mvvmapp.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 句
 */
class SentenceBean(
        /**
         * 句子
         */
        @field:SerializedName("sentenceId") var sentenceId: String,
        /**
         * 句的图片
         */
        @field:SerializedName("voice") var voice: String,
        /**
         * 句子
         */
        @field:SerializedName("content") var content: String) : BaseBean(), Serializable {

    /**
     * 句子配音
     */
    @SerializedName("voiceOver")
    var voiceOver: String? = null

    @SerializedName("voiceDesc")
    var voiceDesc: String? = null

    @SerializedName("translatel")
    var translatel: String? = null

    /**
     * 不读句的下标  [0-1,3-5,8-22]
     */
    @SerializedName("noReadIndexList")
    var noReadIndexList: List<String>? = null

}