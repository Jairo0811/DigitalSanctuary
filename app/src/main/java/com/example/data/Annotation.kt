package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "annotations")
data class Annotation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val type: String, // "Thesis", "Insight", "Source"
    val content: String,
    val note: String = "",
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val locationInfo: String = "", // e.g. "p. 8" or "Chapter 3"
    val timestamp: Long = System.currentTimeMillis()
)
