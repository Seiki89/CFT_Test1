package com.example.testcft.room_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
    (tableName = "Items")
data class Item (
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,
    @ColumnInfo (name ="NumberCard")
    var num : String,
    @ColumnInfo (name ="BankName")
    var bank : String?,
)