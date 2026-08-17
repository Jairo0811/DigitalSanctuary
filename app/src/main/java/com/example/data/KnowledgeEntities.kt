package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    indices = [Index(value = ["bookId", "locationIndex"], unique = true)]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val locationIndex: Int,
    val label: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "knowledge_links",
    indices = [Index(value = ["fromAnnotationId"]), Index(value = ["toAnnotationId"])]
)
data class KnowledgeLink(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromAnnotationId: Int,
    val toAnnotationId: Int,
    val relation: String = "related",
    val createdAt: Long = System.currentTimeMillis()
)
