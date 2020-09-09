package com.example.mvvmapp.db.converter

import com.example.mvvmapp.bean.ParagrapBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.greendao.converter.PropertyConverter
import java.util.*

/**
 * Created by yzh on 2020/6/9 14:02.
 */
class ParagrapBeanConvent : PropertyConverter<List<ParagrapBean>, String?> {
    private val mGson: Gson by lazy {
        Gson()
    }
    override fun convertToEntityProperty(databaseValue: String?): List<ParagrapBean> {
        val type = object : TypeToken<ArrayList<ParagrapBean?>?>() {}.type
        return mGson.fromJson<ArrayList<ParagrapBean>>(databaseValue, type)
    }

    override fun convertToDatabaseValue(entityProperty: List<ParagrapBean>?): String? {
        return mGson.toJson(entityProperty)
    }

}