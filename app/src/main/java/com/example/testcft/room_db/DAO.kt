package com.example.testcft.room_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {
    @Insert
    fun inserItem(item: Item)
    @Query("SELECT * FROM Items")
    fun getAllItems(): List<Item>
}