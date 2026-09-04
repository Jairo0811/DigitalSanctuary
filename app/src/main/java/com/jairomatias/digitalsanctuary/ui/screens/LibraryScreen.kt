package com.jairomatias.digitalsanctuary.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jairomatias.digitalsanctuary.data.Book
import com.jairomatias.digitalsanctuary.data.ReadingStatus
import com.jairomatias.digitalsanctuary.ui.MainViewModel
import com.jairomatias.digitalsanctuary.ui.components.BookCover
import com.jairomatias.digitalsanctuary.ui.library.LibrarySort
import com.jairomatias.digitalsanctuary.ui.library.LibraryStatusFilter

@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onNavigateToReader: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allBooks by viewModel.allBooks.collectAsState()
    val books by viewModel.libraryBooks.collectAsState()
    val filterState by viewModel.libraryFilterState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingBook by remember { mutableStateOf<Book?>(null) }
    var deletingBook by remember { mutableStateOf<Book?>(null) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val categories = allBooks.map { it.category.trim() }.filter { it.isNotBlank() }.distinct().sorted()
    val continueReadingBook = allBooks
        .filter { it.readingStatus == ReadingStatus.READING }
        .maxByOrNull { it.lastReadTimestamp }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LibraryToolbar(
            query = filterState.query,
            onQueryChange = viewModel::updateLibraryQuery,
            status = filterState.status,
            onStatusChange = viewModel::updateLibraryStatus,
            favoritesOnly = filterState.favoritesOnly,
            onToggleFavorites = viewModel::toggleFavoritesFilter,
            category = filterState.category,
            categories = categories,
            categoryMenuExpanded = categoryMenuExpanded,
            onCategoryMenuExpandedChange = { categoryMenuExpanded = it },
            onCategoryChange = viewModel::updateLibraryCategory,
            sort = filterState.sort,
            sortMenuExpanded = sortMenuExpanded,
            onSortMenuExpandedChange = { sortMenuExpanded = it },
            onSortChange = viewModel::updateLibrarySort,
            onClear = viewModel::clearLibraryFilters
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (continueReadingBook != null && filterState.query.isBlank() && filterState.status == LibraryStatusFilter.ALL) {
                item(span = { GridItemSpan(2) }) {
                    ContinueReadingCard(
                        book = continueReadingBook,
                        onResume = {
                            viewModel.selectBook(continueReadingBook.id)
                            onNavigateToReader()
                        }
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR LIBRARY",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${books.size} book${if (books.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (books.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    EmptyLibraryState(
                        hasBooks = allBooks.isNotEmpty(),
                        onClearFilters = viewModel::clearLibraryFilters,
                        onAddBook = { showAddDialog = true }
                    )
                }
            } else {
                items(books, key = { it.id }) { book ->
                    LibraryBookCard(
                        book = book,
                        onOpen = {
                            viewModel.selectBook(book.id)
                            onNavigateToReader()
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(book.id) },
                        onEdit = { editingBook = book },
                        onDelete = { deletingBook = book }
                    )
                }
            }

            item {
                AddBookCard(onClick = { showAddDialog = true })
            }
        }
    }

    if (showAddDialog) {
        BookEditorDialog(
            book = null,
            onDismiss = { showAddDialog = false },
            onSave = { form ->
                viewModel.addNewBook(
                    title = form.title,
                    author = form.author,
                    category = form.category,
                    description = form.description,
                    isbn = form.isbn,
                    publisher = form.publisher,
                    pageCount = form.pageCount,
                    readingStatus = form.readingStatus,
                    coverUrl = form.coverUrl
                )
                showAddDialog = false
            }
        )
    }

    editingBook?.let { book ->
        BookEditorDialog(
            book = book,
            onDismiss = { editingBook = null },
            onSave = { form ->
                viewModel.updateBookDetails(
                    book = book,
                    title = form.title,
                    author = form.author,
                    category = form.category,
                    description = form.description,
                    isbn = form.isbn,
                    publisher = form.publisher,
                    pageCount = form.pageCount,
                    readingStatus = form.readingStatus,
                    coverUrl = form.coverUrl
                )
                editingBook = null
            }
        )
    }

    deletingBook?.let { book ->
        AlertDialog(
            onDismissRequest = { deletingBook = null },
            title = { Text("Delete book?") },
            text = { Text("\"${book.title}\" will be removed from your local library. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBook(book.id)
                        deletingBook = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingBook = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LibraryToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    status: LibraryStatusFilter,
    onStatusChange: (LibraryStatusFilter) -> Unit,
    favoritesOnly: Boolean,
    onToggleFavorites: () -> Unit,
    category: String?,
    categories: List<String>,
    categoryMenuExpanded: Boolean,
    onCategoryMenuExpandedChange: (Boolean) -> Unit,
    onCategoryChange: (String?) -> Unit,
    sort: LibrarySort,
    sortMenuExpanded: Boolean,
    onSortMenuExpandedChange: (Boolean) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("library_search_field"),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search title, author, category or ISBN") }
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(LibraryStatusFilter.entries) { item ->
                FilterChip(
                    selected = status == item,
                    onClick = { onStatusChange(item) },
                    label = { Text(statusFilterLabel(item)) }
                )
            }
            item {
                FilterChip(
                    selected = favoritesOnly,
                    onClick = onToggleFavorites,
                    leadingIcon = {
                        Icon(
                            imageVector = if (favoritesOnly) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Favorites") }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                TextButton(onClick = { onCategoryMenuExpandedChange(true) }) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Spacer(Modifier.size(4.dp))
                    Text(category ?: "All categories")
                }
                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { onCategoryMenuExpandedChange(false) }
                ) {
                    DropdownMenuItem(
                        text = { Text("All categories") },
                        onClick = {
                            onCategoryChange(null)
                            onCategoryMenuExpandedChange(false)
                        }
                    )
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                onCategoryChange(item)
                                onCategoryMenuExpandedChange(false)
                            }
                        )
                    }
                }
            }

            Box {
                TextButton(onClick = { onSortMenuExpandedChange(true) }) {
                    Text("Sort: ${sortLabel(sort)}")
                }
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { onSortMenuExpandedChange(false) }
                ) {
                    LibrarySort.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(sortLabel(item)) },
                            onClick = {
                                onSortChange(item)
                                onSortMenuExpandedChange(false)
                            }
                        )
                    }
                }
            }

            TextButton(onClick = onClear) {
                Text("Reset")
            }
        }
    }
}

