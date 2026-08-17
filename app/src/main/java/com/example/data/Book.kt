package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String,
    val progress: Float, // e.g., 0.68f for 68%
    val quote: String = "",
    val isFinished: Boolean = false,
    val coverUrl: String = "",
    val description: String = "",
    val lastReadTimestamp: Long = System.currentTimeMillis()
)
