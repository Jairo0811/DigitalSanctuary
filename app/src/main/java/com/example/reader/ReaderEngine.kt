package com.example.reader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Html
import com.example.data.Book
import com.example.data.DocumentFormat
import java.io.BufferedInputStream
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface LoadedDocument {
    val locationCount: Int

    data class Epub(
        val chapters: List<ReaderChapter>
    ) : LoadedDocument {
        override val locationCount: Int = chapters.size
    }

    data class Pdf(
        val uri: Uri,
        override val locationCount: Int
    ) : LoadedDocument
}

data class ReaderChapter(
    val title: String,
    val content: String
)

data class ImportedDocumentMetadata(
    val title: String,
    val format: String,
    val locationCount: Int
)

object ReaderEngine {
    suspend fun inspect(context: Context, uri: Uri): ImportedDocumentMetadata = withContext(Dispatchers.IO) {
        val displayName = queryDisplayName(context, uri) ?: "Imported document"
        val title = displayName.substringBeforeLast('.').ifBlank { "Imported document" }
        val format = detectFormat(context, uri, displayName)
        val locations = when (format) {
            DocumentFormat.PDF -> pdfPageCount(context, uri)
            DocumentFormat.EPUB -> parseEpub(context, uri).size
            else -> 0
        }
        ImportedDocumentMetadata(title, format, locations)
    }

    suspend fun load(context: Context, book: Book): LoadedDocument? = withContext(Dispatchers.IO) {
        if (book.localUri.isBlank()) return@withContext null
        val uri = Uri.parse(book.localUri)
        when (book.documentFormat) {
            DocumentFormat.EPUB -> LoadedDocument.Epub(parseEpub(context, uri))
            DocumentFormat.PDF -> LoadedDocument.Pdf(uri, pdfPageCount(context, uri))
            else -> null
        }
    }

    suspend fun renderPdfPage(
        context: Context,
        uri: Uri,
        pageIndex: Int,
        targetWidth: Int = 1400
    ): Bitmap? = withContext(Dispatchers.IO) {
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext null
        descriptor.use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                if (renderer.pageCount == 0) return@withContext null
                val safeIndex = pageIndex.coerceIn(0, renderer.pageCount - 1)
                renderer.openPage(safeIndex).use { page ->
                    val ratio = page.height.toFloat() / page.width.toFloat().coerceAtLeast(1f)
                    val width = targetWidth.coerceAtLeast(600)
                    val height = (width * ratio).toInt().coerceAtLeast(800)
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
                        bitmap.eraseColor(android.graphics.Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    }
                }
            }
        }
    }

    private fun queryDisplayName(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }

    private fun detectFormat(context: Context, uri: Uri, displayName: String): String {
        val mime = context.contentResolver.getType(uri).orEmpty().lowercase()
        val extension = displayName.substringAfterLast('.', "").lowercase()
        return when {
            mime == "application/pdf" || extension == "pdf" -> DocumentFormat.PDF
            mime.contains("epub") || extension == "epub" -> DocumentFormat.EPUB
            else -> DocumentFormat.NONE
        }
    }

    private fun pdfPageCount(context: Context, uri: Uri): Int {
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return 0
        descriptor.use { pfd ->
            return PdfRenderer(pfd).use { it.pageCount }
        }
    }

    private fun parseEpub(context: Context, uri: Uri): List<ReaderChapter> {
        val stream = context.contentResolver.openInputStream(uri) ?: return emptyList()
        val chapters = mutableListOf<ReaderChapter>()
        stream.use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && isHtmlEntry(entry.name)) {
                        val html = zip.readBytes().toString(Charsets.UTF_8)
                        val text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
                            .toString()
                            .replace(Regex("\\n{3,}"), "\n\n")
                            .trim()
                        if (text.length >= 40) {
                            chapters += ReaderChapter(
                                title = chapterTitle(html, entry.name, chapters.size + 1),
                                content = text
                            )
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }
        return chapters.ifEmpty {
            listOf(ReaderChapter("EPUB", "No readable XHTML/HTML chapters were found in this EPUB."))
        }
    }

    private fun isHtmlEntry(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".xhtml") || lower.endsWith(".html") || lower.endsWith(".htm")
    }

    private fun chapterTitle(html: String, path: String, index: Int): String {
        val heading = Regex("<(h1|h2)[^>]*>(.*?)</\\1>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(html)
            ?.groupValues
            ?.getOrNull(2)
            ?.let { Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString().trim() }
            .orEmpty()
        if (heading.isNotBlank()) return heading.take(120)
        val filename = path.substringAfterLast('/').substringBeforeLast('.').replace('_', ' ').replace('-', ' ').trim()
        return filename.ifBlank { "Chapter $index" }
    }
}