@Composable
private fun ContinueReadingCard(book: Book, onResume: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onResume)
            .testTag("continue_reading_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BookCover(
                title = book.title,
                author = book.author,
                coverUrl = book.coverUrl,
                modifier = Modifier.weight(0.8f)
            )
            Column(modifier = Modifier.weight(1.4f)) {
                Text(
                    text = "CONTINUE READING",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(book.title, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(book.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { book.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                Text("${(book.progress * 100).toInt()}% complete", style = MaterialTheme.typography.labelMedium)
                if (book.quote.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "\"${book.quote}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F0F0F),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text("Resume")
                }
            }
        }
    }
}

@Composable
private fun LibraryBookCard(
    book: Book,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("book_item_${book.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onOpen)
        ) {
            BookCover(
                title = book.title,
                author = book.author,
                coverUrl = book.coverUrl,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(book.progress.coerceIn(0f, 1f))
                        .background(Color(0xFF0F0F0F))
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            ) {
                Text(
                    text = statusLabel(book.readingStatus),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (book.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Book actions")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            if (book.isFinished) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = CircleShape,
                    color = Color(0xFF0F0F0F)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp).size(14.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.author.ifBlank { "Unknown author" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (book.category.isNotBlank()) {
            Text(
                text = book.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AddBookCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .testTag("add_to_library_card"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text("Add book", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun EmptyLibraryState(
    hasBooks: Boolean,
    onClearFilters: () -> Unit,
    onAddBook: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (hasBooks) "No books match these filters" else "Your library is empty",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (hasBooks) "Try another search, category or reading status." else "Add your first book to begin building your sanctuary.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = if (hasBooks) onClearFilters else onAddBook) {
                Text(if (hasBooks) "Reset filters" else "Add book")
            }
        }
    }
}

private data class BookForm(
    val title: String,
    val author: String,
    val category: String,
    val description: String,
    val isbn: String,
    val publisher: String,
    val pageCount: Int,
    val readingStatus: String,
    val coverUrl: String
)

@Composable
private fun BookEditorDialog(
    book: Book?,
    onDismiss: () -> Unit,
    onSave: (BookForm) -> Unit
) {
    var title by remember(book?.id) { mutableStateOf(book?.title.orEmpty()) }
    var author by remember(book?.id) { mutableStateOf(book?.author.orEmpty()) }
    var category by remember(book?.id) { mutableStateOf(book?.category.orEmpty()) }
    var description by remember(book?.id) { mutableStateOf(book?.description.orEmpty()) }
    var isbn by remember(book?.id) { mutableStateOf(book?.isbn.orEmpty()) }
    var publisher by remember(book?.id) { mutableStateOf(book?.publisher.orEmpty()) }
    var pageCount by remember(book?.id) { mutableStateOf(book?.pageCount?.takeIf { it > 0 }?.toString().orEmpty()) }
    var readingStatus by remember(book?.id) { mutableStateOf(book?.readingStatus ?: ReadingStatus.TO_READ) }
    var coverUrl by remember(book?.id) { mutableStateOf(book?.coverUrl.orEmpty()) }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (book == null) "Add book" else "Edit book") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth().testTag("book_title_field"),
                    label = { Text("Title *") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Author") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Category") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pageCount,
                        onValueChange = { pageCount = it.filter(Char::isDigit) },
                        modifier = Modifier.weight(0.7f),
                        label = { Text("Pages") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = isbn,
                        onValueChange = { isbn = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("ISBN") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = publisher,
                        onValueChange = { publisher = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Publisher") },
                        singleLine = true
                    )
                }
                Box {
                    TextButton(onClick = { statusMenuExpanded = true }) {
                        Text("Status: ${statusLabel(readingStatus)}")
                    }
                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        ReadingStatus.values.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(statusLabel(status)) },
                                onClick = {
                                    readingStatus = status
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cover URL") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(
                        BookForm(
                            title = title,
                            author = author,
                            category = category,
                            description = description,
                            isbn = isbn,
                            publisher = publisher,
                            pageCount = pageCount.toIntOrNull() ?: 0,
                            readingStatus = readingStatus,
                            coverUrl = coverUrl
                        )
                    )
                }
            ) {
                Text(if (book == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun statusFilterLabel(status: LibraryStatusFilter): String = when (status) {
    LibraryStatusFilter.ALL -> "All"
    LibraryStatusFilter.TO_READ -> "To read"
    LibraryStatusFilter.READING -> "Reading"
    LibraryStatusFilter.COMPLETED -> "Completed"
    LibraryStatusFilter.PAUSED -> "Paused"
    LibraryStatusFilter.ABANDONED -> "Abandoned"
}

private fun statusLabel(status: String): String = when (status) {
    ReadingStatus.TO_READ -> "To read"
    ReadingStatus.READING -> "Reading"
    ReadingStatus.COMPLETED -> "Completed"
    ReadingStatus.PAUSED -> "Paused"
    ReadingStatus.ABANDONED -> "Abandoned"
    else -> "To read"
}

private fun sortLabel(sort: LibrarySort): String = when (sort) {
    LibrarySort.RECENT -> "Recent"
    LibrarySort.TITLE -> "Title"
    LibrarySort.AUTHOR -> "Author"
    LibrarySort.PROGRESS -> "Progress"
}
