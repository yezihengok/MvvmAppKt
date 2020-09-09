package com.example.mvvmapp.db.upgrade

import android.database.Cursor
import android.text.TextUtils
import android.util.Log
import com.primary.greendao.gen.DaoMaster
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.database.Database
import org.greenrobot.greendao.internal.DaoConfig
import java.util.*

/**
 * 数据库迁移
 */
class DBMigrationHelper {

    companion object {
        var instance: DBMigrationHelper? = null
            get() {
                if (field == null) {
                    field = DBMigrationHelper()
                }
                return field
            }
            private set

    }

    /**
     * 数据库迁移
     *
     * @param db
     * @param daoClasses
     */
    fun migrate(db: Database?, vararg daoClasses: Class<*>?) {
        if (db==null){
            return
        }
        //生成临时表，复制表数据
        generateTempTables(db, *daoClasses as Array<out Class<*>>)
        DaoMaster.dropAllTables(db, true)
        DaoMaster.createAllTables(db, false)
        //恢复数据
        restoreData(db, *daoClasses)
    }

    /**
     * 生成临时表，复制表数据
     *
     * @param db
     * @param daoClasses
     */
    private fun generateTempTables(db: Database, vararg daoClasses: Class<*>) {
        for (element in daoClasses) {
            val daoConfig = DaoConfig(db, element as Class<out AbstractDao<*, *>>?)
            var divider = ""
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_TEMP"
            val properties: ArrayList<String> = ArrayList()
            val createTableStringBuilder = StringBuilder()
            createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (")
            for (j in daoConfig.properties.indices) {
                val columnName = daoConfig.properties[j].columnName
                if (getColumns(db, tableName).contains(columnName)) {
                    properties.add(columnName)
                    var type: String? = null
                    try {
                        type = getTypeByClass(daoConfig.properties[j].type)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    createTableStringBuilder.append(divider).append(columnName).append(" ").append(type)
                    if (daoConfig.properties[j].primaryKey) {
                        createTableStringBuilder.append(" PRIMARY KEY")
                    }
                    divider = ","
                }
            }
            createTableStringBuilder.append(");")
            db.execSQL(createTableStringBuilder.toString())
            Log.e("DBMigrationHelper", "generateTempTables: sql:$createTableStringBuilder")
            val insertTableStringBuilder = StringBuilder()
            insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (")
            insertTableStringBuilder.append(TextUtils.join(",", properties))
            insertTableStringBuilder.append(") SELECT ")
            insertTableStringBuilder.append(TextUtils.join(",", properties))
            insertTableStringBuilder.append(" FROM ").append(tableName).append(";")
            db.execSQL(insertTableStringBuilder.toString())
            Log.e("DBMigrationHelper", "generateTempTables: sql:$insertTableStringBuilder")
        }
    }

    /**
     * 恢复数据
     *
     * @param db
     * @param daoClasses
     */
    private fun restoreData(db: Database, vararg daoClasses: Class<*>) {
        for (element in daoClasses) {
            val daoConfig = DaoConfig(db, element as Class<out AbstractDao<*, *>>?)
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_TEMP"
            val properties: ArrayList<String> = ArrayList()
            val propertiesQuery: ArrayList<String> = ArrayList()
            for (j in daoConfig.properties.indices) {
                val columnName = daoConfig.properties[j].columnName
                if (getColumns(db, tempTableName).contains(columnName)) {
                    properties.add(columnName)
                    propertiesQuery.add(columnName)
                } else {
                    try {
                        if (getTypeByClass(daoConfig.properties[j].type) == "INTEGER") {
                            propertiesQuery.add("0 as $columnName")
                            properties.add(columnName)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val insertTableStringBuilder = StringBuilder()
            insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (")
            insertTableStringBuilder.append(TextUtils.join(",", properties))
            insertTableStringBuilder.append(") SELECT ")
            insertTableStringBuilder.append(TextUtils.join(",", propertiesQuery))
            insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";")
            val dropTableStringBuilder = StringBuilder()
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName)
            db.execSQL(insertTableStringBuilder.toString())
            db.execSQL(dropTableStringBuilder.toString())
            Log.e("DBMigrationHelper", "restoreData: sql:$insertTableStringBuilder")
            Log.e("DBMigrationHelper", "restoreData: sql:$dropTableStringBuilder")
        }
    }

    /**
     * @param type
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getTypeByClass(type: Class<*>): String {
        if (type == String::class.java) {
            return "TEXT"
        }
        if (type == Long::class.java || type == Int::class.java || type == Long::class.javaPrimitiveType || type == Int::class.javaPrimitiveType) {
            return "INTEGER"
        }
        if (type == Boolean::class.java || type == Boolean::class.javaPrimitiveType) {
            return "BOOLEAN"
        }
        val exception = Exception("migration helper - class doesn't match with the current parameters - Class: $type")
        exception.printStackTrace()
        throw exception
    }


    /**
     * 获取列的名字
     *
     * @param db
     * @param tableName
     * @return
     */
    private fun getColumns(db: Database, tableName: String): List<String?> {
        var columns: List<String?> = ArrayList()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $tableName limit 1", null)
            if (cursor != null) {
                columns = ArrayList(listOf(*cursor.columnNames))
            }
        } catch (e: Exception) {
            Log.e(tableName, e.message, e)
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return columns
    }

}