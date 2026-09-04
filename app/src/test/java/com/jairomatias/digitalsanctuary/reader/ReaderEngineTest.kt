package com.jairomatias.digitalsanctuary.reader

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.jairomatias.digitalsanctuary.data.Book
import com.jairomatias.digitalsanctuary.data.DocumentFormat
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReaderEngineTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `valid epub loads readable chapter and metadata`() = runBlocking {
        val file = createEpub(
            "sample.epub",
            "OPS/chapter1.xhtml" to """
                <html><body>
                  <h1>Opening Chapter</h1>
                  <p>This chapter contains enough readable text to be accepted by the Digital Sanctuary reader engine.</p>
                </body></html>
            """.trimIndent()
        )
        val uri = Uri.fromFile(file)
        val metadata = ReaderEngine.inspect(context, uri)
        val book = sampleBook(uri)

        val loaded = ReaderEngine.load(context, book) as LoadedDocument.Epub

        assertEquals(DocumentFormat.EPUB, metadata.format)
        assertEquals("sample", metadata.title)
        assertEquals(1, metadata.locationCount)
        assertEquals(1, loaded.locationCount)
        assertEquals("Opening Chapter", loaded.chapters.first().title)
        assertTrue(loaded.chapters.first().content.contains("Digital Sanctuary reader engine"))
    }

    @Test
    fun `malformed epub degrades to fallback chapter instead of throwing`() = runBlocking {
        val file = File(context.cacheDir, "broken.epub").apply { writeText("this is not a zip archive") }
        val loaded = ReaderEngine.load(context, sampleBook(Uri.fromFile(file)))

        assertNotNull(loaded)
        val epub = loaded as LoadedDocument.Epub
        assertEquals(1, epub.locationCount)
        assertEquals("EPUB", epub.chapters.first().title)
        assertTrue(epub.chapters.first().content.contains("No readable"))
    }

    @Test
    fun `oversized epub html entry is skipped safely`() = runBlocking {
        val oversized = buildString {
            append("<html><body><h1>Huge</h1><p>")
            repeat(2 * 1024 * 1024 + 128) { append('a') }
            append("</p></body></html>")
        }
        val file = createEpub("oversized.epub", "OPS/huge.xhtml" to oversized)

        val loaded = ReaderEngine.load(context, sampleBook(Uri.fromFile(file))) as LoadedDocument.Epub

        assertEquals(1, loaded.locationCount)
        assertEquals("EPUB", loaded.chapters.first().title)
    }

    private fun sampleBook(uri: Uri) = Book(
        id = "reader-test",
        title = "Reader Test",
        author = "Test Author",
        category = "Testing",
        progress = 0f,
        localUri = uri.toString(),
        documentFormat = DocumentFormat.EPUB
    )

    private fun createEpub(name: String, vararg entries: Pair<String, String>): File {
        val file = File(context.cacheDir, name)
        ZipOutputStream(FileOutputStream(file)).use { zip ->
            entries.forEach { (path, content) ->
                zip.putNextEntry(ZipEntry(path))
                zip.write(content.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        }
        return file
    }
}
