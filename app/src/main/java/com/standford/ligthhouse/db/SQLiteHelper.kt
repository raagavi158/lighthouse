package com.standford.ligthhouse.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.standford.ligthhouse.model.BodyList
import com.standford.ligthhouse.model.Data
import com.standford.ligthhouse.model.LinkModel
import org.json.JSONObject
import java.util.*


class SQLiteHelper(context: Context?) {

    var database: SQLiteDatabase? = null
    var dbHandler: SQLiteHandler

    fun open() {
        database = dbHandler.writableDatabase
    }

    fun close() {
        val sQLiteDatabase = database
        if (sQLiteDatabase != null && sQLiteDatabase.isOpen) {
            dbHandler.close()
        }
    }

    fun stuffInsert(
        createdDate: String?,
        id: String?,
        profileId: String?,
        identifier: String?,
        topline: String?,
        rank: String?,
        score: Int?,
        country: String?,
        language: String?,
        writeup: String?,
        criteria: String?,
        active: Boolean?,
        healthGuard: Boolean?,
        locale: String?
    ) {
        Log.e("database.insert:id:", id.toString())
        val contentValues = ContentValues()
        contentValues.put("createdDate", createdDate)
        contentValues.put("data_id", id)
        contentValues.put("profileId", profileId)
        contentValues.put("identifier", identifier)
        contentValues.put("topline", topline)
        contentValues.put("rank", rank)
        contentValues.put("score", score)
        contentValues.put("country", country)
        contentValues.put("language", language)
        contentValues.put("writeup", writeup)
        contentValues.put("criteria", criteria)
        contentValues.put("active", active)
        contentValues.put("healthGuard", healthGuard)
        contentValues.put("locale", locale)
        database!!.insert("api_data_ST", null, contentValues)
    }

    fun stuffInsertMessage(
        linkModel: LinkModel
    ) {
        val gson = Gson()

        val contentValues = ContentValues()
        contentValues.put("sender", linkModel.sender)
        contentValues.put("link", linkModel.link)
        contentValues.put("originalMessage", linkModel.originalMessage)
        contentValues.put("identifier", linkModel.identifier)
        contentValues.put("topline", linkModel.topline)
        contentValues.put("rank", linkModel.rank)
        contentValues.put("score", linkModel.score)
        contentValues.put("writeup", gson.toJson(linkModel.writeup))
        contentValues.put(
            "messagesContainingDomain",
            gson.toJson(linkModel.messagesContainingDomain)
        )
        database!!.insert("api_data_ST_message", null, contentValues)
    }


    fun bodyInsert(
        profileId: String?,
        title: String?,
        body: String?,
        order: Int?
    ) {
        val contentValues = ContentValues()
        contentValues.put("profileId", profileId)
        contentValues.put("title", title)
        contentValues.put("body", body)
        contentValues.put("body_order", order)
        database!!.insert("body_list", null, contentValues)
    }

    fun stuffContentInsert(contentValues: ContentValues?) {
        val sb = StringBuilder()
        sb.append("")
        sb.append(database!!.insert("api_data_ST", null, contentValues))
        Log.e("database.insert::", sb.toString())
    }

    fun bodyContentInsert(contentValues: ContentValues?) {
        val sb = StringBuilder()
        sb.append("")
        sb.append(database!!.insert("body_list", null, contentValues))
        Log.e("database.insert::", sb.toString())
    }

    fun stuffUpdate(
        createdDate: String?,
        id: String?,
        profileId: String?,
        identifier: String?,
        topline: String?,
        rank: String?,
        score: Int?,
        country: String?,
        language: String?,
        writeup: List<BodyList>?,
        criteria: String?,
        active: Boolean?,
        healthGuard: Boolean?,
        locale: String?
    ) {
        val contentValues = ContentValues()
        contentValues.put("createdDate", createdDate)
        contentValues.put("data_id", id)
        contentValues.put("profileId", profileId)
        contentValues.put("identifier", identifier)
        contentValues.put("topline", topline)
        contentValues.put("rank", rank)
        contentValues.put("score", score)
        contentValues.put("country", country)
        contentValues.put("language", language)
//        contentValues.put("writeup", writeup)
        contentValues.put("criteria", criteria)
        contentValues.put("active", active)
        contentValues.put("healthGuard", healthGuard)
        contentValues.put("locale", locale)
        database!!.update(
            "api_data_ST",
            contentValues,
            "id = ?",
            arrayOf(id.toString())
        )
        for (bodylist in writeup!!) {
            bodyUpdate(profileId, bodylist.title, bodylist.body.toString(), bodylist.order)
        }
    }

    fun bodyUpdate(
        profileId: String?,
        title: String?,
        body: String?,
        order: Int?
    ) {
        val contentValues = ContentValues()
        contentValues.put("profileId", profileId)
        contentValues.put("title", title)
        contentValues.put("body", body)
        contentValues.put("body_order", order)
        database!!.update(
            "body_list",
            contentValues,
            "profileId = ?",
            arrayOf(profileId.toString())
        )
        database!!.insert("body_list", null, contentValues)
    }

