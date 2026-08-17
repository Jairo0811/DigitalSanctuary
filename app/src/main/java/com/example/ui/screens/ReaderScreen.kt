package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Book
import com.example.data.DocumentFormat
import com.example.reader.LoadedDocument
import com.example.reader.ReaderEngine
import com.example.ui.AiUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.SepiaPaper

@Composable
fun ReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val books by viewModel.allBooks.collectAsState()
    val selectedBookId by viewModel.selectedBookId.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val bookmarks by viewModel.allBookmarks.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val book = books.firstOrNull { it.id == selectedBookId }

    var document by remember { mutableStateOf<LoadedDocument?>(null) }
    var loading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableIntStateOf(0) }
    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }

    LaunchedEffect(book?.id, book?.localUri) {
        location = book?.currentLocation ?: 0
        document = null
        pdfBitmap = null
        loadError = null
        if (book != null && book.localUri.isNotBlank()) {
            loading = true
            runCatching { ReaderEngine.load(context, book) }
                .onSuccess { loaded ->
                    document = loaded
                    val total = loaded?.locationCount ?: 0
                    if (total > 0) {
                        location = book.currentLocation.coerceIn(0, total - 1)
                        viewModel.updateReaderLocation(book.id, location, total)
                    }
                }
                .onFailure { loadError = it.message ?: "Unable to open this document" }
            loading = false
        }
    }

    val pdfDocument = document as? LoadedDocument.Pdf
    LaunchedEffect(pdfDocument?.uri, location) {
        if (pdfDocument != null) {
            pdfBitmap = ReaderEngine.renderPdfPage(context, pdfDocument.uri, location)
        }
    }

    val background = if (settings.bleachLevel == "Standard") SepiaPaper else MaterialTheme.colorScheme.background

    Box(modifier = modifier.fillMaxSize().background(background)) {
        when {
            book == null -> EmptyReaderState()
            loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            loadError != null -> ReaderError(loadError.orEmpty())
            else -> ReaderContent(
                book = book,
                document = document,
                location = location,
                pdfBitmap = pdfBitmap,
                textSize = settings.textSize,
                isBookmarked = bookmarks.any { it.bookId == book.id && it.locationIndex == location },
                onPrevious = {
                    val next = (location - 1).coerceAtLeast(0)
                    location = next
                    viewModel.updateReaderLocation(book.id, next, totalLocations(document))
                },
                onNext = {
                    val total = totalLocations(document)
                    val next = (location + 1).coerceAtMost((total - 1).coerceAtLeast(0))
                    location = next
                    viewModel.updateReaderLocation(book.id, next, total)
                },
                onBookmark = {
                    viewModel.toggleBookmark(book.id, location, locationLabel(book, document, location))
                },
                onAddNote = { showNoteDialog = true },
                onAi = { showAiDialog = true }
            )
        }
    }

    if (showNoteDialog && book != null) {
        AddReaderNoteDialog(
            book = book,
            location = location,
            locationInfo = locationLabel(book, document, location),
            suggestedContent = currentTextContext(book, document, location),
            onDismiss = { showNoteDialog = false },
            onSave = { type, content, note, tags ->
                viewModel.addKnowledgeNote(
                    bookId = book.id,
                    type = type,
                    content = content,
                    note = note,
                    tags = tags,
                    locationInfo = locationLabel(book, document, location),
                    locationIndex = location
                )
                showNoteDialog = false
            }
        )
    }

    if (showAiDialog && book != null) {
        AiReaderDialog(
            aiState = aiState,
            hasTextContext = currentTextContext(book, document, location).isNotBlank(),
            onAction = { instruction ->
                viewModel.runAi(instruction, currentTextContext(book, document, location))
            },
            onSave = {
                viewModel.saveAiResultAsInsight(book.id, locationLabel(book, document, location), location)
            },
            onDismiss = {
                viewModel.clearAiState()
                showAiDialog = false
            }
        )
    }
}

