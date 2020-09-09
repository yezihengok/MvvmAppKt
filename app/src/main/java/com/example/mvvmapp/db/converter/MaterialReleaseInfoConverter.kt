package com.example.mvvmapp.db.converter

import com.example.mvvmapp.bean.ArticleBean.MaterialReleaseInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.greendao.converter.PropertyConverter

/**
 * Created by yzh on 2020/6/9 14:16.
 */
class MaterialReleaseInfoConverter : PropertyConverter<MaterialReleaseInfo, String?> {
    private val mGson: Gson by lazy {
        Gson()
    }
    override fun convertToEntityProperty(databaseValue: String?): MaterialReleaseInfo {
        val type = object : TypeToken<MaterialReleaseInfo?>() {}.type
        return mGson.fromJson(databaseValue, type)
    }

    override fun convertToDatabaseValue(entityProperty: MaterialReleaseInfo?): String? {
        return mGson.toJson(entityProperty)
    }

}