    fun isTableExists(str: String?): Boolean {
        val sQLiteDatabase = database
        val sb = StringBuilder()
        sb.append("select DISTINCT tbl_name from sqlite_master where tbl_name = '")
        sb.append(str)
        sb.append("'")
        val rawQuery = sQLiteDatabase!!.rawQuery(sb.toString(), null)
        if (rawQuery != null) {
            if (rawQuery.count > 0) {
                rawQuery.close()
                return true
            }
            rawQuery.close()
        }
        return false
    }

    fun dropOldTable(str: String?): Boolean {
        val sQLiteDatabase = database
        val sb = StringBuilder()
        sb.append("DROP TABLE ")
        sb.append(str)
        val rawQuery = sQLiteDatabase!!.rawQuery(sb.toString(), null)
        if (rawQuery != null) {
            if (rawQuery.count > 0) {
                rawQuery.close()
                return true
            }
            rawQuery.close()
        }
        return false
    }

    fun bitMapToString(bArr: ByteArray?): String? {
        return if (bArr != null) {
            Base64.encodeToString(bArr, 0)
        } else null
    }

    fun truncateAll() {
        database!!.execSQL("DELETE FROM api_data_ST")
    }

    @SuppressLint("Range")
    fun getData(context: Context?, identifier: String): Data? {
        Log.e("getData", "str:" + identifier)
        val str2 = ""
        val stuffGetSet = Data()
        return try {
            val rawQuery = database!!.rawQuery(
                "SELECT * FROM api_data_ST where identifier = ?",
                arrayOf(identifier)
            )
            val sb = StringBuilder()
            sb.append(str2)
            sb.append(rawQuery.count)
            Log.e("cursor::01", sb.toString())
            val sb2 = StringBuilder()
            sb2.append(str2)
            sb2.append(rawQuery.count)
            Log.e("cursor::02", sb2.toString())
            if (rawQuery.moveToFirst()) {
                while (true) {
                    val sb3 = StringBuilder()
                    sb3.append(str2)
                    sb3.append(rawQuery.count)

//                    val data=Data()
//                    stuffGetSet = wit(
                    stuffGetSet.createdDate =
                        rawQuery.getString(rawQuery.getColumnIndex("createdDate"))
                    stuffGetSet.id = rawQuery.getString(rawQuery.getColumnIndex("data_id"))
                    stuffGetSet.profileId = rawQuery.getString(rawQuery.getColumnIndex("profileId"))
                    stuffGetSet.identifier =
                        rawQuery.getString(rawQuery.getColumnIndex("identifier"))
                    stuffGetSet.topline = rawQuery.getString(rawQuery.getColumnIndex("topline"))
                    stuffGetSet.rank = rawQuery.getString(rawQuery.getColumnIndex("rank"))
                    stuffGetSet.score = rawQuery.getInt(rawQuery.getColumnIndex("score"))
                    stuffGetSet.country = rawQuery.getString(rawQuery.getColumnIndex("country"))
                    stuffGetSet.language = rawQuery.getString(rawQuery.getColumnIndex("language"))
//                        val str = rawQuery.getString(rawQuery.getColumnIndex("writeup"))
//                    data.list.forEach { sb.append(it).append(",") }
//                    data.list = str.split(",")

//                        data.writeup = JSONObject(rawQuery.getString(rawQuery.getColumnIndex("writeup")))
                    stuffGetSet.criteria =
                        JSONObject(rawQuery.getString(rawQuery.getColumnIndex("criteria")))
                    stuffGetSet.active =
                        rawQuery.getString(rawQuery.getColumnIndex("active")).toBoolean()
                    stuffGetSet.healthGuard =
                        rawQuery.getString(rawQuery.getColumnIndex("healthGuard")).toBoolean()
                    stuffGetSet.locale = rawQuery.getString(rawQuery.getColumnIndex("locale"))

                    stuffGetSet.writeup = getDataBody(context, stuffGetSet.profileId!!)

//                    )
                    try {
                        if (!rawQuery.moveToNext()) {
                            break
                        }
                    } catch (unused: SQLiteException) {
                        Log.e("getData", "SQLiteException01:" + unused)
                        return stuffGetSet
                    }
                }
                //StuffGetSet2 = stuffGetSet;
            }
            rawQuery.close()
            stuffGetSet
        } catch (unused2: SQLiteException) {
            Log.e("getData", "SQLiteException02:" + unused2)
            stuffGetSet
        }
    }

