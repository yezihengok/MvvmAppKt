package com.example.mvvmapp.db.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.greendao.converter.PropertyConverter
import java.util.*

/**
 * Created by yzh on 2020/8/27 10:22.
 */
class CommBeanConvent<T> : PropertyConverter<List<T>,String> {
    val mGson:Gson by lazy {
        Gson()
    }
    override fun convertToEntityProperty(databaseValue: String?): List<T> {
        //Type type = new TypeToken<ArrayList<T>>() {}.getType();
        val type= object :TypeToken<ArrayList<T>>() {}.type
        return mGson.fromJson<ArrayList<T>?>(databaseValue, type)!!
    }

    override fun convertToDatabaseValue(entityProperty: List<T>?): String {
       return mGson.toJson(entityProperty)
    }
}