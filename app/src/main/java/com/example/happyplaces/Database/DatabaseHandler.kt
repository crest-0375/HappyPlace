package com.example.happyplaces.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.happyplaces.Models.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlaceDataBase"
        private const val TABLE_HAPPY_PLACE = "HappyPlaceTable"

        // column name
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
//        db.close()
        return result
    }

    @SuppressLint("Range", "Recycle")
    fun getHappyPlacesList(): ArrayList<HappyPlaceModel> {
        val db = this.writableDatabase
        val SELECT_QUERY = "SELECT * FROM $TABLE_HAPPY_PLACE"
        val happyPlaceList = ArrayList<HappyPlaceModel>()

        try {
            val cursor: Cursor = db.rawQuery(SELECT_QUERY, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )
                    happyPlaceList.add(place)

                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteAbortException) {
            db.execSQL(SELECT_QUERY)
            return ArrayList()
        }
        return happyPlaceList
    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)
        Log.d("TAG", "id = ${happyPlace.id}")
        val result = db.update(
            TABLE_HAPPY_PLACE,
            contentValues,
            "$KEY_ID=?",
            arrayOf(happyPlace.id.toString())
        )

//        db.close()
        return result
    }

    fun deleteHappyPlace(id: Int) : Int{
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_HAPPY_PLACE,
            "$KEY_ID=?",
            arrayOf(id.toString())
        )
//        db.close()
        return result
    }

}