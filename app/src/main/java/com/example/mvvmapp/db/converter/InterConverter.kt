package com.example.mvvmapp.db.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.greendao.converter.PropertyConverter
import java.util.*

/**
 * * GreenDao实体类默认不支持List，数组和Object类型
 * * 需要自己定义转换器，将复杂类型转换为基本类型，再存到数据库
 * * 获取的时候，再将基本类型转换为复杂类型
 * Created by yzh on 2020/6/9 11:46.
 */
class InterConverter : PropertyConverter<List<Int>, String?> {
    private val mGson: Gson by lazy {
        Gson()
    }
    override fun convertToEntityProperty(databaseValue: String?): List<Int> {
        val type = object : TypeToken<ArrayList<Int?>?>() {}.type
        return mGson.fromJson<ArrayList<Int>>(databaseValue, type)
    }

    override fun convertToDatabaseValue(entityProperty: List<Int>?): String? {
        return mGson.toJson(entityProperty)
    }

}