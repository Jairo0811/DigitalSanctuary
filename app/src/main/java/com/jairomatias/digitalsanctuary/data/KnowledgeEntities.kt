package com.jairomatias.digitalsanctuary.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["bookId", "locationIndex"], unique = true)
    ]
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
    foreignKeys = [
        ForeignKey(
            entity = Annotation::class,
            parentColumns = ["id"],
            childColumns = ["fromAnnotationId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Annotation::class,
            parentColumns = ["id"],
            childColumns = ["toAnnotationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["fromAnnotationId"]), Index(value = ["toAnnotationId"])]
)
data class KnowledgeLink(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromAnnotationId: Int,
    val toAnnotationId: Int,
    val relation: String = "related",
    val createdAt: Long = System.currentTimeMillis()
)
