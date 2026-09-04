package com.jairomatias.digitalsanctuary.ui.library

import com.jairomatias.digitalsanctuary.data.Book
import com.jairomatias.digitalsanctuary.data.ReadingStatus

enum class LibraryStatusFilter {
    ALL,
    TO_READ,
    READING,
    COMPLETED,
    PAUSED,
    ABANDONED
}

enum class LibrarySort {
    RECENT,
    TITLE,
    AUTHOR,
    PROGRESS
}

data class LibraryFilterState(
    val query: String = "",
    val status: LibraryStatusFilter = LibraryStatusFilter.ALL,
    val category: String? = null,
    val favoritesOnly: Boolean = false,
    val sort: LibrarySort = LibrarySort.RECENT
)

fun filterAndSortBooks(
    books: List<Book>,
    state: LibraryFilterState
): List<Book> {
    val normalizedQuery = state.query.trim()

    return books
        .asSequence()
        .filter { book ->
            normalizedQuery.isBlank() ||
                book.title.contains(normalizedQuery, ignoreCase = true) ||
                book.author.contains(normalizedQuery, ignoreCase = true) ||
                book.category.contains(normalizedQuery, ignoreCase = true) ||
                book.isbn.contains(normalizedQuery, ignoreCase = true) ||
                book.publisher.contains(normalizedQuery, ignoreCase = true)
        }
        .filter { book ->
            state.status == LibraryStatusFilter.ALL ||
                book.readingStatus == state.status.name
        }
        .filter { book ->
            state.category.isNullOrBlank() ||
                book.category.equals(state.category, ignoreCase = true)
        }
        .filter { book -> !state.favoritesOnly || book.isFavorite }
        .let { sequence ->
            when (state.sort) {
                LibrarySort.RECENT -> sequence.sortedByDescending { it.lastReadTimestamp }
                LibrarySort.TITLE -> sequence.sortedBy { it.title.lowercase() }
                LibrarySort.AUTHOR -> sequence.sortedBy { it.author.lowercase() }
                LibrarySort.PROGRESS -> sequence.sortedByDescending { it.progress }
            }
        }
        .toList()
}

fun normalizedReadingStatus(status: String, progress: Float): String = when {
    progress >= 0.99f -> ReadingStatus.COMPLETED
    status == ReadingStatus.COMPLETED && progress < 0.99f -> ReadingStatus.READING
    else -> status
}
