package com.example

import com.example.data.Bookmark
import com.example.data.DocumentFormat
import com.example.data.KnowledgeLink
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class KnowledgeModelTest {
    @Test
    fun `document formats remain stable for persisted data`() {
        assertEquals("EPUB", DocumentFormat.EPUB)
        assertEquals("PDF", DocumentFormat.PDF)
        assertEquals("NONE", DocumentFormat.NONE)
    }

    @Test
    fun `bookmark identifies one reading location`() {
        val bookmark = Bookmark(bookId = "book-1", locationIndex = 4, label = "Chapter 5")
        assertEquals("book-1", bookmark.bookId)
        assertEquals(4, bookmark.locationIndex)
    }

    @Test
    fun `knowledge link keeps source and target distinct`() {
        val link = KnowledgeLink(fromAnnotationId = 1, toAnnotationId = 2, relation = "supports")
        assertNotEquals(link.fromAnnotationId, link.toAnnotationId)
        assertEquals("supports", link.relation)
    }
}
