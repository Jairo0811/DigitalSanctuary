package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Annotation
import com.example.ui.MainViewModel
import com.example.ui.components.BookCover
import com.example.ui.theme.AnnotationBlue
import com.example.ui.theme.AnnotationYellow
import com.example.ui.theme.SepiaPaper

@Composable
fun NotesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val annotations by viewModel.allAnnotations.collectAsState()
    val books by viewModel.allBooks.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    // Filter annotations based on search queries and category buttons
    val filteredAnnotations = annotations.filter { ann ->
        val matchesSearch = searchQuery.isBlank() ||
                ann.content.contains(searchQuery, ignoreCase = true) ||
                ann.note.contains(searchQuery, ignoreCase = true) ||
                ann.bookTitle.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == null || ann.type.equals(selectedCategoryFilter, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & New Note Action Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // High-fidelity search bar matching HTML
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search annotations, tags, or books...",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes_search_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SepiaPaper.copy(alpha = 0.3f),
                    unfocusedContainerColor = SepiaPaper.copy(alpha = 0.3f),
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF0F0F0F),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Category Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add Note Action Button
                Button(
                    onClick = { showAddNoteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F0F0F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_new_note_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Icon",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "New Note",
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                    }
                }

                // Clean Filter badges
                listOf("Thesis", "Insight", "Source").forEach { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    val badgeColor = when (cat) {
                        "Thesis" -> AnnotationBlue
                        "Insight" -> AnnotationYellow
                        else -> SepiaPaper
                    }

                    Surface(
                        onClick = {
                            selectedCategoryFilter = if (isSelected) null else cat
                        },
                        color = if (isSelected) Color(0xFF0F0F0F) else badgeColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, if (isSelected) Color.Black else Color.Transparent),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("filter_chip_$cat")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(badgeColor, CircleShape)
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF0F0F0F)
                                )
                            }
                        }
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        // Empty Search state
        if (filteredAnnotations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = "No Notes",
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No annotations found",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Try searching for a different term or write a new note above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            // PKM Bento Staggered Grid (supports responsive column wrapping beautifully)
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp
            ) {
                items(filteredAnnotations) { ann ->
                    BentoNoteCard(
                        annotation = ann,
                        onDeleteClick = { viewModel.deleteAnnotation(ann.id) }
                    )
                }
            }
        }
    }

    // Dynamic dialogue overlay to construct new Annotations
    if (showAddNoteDialog) {
        var noteType by remember { mutableStateOf("Thesis") }
        var noteContent by remember { mutableStateOf("") }
        var userNoteText by remember { mutableStateOf("") }
        var selectedBook by remember { mutableStateOf(books.firstOrNull() ?: books[0]) }
        var locationInfo by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = {
                Text(
                    text = "Create annotation",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Category filter Row
                    Column {
                        Text(
                            text = "CATEGORY TYPE",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF76777B),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Thesis", "Insight", "Source").forEach { level ->
                                val isSelected = noteType == level
                                Button(
                                    onClick = { noteType = level },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    Text(text = level, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Book list dropdown placeholder/simplistic selection
                    Column {
                        Text(
                            text = "ASSIGN TO BOOK",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF76777B),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().clickable {
                                // Rotate chosen book
                                val idx = books.indexOf(selectedBook)
                                val nextIdx = (idx + 1) % books.size
                                selectedBook = books[nextIdx]
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedBook.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Rotate Book choice",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Location Info
                    OutlinedTextField(
                        value = locationInfo,
                        onValueChange = { locationInfo = it },
                        label = { Text("Location (e.g. p. 48 or Chap 2)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Citation Content
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Quoted reference text") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    // Personal Thought notes
                    OutlinedTextField(
                        value = userNoteText,
                        onValueChange = { userNoteText = it },
                        label = { Text("Personal interpretation note (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteContent.isNotBlank() || userNoteText.isNotBlank()) {
                            viewModel.addAnnotation(
                                Annotation(
                                    bookId = selectedBook.id,
                                    type = noteType,
                                    content = noteContent.ifBlank { "Personal note" },
                                    note = userNoteText,
                                    bookTitle = selectedBook.title,
                                    bookAuthor = selectedBook.author,
                                    locationInfo = locationInfo.ifBlank { "Unplaced" }
                                )
                            )
                            showAddNoteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F0F0F),
                        contentColor = Color.White
                    )
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddNoteDialog = false }
                ) {
                    Text("Cancel", color = Color(0xFF5F5E59))
                }
            }
        )
    }
}

// Bento card item representation mapping the unique styling categories of the original layout
@Composable
fun BentoNoteCard(
    annotation: Annotation,
    onDeleteClick: () -> Unit
) {
    val levelColor = when (annotation.type) {
        "Thesis" -> AnnotationBlue
        "Insight" -> AnnotationYellow
        else -> SepiaPaper
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_card_${annotation.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Category color strip matching original HTML Bento grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(levelColor)
                    .border(width = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Headline Tag + Delete Icon row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = annotation.type.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete citation note",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // If type is Source, let's render a custom styled physical book slot as shown in Card 3 of HTML
                if (annotation.type.equals("Source", ignoreCase = true)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BookCover(
                            title = annotation.bookTitle,
                            author = annotation.bookAuthor,
                            coverUrl = "", // Blank triggers beautiful tactile title canvas fallback
                            modifier = Modifier
                                .width(40.dp)
                                .height(56.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = annotation.bookTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F0F0F),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = annotation.bookAuthor,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Check Icon",
                                    tint = Color(0xFF5F5E59),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Imported via Calibre",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Regular quoted citation or study thought
                    Text(
                        text = "\"${annotation.content}\"",
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF0F0F0F),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 5
                    )

                    // Displays written personal thoughts if any
                    if (annotation.note.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = annotation.note,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Divider line
                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Meta footer displaying book references
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Reference book",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = annotation.bookTitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (annotation.bookAuthor.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                color = Color.Transparent
                            ) {
                                Text(
                                    text = annotation.bookAuthor,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (annotation.locationInfo.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                color = Color.Transparent
                            ) {
                                Text(
                                    text = annotation.locationInfo,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