@Composable
private fun ReaderContent(
    book: Book,
    document: LoadedDocument?,
    location: Int,
    pdfBitmap: Bitmap?,
    textSize: Int,
    isBookmarked: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onBookmark: () -> Unit,
    onAddNote: () -> Unit,
    onAi: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(tonalElevation = 1.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Text(
                    text = locationLabel(book, document, location),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onBookmark) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Bookmark", tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onAddNote) {
                        Icon(Icons.Default.EditNote, contentDescription = "Add note")
                    }
                    IconButton(onClick = onAi) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Gemini assistance")
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (document) {
                is LoadedDocument.Epub -> {
                    val chapter = document.chapters.getOrNull(location)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Text(
                            chapter?.title ?: book.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = (textSize + 6).sp,
                            lineHeight = (textSize + 12).sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            chapter?.content.orEmpty(),
                            fontFamily = FontFamily.Serif,
                            fontSize = textSize.sp,
                            lineHeight = (textSize + 10).sp
                        )
                    }
                }
                is LoadedDocument.Pdf -> {
                    if (pdfBitmap == null) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        Image(
                            bitmap = pdfBitmap.asImageBitmap(),
                            contentDescription = "PDF page ${location + 1}",
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                null -> DemoReaderContent(book, textSize)
            }
        }

        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onPrevious, enabled = location > 0) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                Text("Previous")
            }
            Text("${location + 1} / ${totalLocations(document)}", style = MaterialTheme.typography.labelLarge)
            Button(onClick = onNext, enabled = location < totalLocations(document) - 1) {
                Text("Next")
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Composable
private fun DemoReaderContent(book: Book, textSize: Int) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Text(book.title, fontFamily = FontFamily.Serif, fontSize = (textSize + 8).sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(book.author, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(24.dp))
        Text(
            book.description.ifBlank { "Import an EPUB or PDF from the Library to read real document content in Digital Sanctuary." },
            fontFamily = FontFamily.Serif,
            fontSize = textSize.sp,
            lineHeight = (textSize + 10).sp
        )
        if (book.quote.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 2.dp) {
                Text("“${book.quote}”", modifier = Modifier.padding(20.dp), fontFamily = FontFamily.Serif)
            }
        }
    }
}

@Composable
private fun EmptyReaderState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Choose a book from Library to start reading.")
    }
}

@Composable
private fun ReaderError(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(24.dp))
    }
}

@Composable
private fun AddReaderNoteDialog(
    book: Book,
    location: Int,
    locationInfo: String,
    suggestedContent: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var type by remember { mutableStateOf("Insight") }
    var content by remember { mutableStateOf(suggestedContent.take(600)) }
    var note by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add knowledge note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${book.title} · $locationInfo", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(type, { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(content, { content = it }, label = { Text("Excerpt / idea") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(note, { note = it }, label = { Text("Your note") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(tags, { tags = it }, label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onSave(type, content, note, tags) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AiReaderDialog(
    aiState: AiUiState,
    hasTextContext: Boolean,
    onAction: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gemini Reading Assistant") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!hasTextContext) {
                    Text("This page has no extractable text context. EPUB chapters and library notes can be sent to Gemini; PDF rendering remains local-only.")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onAction("Summarize this reading passage in concise bullet points.") }, enabled = hasTextContext) { Text("Summarize") }
                    OutlinedButton(onClick = { onAction("Explain the difficult concepts in this passage clearly.") }, enabled = hasTextContext) { Text("Explain") }
                }
                OutlinedButton(onClick = { onAction("Extract the most useful insights and connections from this passage.") }, enabled = hasTextContext) { Text("Insights") }
                when (aiState) {
                    AiUiState.Idle -> Text("Choose an action.", style = MaterialTheme.typography.bodySmall)
                    AiUiState.Loading -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(20.dp))
                        Text("  Thinking…")
                    }
                    is AiUiState.Error -> Text(aiState.message, color = MaterialTheme.colorScheme.error)
                    is AiUiState.Success -> Text(aiState.text, modifier = Modifier.verticalScroll(rememberScrollState()).height(220.dp))
                }
            }
        },
        confirmButton = {
            if (aiState is AiUiState.Success) Button(onClick = onSave) { Text("Save insight") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

private fun totalLocations(document: LoadedDocument?): Int = (document?.locationCount ?: 1).coerceAtLeast(1)

private fun locationLabel(book: Book, document: LoadedDocument?, location: Int): String = when (document) {
    is LoadedDocument.Epub -> document.chapters.getOrNull(location)?.title ?: "Chapter ${location + 1}"
    is LoadedDocument.Pdf -> "Page ${location + 1} of ${document.locationCount}"
    null -> if (book.documentFormat == DocumentFormat.NONE) "Library preview" else "Location ${location + 1}"
}

private fun currentTextContext(book: Book, document: LoadedDocument?, location: Int): String = when (document) {
    is LoadedDocument.Epub -> document.chapters.getOrNull(location)?.let { "${book.title}\n${it.title}\n\n${it.content}" }.orEmpty()
    is LoadedDocument.Pdf -> ""
    null -> listOf(book.title, book.author, book.description, book.quote).filter { it.isNotBlank() }.joinToString("\n\n")
}
