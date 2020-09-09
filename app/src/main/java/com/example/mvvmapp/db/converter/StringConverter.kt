package com.example.mvvmapp.db.converter

import org.greenrobot.greendao.converter.PropertyConverter
import java.util.*

/**
 * GreenDao实体类默认不支持List，数组和Object类型
 * 需要自己定义转换器，将复杂类型转换为基本类型，再存到数据库
 * 获取的时候，再将基本类型转换为复杂类型
 * Created by yzh on 2020/6/9 11:47.
 */
class StringConverter : PropertyConverter<List<String>?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): List<String>? {
        return if (databaseValue == null) {
            null
        } else {
            //List<String> list = Arrays.asList(databaseValue.split(SPIT));
            arrayListOf(*databaseValue.split(SPIT).toTypedArray())
        }
    }

    override fun convertToDatabaseValue(entityProperty: List<String>?): String? {
        return if (entityProperty == null) {
            null
        } else {
            val sb = StringBuilder()
            for (link in entityProperty) {
                sb.append(link)
                sb.append(SPIT)
            }
            sb.toString()
        }
    }

    companion object {
        private const val SPIT = "#;#"
    }
}