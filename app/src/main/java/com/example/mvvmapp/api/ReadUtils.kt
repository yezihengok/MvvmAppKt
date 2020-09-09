package com.example.mvvmapp.api

import android.view.View
import android.widget.TextView

/**
 * 阅读相关工具类
 * Created by yzh on 2020/8/31 17:20.
 */
class ReadUtils {
    companion object {
        val instance: ReadUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ReadUtils() }
    }

    fun setCategoryTv(tv:TextView?,category:String?=""){
        val content = when (category) {
            "cnView","enView" -> "普通绘本"
            "cnVerse","enVerse"->""
            "cnViewSentence" -> "好句好段"
            "cnViewProse" -> "小古文"
            "cnVerseProse"-> "小古文"
            "cnViewModernPoetry" -> "普通绘本"
            "cnViewAphorism" -> "普通绘本"
            "cnWords"->""
            "cnViewChapter","enViewChapter" -> "普通绘本"

            "enViewWellKnown"-> "普通绘本"
            "enViewCartoon"-> "普通绘本"
            "enViewBellesLettres"-> "普通绘本"
            "enViewSentence"-> "好词好句"
            "enViewClassicNews"-> "普通绘本"
            else -> ""
        }
        tv?.visibility= if(content.isEmpty()) View.GONE else View.VISIBLE
        tv?.text = content
    }
}