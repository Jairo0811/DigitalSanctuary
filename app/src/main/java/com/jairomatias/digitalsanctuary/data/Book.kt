package com.jairomatias.digitalsanctuary.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object ReadingStatus {
    const val TO_READ = "TO_READ"
    const val READING = "READING"
    const val COMPLETED = "COMPLETED"
    const val PAUSED = "PAUSED"
    const val ABANDONED = "ABANDONED"

    val values = listOf(TO_READ, READING, COMPLETED, PAUSED, ABANDONED)
}

object DocumentFormat {
    const val NONE = "NONE"
    const val EPUB = "EPUB"
    const val PDF = "PDF"
}

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String,
    val progress: Float,
    val quote: String = "",
    val isFinished: Boolean = false,
    val coverUrl: String = "",
    val description: String = "",
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "''") val isbn: String = "",
    @ColumnInfo(defaultValue = "''") val publisher: String = "",
    @ColumnInfo(defaultValue = "0") val pageCount: Int = 0,
    @ColumnInfo(defaultValue = "'TO_READ'") val readingStatus: String = ReadingStatus.TO_READ,
    @ColumnInfo(defaultValue = "0") val rating: Int = 0,
    @ColumnInfo(defaultValue = "0") val isFavorite: Boolean = false,
    @ColumnInfo(defaultValue = "0") val dateAdded: Long = 0L,
    @ColumnInfo(defaultValue = "0") val startedAt: Long = 0L,
    @ColumnInfo(defaultValue = "0") val finishedAt: Long = 0L,
    @ColumnInfo(defaultValue = "''") val localUri: String = "",
    @ColumnInfo(defaultValue = "'NONE'") val documentFormat: String = DocumentFormat.NONE,
    @ColumnInfo(defaultValue = "0") val currentLocation: Int = 0,
    @ColumnInfo(defaultValue = "0") val totalLocations: Int = 0
)
