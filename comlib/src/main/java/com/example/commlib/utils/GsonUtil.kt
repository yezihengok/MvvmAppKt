package com.example.commlib.utils

import android.text.TextUtils
import com.blankj.ALog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*

/**
 * Gson 工具类
 */
object GsonUtil {
    private val TAG = GsonUtil::class.java.simpleName
    private val gson = Gson()

    /**
     * 把一个map变成json字符串
     *
     * @param map
     * @return
     */
    fun parseMapToJson(map: Map<*, *>?): String? {
        try {
            return gson.toJson(map)
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * 把一个json字符串变成对象
     *
     * @param json
     * @param cls
     * @return
     */
    fun <T> parseJsonToBean(json: String?, cls: Class<T>?): T? {
        var t: T? = null
        try {
            t = gson.fromJson(json, cls)
        } catch (e: Exception) {
        }
        return t
    }

    /**
     * 把json字符串变成map
     *
     * @param json
     * @return
     */
    fun parseJsonToMap(json: String?): HashMap<String?, Any?>? {
        val type = object : TypeToken<HashMap<String?, Any?>?>() {}.type
        var map: HashMap<String?, Any?>? = null
        try {
            map = gson.fromJson<HashMap<String?, Any?>>(json, type)
        } catch (e: Exception) {
            ALog.v(TAG, "转换异常" + e.message)
        }
        return map
    }

    /**
     * 把json字符串变成集合
     * params: new TypeToken<List></List><yourbean>>(){}.getType(),
     *
     * @param json
     * @param type new TypeToken<List></List><yourbean>>(){}.getType()
     * @return
    </yourbean></yourbean> */
    fun parseJsonToList(json: String?, type: Type?): List<*> {
        return gson.fromJson(json, type)
    }

    /**
     * 把  list  转成  Strin   json
     *
     * @param list
     * @return
     */
    fun parseListToJson(list: List<*>?): String {
        return gson.toJson(list)
    }

    /**
     * 获取json串中某个字段的值，注意，只能获取同一层级的value
     *
     * @param json
     * @param key
     * @return
     */
    fun getFieldValue(json: String, key: String): String? {
        if (TextUtils.isEmpty(json)) return null
        if (!json.contains(key)) return ""
        var jsonObject: JSONObject? = null
        var value: String? = null
        try {
            jsonObject = JSONObject(json)
            value = jsonObject.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 格式化json
     *
     * @param uglyJSONString
     * @return
     */
    fun jsonFormatter(uglyJSONString: String?): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jp = JsonParser()
        val je = jp.parse(uglyJSONString)
        return gson.toJson(je)
    }

    fun getJsonToMap(json: String?): Map<String, String> {
        return gson.fromJson(json, object : TypeToken<Map<String?, String?>?>() {}.type)
    }

    /**
     * 通过  bean    返回 json
     *
     * @param bean
     * @return
     */
    fun getBeanToJson(bean: Any?): String {
        val gson = Gson()
        return gson.toJson(bean)
    }

    //----------------------手动解析json类---------------------------
    fun getJSONArray(`object`: JSONObject, name: String): JSONArray? {
        if (`object`.isNull(name)) {
            ALog.e("没有找到节点：$name")
            return null
        }
        return try {
            `object`.getJSONArray(name)
        } catch (e: JSONException) {
            //Log.e("", e.getMessage());
            null
        }
    }

    /**
     * 加工json节点的数据，如果有节点则取节点数据，如果没有节点则返回空字符串
     * @param object
     * @param name
     * @return
     */
    fun getString(`object`: JSONObject?, name: String?): String {
        if (`object` == null) {
            return ""
        }
        return if (`object`.isNull(name)) {
            //CommUtils.logD("没有找到节点：" + name);
            ""
        } else try {
            `object`.getString(name)
        } catch (e: JSONException) {
            //Log.e("", e.getMessage());
            ""
        }
    }

    fun getDoubleString(`object`: JSONObject, name: String?): String {
        return if (`object`.isNull(name)) {
            //Log.e("", "没有找到节点："+name);
            "0"
        } else try {
            `object`.getString(name)
        } catch (e: JSONException) {
            //Log.e("", e.getMessage());
            "0"
        }
    }

    /**
     * 获取json节点的数据并换行为Long型，否则返回0
     * @param object
     * @param name
     * @return
     */
    fun getDouble(`object`: JSONObject, name: String?): Double {
        return if (`object`.isNull(name)) {
            0.0
        } else try {
            `object`.getDouble(name)
        } catch (e: JSONException) {
            0.0
        }
    }

    fun getInt(`object`: JSONObject, name: String?): Int {
        return if (`object`.isNull(name)) {
            0
        } else try {
            `object`.getInt(name)
        } catch (e: JSONException) {
            0
        }
    }

    /**
     * <获取json节点的数据并换行为int型></获取json节点的数据并换行为int型>，否则返回0>
     *
     */
    fun getIntString(`object`: JSONObject, name: String?): String {
        return if (`object`.isNull(name)) {
            "0"
        } else try {
            `object`.getString(name)
        } catch (e: JSONException) {
            "0"
        }
    }
}