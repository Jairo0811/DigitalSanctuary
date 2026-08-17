package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "annotations")
data class Annotation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val type: String,
    val content: String,
    val note: String = "",
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val locationInfo: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "''") val tags: String = "",
    @ColumnInfo(defaultValue = "0") val locationIndex: Int = 0
)
