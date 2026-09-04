package com.jairomatias.digitalsanctuary.ui.library

import com.jairomatias.digitalsanctuary.data.Book
import com.jairomatias.digitalsanctuary.data.ReadingStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class LibraryQueryTest {

    private val books = listOf(
        Book(
            id = "1",
            title = "Meditations",
            author = "Marcus Aurelius",
            category = "Philosophy",
            progress = 0.7f,
            readingStatus = ReadingStatus.READING,
            publisher = "Penguin",
            isbn = "111",
            lastReadTimestamp = 300L
        ),
        Book(
            id = "2",
            title = "Deep Work",
            author = "Cal Newport",
            category = "Productivity",
            progress = 1f,
            isFinished = true,
            readingStatus = ReadingStatus.COMPLETED,
            isFavorite = true,
            publisher = "Grand Central",
            isbn = "222",
            lastReadTimestamp = 200L
        ),
        Book(
            id = "3",
            title = "Sapiens",
            author = "Yuval Noah Harari",
            category = "History",
            progress = 0f,
            readingStatus = ReadingStatus.TO_READ,
            publisher = "Harper",
            isbn = "333",
            lastReadTimestamp = 100L
        )
    )

    @Test
    fun search_matches_title_author_category_isbn_and_publisher() {
        assertEquals(listOf("1"), filterAndSortBooks(books, LibraryFilterState(query = "Marcus")).map { it.id })
        assertEquals(listOf("2"), filterAndSortBooks(books, LibraryFilterState(query = "Productivity")).map { it.id })
        assertEquals(listOf("3"), filterAndSortBooks(books, LibraryFilterState(query = "333")).map { it.id })
        assertEquals(listOf("1"), filterAndSortBooks(books, LibraryFilterState(query = "Penguin")).map { it.id })
    }

    @Test
    fun status_and_favorite_filters_can_be_combined() {
        val result = filterAndSortBooks(
            books,
            LibraryFilterState(
                status = LibraryStatusFilter.COMPLETED,
                favoritesOnly = true
            )
        )

        assertEquals(listOf("2"), result.map { it.id })
    }

    @Test
    fun category_filter_is_case_insensitive() {
        val result = filterAndSortBooks(
            books,
            LibraryFilterState(category = "philosophy")
        )

        assertEquals(listOf("1"), result.map { it.id })
    }

    @Test
    fun sorting_supports_title_author_progress_and_recent() {
        assertEquals(
            listOf("1", "2", "3"),
            filterAndSortBooks(books, LibraryFilterState(sort = LibrarySort.RECENT)).map { it.id }
        )
        assertEquals(
            listOf("2", "1", "3"),
            filterAndSortBooks(books, LibraryFilterState(sort = LibrarySort.TITLE)).map { it.id }
        )
        assertEquals(
            listOf("2", "1", "3"),
            filterAndSortBooks(books, LibraryFilterState(sort = LibrarySort.AUTHOR)).map { it.id }
        )
        assertEquals(
            listOf("2", "1", "3"),
            filterAndSortBooks(books, LibraryFilterState(sort = LibrarySort.PROGRESS)).map { it.id }
        )
    }

    @Test
    fun completed_progress_normalizes_reading_status() {
        assertEquals(ReadingStatus.COMPLETED, normalizedReadingStatus(ReadingStatus.READING, 1f))
        assertEquals(ReadingStatus.READING, normalizedReadingStatus(ReadingStatus.COMPLETED, 0.5f))
        assertEquals(ReadingStatus.PAUSED, normalizedReadingStatus(ReadingStatus.PAUSED, 0.5f))
    }
}
