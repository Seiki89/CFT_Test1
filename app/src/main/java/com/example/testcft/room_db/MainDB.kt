package com.example.testcft.room_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1)
abstract class Maindb : RoomDatabase() {
    abstract fun getDao(): DAO

    companion object{
        fun historydb(context: Context): Maindb{
            return Room.databaseBuilder(
                context.applicationContext,
                Maindb::class.java,
                "DataBaseFile.db").build()
        }
    }
}