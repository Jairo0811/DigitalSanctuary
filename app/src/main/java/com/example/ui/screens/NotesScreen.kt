package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.Annotation
import com.example.data.Book
import com.example.ui.AiUiState
import com.example.ui.MainViewModel

@Composable
fun NotesScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val results by viewModel.knowledgeSearchResults.collectAsState()
    val query by viewModel.knowledgeQuery.collectAsState()
    val selectedBookId by viewModel.selectedBookId.collectAsState()
    val links by viewModel.knowledgeLinks.collectAsState()
    val allNotes by viewModel.allAnnotations.collectAsState()
    val aiState by viewModel.aiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var linkSourceId by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        Text("Knowledge Hub", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Text(
            "Search books and notes, connect ideas, preserve highlights and export your knowledge.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updateKnowledgeQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search knowledge") }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.NoteAdd, contentDescription = null)
                Text(" Note")
            }
            OutlinedButton(onClick = {
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/markdown"
                    putExtra(Intent.EXTRA_SUBJECT, "Digital Sanctuary Knowledge Export")
                    putExtra(Intent.EXTRA_TEXT, viewModel.exportKnowledgeAsMarkdown())
                }
                context.startActivity(Intent.createChooser(share, "Export knowledge"))
            }) {
                Icon(Icons.Default.IosShare, contentDescription = null)
                Text(" Export")
            }
            OutlinedButton(onClick = { showAiDialog = true }) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Text(" Ask AI")
            }
        }

        Text(
            "${results.books.size} books · ${results.annotations.size} notes · ${links.size} links",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (results.books.isNotEmpty()) {
                item { SectionTitle("Books") }
                items(results.books, key = { "book-${it.id}" }) { book -> BookKnowledgeCard(book, viewModel) }
            }
            if (results.annotations.isNotEmpty()) {
                item { SectionTitle("Notes & Highlights") }
                items(results.annotations, key = { "note-${it.id}" }) { annotation ->
                    KnowledgeNoteCard(
                        annotation = annotation,
                        isLinkSource = linkSourceId == annotation.id,
                        linkedCount = links.count { it.fromAnnotationId == annotation.id || it.toAnnotationId == annotation.id },
                        onDelete = { viewModel.deleteAnnotation(annotation.id) },
                        onLink = {
                            if (linkSourceId == 0) {
                                linkSourceId = annotation.id
                            } else {
                                viewModel.linkAnnotations(linkSourceId, annotation.id)
                                linkSourceId = 0
                            }
                        }
                    )
                }
            }
            if (results.books.isEmpty() && results.annotations.isEmpty()) {
                item {
                    Text(
                        "No knowledge matches this search.",
                        modifier = Modifier.padding(vertical = 36.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddKnowledgeDialog(
            books = results.books,
            initialBookId = selectedBookId ?: results.books.firstOrNull()?.id.orEmpty(),
            onDismiss = { showAddDialog = false },
            onSave = { bookId, type, content, note, tags ->
                viewModel.addKnowledgeNote(bookId, type, content, note, tags)
                showAddDialog = false
            }
        )
    }

    if (showAiDialog) {
        KnowledgeAiDialog(
            state = aiState,
            onRun = {
                val knowledgeContext = allNotes.joinToString("\n\n") { note ->
                    "${note.bookTitle} | ${note.type} | ${note.tags}\n${note.content}\n${note.note}"
                }
                viewModel.runAi(
                    "Synthesize this personal knowledge base. Identify recurring themes, contradictions, and useful connections.",
                    knowledgeContext
                )
            },
            onDismiss = {
                viewModel.clearAiState()
                showAiDialog = false
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun BookKnowledgeCard(book: Book, viewModel: MainViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { viewModel.selectBook(book.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            Text(book.author, style = MaterialTheme.typography.bodySmall)
            if (book.category.isNotBlank()) Text(book.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun KnowledgeNoteCard(
    annotation: Annotation,
    isLinkSource: Boolean,
    linkedCount: Int,
    onDelete: () -> Unit,
    onLink: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLinkSource) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(annotation.type, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(annotation.bookTitle.ifBlank { "Unassigned" }, style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily.Serif)
                }
                IconButton(onClick = onLink) {
                    Icon(if (isLinkSource) Icons.Default.AddLink else Icons.Default.Link, contentDescription = "Link note")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete note")
                }
            }
            if (annotation.locationInfo.isNotBlank()) {
                Text(annotation.locationInfo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(annotation.content, maxLines = 7, overflow = TextOverflow.Ellipsis)
            if (annotation.note.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(annotation.note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 10.dp)) {
                annotation.tags.split(',').filter { it.isNotBlank() }.take(4).forEach { tag ->
                    AssistChip(onClick = {}, label = { Text("#$tag") })
                }
                if (linkedCount > 0) AssistChip(onClick = {}, label = { Text("$linkedCount links") })
            }
        }
    }
}

@Composable
private fun AddKnowledgeDialog(
    books: List<Book>,
    initialBookId: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var selectedBookId by remember { mutableStateOf(initialBookId) }
    var type by remember { mutableStateOf("Insight") }
    var content by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New knowledge note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (books.isNotEmpty()) {
                    Text("Book", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        books.take(3).forEach { book ->
                            AssistChip(
                                onClick = { selectedBookId = book.id },
                                label = { Text(if (selectedBookId == book.id) "✓ ${book.title.take(15)}" else book.title.take(15)) }
                            )
                        }
                    }
                }
                OutlinedTextField(type, { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(content, { content = it }, label = { Text("Excerpt / idea") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(note, { note = it }, label = { Text("Reflection") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(tags, { tags = it }, label = { Text("Tags") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(selectedBookId, type, content, note, tags) }, enabled = selectedBookId.isNotBlank() && (content.isNotBlank() || note.isNotBlank())) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun KnowledgeAiDialog(state: AiUiState, onRun: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ask Digital Sanctuary") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Gemini can synthesize your saved notes and highlights. Your configured AI endpoint receives only the text needed for this request.")
                Button(onClick = onRun, enabled = state !is AiUiState.Loading) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Text(" Analyze knowledge base")
                }
                when (state) {
                    AiUiState.Idle -> Unit
                    AiUiState.Loading -> CircularProgressIndicator()
                    is AiUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                    is AiUiState.Success -> Text(state.text)
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