    @SuppressLint("Range")
    fun getDataBody(context: Context?, profileId: String): List<BodyList>? {
        Log.e("getData", "str:" + profileId)
        val str2 = ""
        val writeup = ArrayList<BodyList>()
        return try {
            val rawQuery = database!!.rawQuery(
                "SELECT * FROM body_list where profileId = ?",
                arrayOf(profileId)
            )
            val sb = StringBuilder()
            sb.append(str2)
            sb.append(rawQuery.count)
            Log.e("cursor::01", sb.toString())
            val sb2 = StringBuilder()
            sb2.append(str2)
            sb2.append(rawQuery.count)
            Log.e("cursor::02", sb2.toString())
            if (rawQuery.moveToFirst()) {
                while (true) {
                    val sb3 = StringBuilder()
                    sb3.append(str2)
                    sb3.append(rawQuery.count)
                    val stuffGetSet = BodyList()
                    stuffGetSet.title = rawQuery.getString(rawQuery.getColumnIndex("title"))
                    stuffGetSet.body = listOf(rawQuery.getString(rawQuery.getColumnIndex("body")))
                    stuffGetSet.order = rawQuery.getInt(rawQuery.getColumnIndex("profileId"))
                    writeup.add(stuffGetSet)

                    try {
                        if (!rawQuery.moveToNext()) {
                            break
                        }
                    } catch (unused: SQLiteException) {
                        Log.e("getData", "SQLiteException01:" + unused)
                        return writeup
                    }
                }
                //StuffGetSet2 = stuffGetSet;
            }
            rawQuery.close()
            writeup
        } catch (unused2: SQLiteException) {
            Log.e("getData", "SQLiteException02:" + unused2)
            writeup
        }
    }


    @SuppressLint("Range")
    fun getAllData(context: Context?): ArrayList<Data> {
        val str = ""
        val arrayList = ArrayList<Data>()
        try {
            val str2 = "SELECT * FROM api_data_ST"
            var rawQuery = database!!.rawQuery(str2, null)
            val sb = StringBuilder()
            sb.append(str)
            sb.append(rawQuery.count)
            val count = rawQuery.count
            for (i in 0..count) {
                val sQLiteDatabase = database
                rawQuery = database!!.rawQuery(str2 + " LIMIT 1 OFFSET " + i, null)

                if (rawQuery.moveToFirst()) {
                    do {
                        val stuffGetSet = Data()
//                    stuffGetSet = wit(
                        stuffGetSet.createdDate =
                            rawQuery.getString(rawQuery.getColumnIndex("createdDate"))
                        stuffGetSet.id = rawQuery.getString(rawQuery.getColumnIndex("data_id"))
                        stuffGetSet.profileId =
                            rawQuery.getString(rawQuery.getColumnIndex("profileId"))
                        stuffGetSet.identifier =
                            rawQuery.getString(rawQuery.getColumnIndex("identifier"))
                        stuffGetSet.topline = rawQuery.getString(rawQuery.getColumnIndex("topline"))
                        stuffGetSet.rank = rawQuery.getString(rawQuery.getColumnIndex("rank"))
                        stuffGetSet.score = rawQuery.getInt(rawQuery.getColumnIndex("score"))
                        stuffGetSet.country = rawQuery.getString(rawQuery.getColumnIndex("country"))
                        stuffGetSet.language =
                            rawQuery.getString(rawQuery.getColumnIndex("language"))
//                        val str = rawQuery.getString(rawQuery.getColumnIndex("writeup"))
//                    data.list.forEach { sb.append(it).append(",") }
//                    data.list = str.split(",")

//                        data.writeup = JSONObject(rawQuery.getString(rawQuery.getColumnIndex("writeup")))
                        stuffGetSet.criteria =
                            JSONObject(rawQuery.getString(rawQuery.getColumnIndex("criteria")))
                        stuffGetSet.active =
                            rawQuery.getString(rawQuery.getColumnIndex("active")).toBoolean()
                        stuffGetSet.healthGuard =
                            rawQuery.getString(rawQuery.getColumnIndex("healthGuard")).toBoolean()
                        stuffGetSet.locale = rawQuery.getString(rawQuery.getColumnIndex("locale"))
                        arrayList.add(stuffGetSet)
                    } while (rawQuery.moveToNext())
                }
            }
            rawQuery.close()
            val sb5 = StringBuilder()
            sb5.append(str)
            sb5.append(arrayList.size)
        } catch (unused: SQLiteException) {
        }

        return arrayList
    }

    val recordCount: Int
        get() {
            val rawQuery = database!!.rawQuery("SELECT DISTINCT data_id FROM api_data_ST", null)
            rawQuery.moveToFirst()
            return rawQuery.count
        }

    fun stuffDelete(i: Int) {
        val deleteQuery = "DELETE FROM api_data_ST WHERE data_id='$i'"
        database!!.execSQL(deleteQuery)
    }

    companion object {
        private const val STUFF_ID = "id"
    }

    init {
        dbHandler = SQLiteHandler(context)
    }
}