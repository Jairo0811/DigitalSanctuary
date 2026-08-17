package com.example.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAssistant
import com.example.ai.AiResult
import com.example.ai.GeminiAiAssistant
import com.example.data.Annotation
import com.example.data.AppDatabase
import com.example.data.AppSetting
import com.example.data.Book
import com.example.data.Bookmark
import com.example.data.DocumentFormat
import com.example.data.KnowledgeLink
import com.example.data.ReadingStatus
import com.example.data.Repository
import com.example.reader.ReaderEngine
import com.example.ui.library.LibraryFilterState
import com.example.ui.library.LibrarySort
import com.example.ui.library.LibraryStatusFilter
import com.example.ui.library.filterAndSortBooks
import com.example.ui.library.normalizedReadingStatus
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Success(val instruction: String, val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

data class KnowledgeSearchResult(
    val books: List<Book> = emptyList(),
    val annotations: List<Annotation> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    private val aiAssistant: AiAssistant = GeminiAiAssistant()

    val allBooks: StateFlow<List<Book>>
    val allAnnotations: StateFlow<List<Annotation>>
    val allBookmarks: StateFlow<List<Bookmark>>
    val knowledgeLinks: StateFlow<List<KnowledgeLink>>
    val settingsState: StateFlow<AppSetting>

    private val _selectedBookId = MutableStateFlow<String?>("architecture_attention")
    val selectedBookId: StateFlow<String?> = _selectedBookId.asStateFlow()

    private val _libraryFilterState = MutableStateFlow(LibraryFilterState())
    val libraryFilterState: StateFlow<LibraryFilterState> = _libraryFilterState.asStateFlow()
    val libraryBooks: StateFlow<List<Book>>

    private val _knowledgeQuery = MutableStateFlow("")
    val knowledgeQuery: StateFlow<String> = _knowledgeQuery.asStateFlow()
    val knowledgeSearchResults: StateFlow<KnowledgeSearchResult>

    private val _aiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val aiState: StateFlow<AiUiState> = _aiState.asStateFlow()

    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

    init {
        val appDatabase = AppDatabase.getDatabase(application, viewModelScope)
        repository = Repository(appDatabase.appDao())

        allBooks = repository.allBooks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        libraryBooks = combine(allBooks, _libraryFilterState) { books, filter ->
            filterAndSortBooks(books, filter)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        allAnnotations = repository.allAnnotations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        allBookmarks = repository.allBookmarks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        knowledgeLinks = repository.knowledgeLinks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        settingsState = repository.settingsFlow
            .map { it ?: AppSetting() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppSetting()
            )

        knowledgeSearchResults = combine(allBooks, allAnnotations, _knowledgeQuery) { books, notes, query ->
            val normalized = query.trim().lowercase()
            if (normalized.isBlank()) {
                KnowledgeSearchResult(books = books, annotations = notes)
            } else {
                KnowledgeSearchResult(
                    books = books.filter { book ->
                        listOf(book.title, book.author, book.category, book.description, book.isbn, book.publisher)
                            .any { it.lowercase().contains(normalized) }
                    },
                    annotations = notes.filter { note ->
                        listOf(note.content, note.note, note.bookTitle, note.bookAuthor, note.locationInfo, note.tags)
                            .any { it.lowercase().contains(normalized) }
                    }
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = KnowledgeSearchResult()
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getSettings() == null) repository.saveSettings(AppSetting())
        }
    }

    fun selectBook(bookId: String?) {
        _selectedBookId.value = bookId
        if (bookId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getBookById(bookId)?.let { book ->
                    val now = System.currentTimeMillis()
                    val status = if (book.readingStatus == ReadingStatus.TO_READ) ReadingStatus.READING else book.readingStatus
                    repository.updateBook(
                        book.copy(
                            readingStatus = status,
                            startedAt = if (book.startedAt == 0L) now else book.startedAt,
                            lastReadTimestamp = now
                        )
                    )
                }
            }
        }
    }

    fun importDocument(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            try {
                runCatching {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val metadata = ReaderEngine.inspect(context, uri)
                if (metadata.format == DocumentFormat.NONE) {
                    _importMessage.value = "Unsupported file. Choose an EPUB or PDF document."
                    return@launch
                }
                val now = System.currentTimeMillis()
                val book = Book(
                    id = UUID.randomUUID().toString(),
                    title = metadata.title,
                    author = "Unknown author",
                    category = "Imported",
                    progress = 0f,
                    description = "Imported ${metadata.format} document",
                    readingStatus = ReadingStatus.TO_READ,
                    dateAdded = now,
                    lastReadTimestamp = now,
                    localUri = uri.toString(),
                    documentFormat = metadata.format,
                    pageCount = if (metadata.format == DocumentFormat.PDF) metadata.locationCount else 0,
                    totalLocations = metadata.locationCount
                )
                repository.insertBook(book)
                _selectedBookId.value = book.id
                _importMessage.value = "${metadata.title} imported successfully"
            } catch (error: Exception) {
                _importMessage.value = error.message ?: "Unable to import document"
            }
        }
    }

    fun clearImportMessage() {
        _importMessage.value = null
    }

    fun updateReaderLocation(bookId: String, location: Int, totalLocations: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookById(bookId)?.let { book ->
                val safeTotal = totalLocations.coerceAtLeast(1)
                val safeLocation = location.coerceIn(0, safeTotal - 1)
                val progress = ((safeLocation + 1).toFloat() / safeTotal).coerceIn(0f, 1f)
                val now = System.currentTimeMillis()
                val status = normalizedReadingStatus(book.readingStatus, progress)
                repository.updateBook(
                    book.copy(
                        currentLocation = safeLocation,
                        totalLocations = safeTotal,
                        progress = progress,
                        isFinished = status == ReadingStatus.COMPLETED,
                        readingStatus = status,
                        startedAt = if (book.startedAt == 0L) now else book.startedAt,
                        finishedAt = if (status == ReadingStatus.COMPLETED) (book.finishedAt.takeIf { it > 0 } ?: now) else 0L,
                        lastReadTimestamp = now
                    )
                )
            }
        }
    }

    fun toggleBookmark(bookId: String, locationIndex: Int, label: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getBookmark(bookId, locationIndex)
            if (existing == null) {
                repository.insertBookmark(Bookmark(bookId = bookId, locationIndex = locationIndex, label = label))
            } else {
                repository.deleteBookmark(existing)
            }
        }
    }

    fun updateKnowledgeQuery(query: String) {
        _knowledgeQuery.value = query
    }

    fun addKnowledgeNote(
        bookId: String,
        type: String,
        content: String,
        note: String,
        tags: String,
        locationInfo: String = "",
        locationIndex: Int = 0
    ) {
        if (content.isBlank() && note.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookById(bookId)
            repository.insertAnnotation(
                Annotation(
                    bookId = bookId,
                    type = type,
                    content = content.trim(),
                    note = note.trim(),
                    tags = normalizeTags(tags),
                    bookTitle = book?.title.orEmpty(),
                    bookAuthor = book?.author.orEmpty(),
                    locationInfo = locationInfo,
                    locationIndex = locationIndex
                )
            )
        }
    }

    fun updateAnnotationTags(annotation: Annotation, tags: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAnnotation(annotation.copy(tags = normalizeTags(tags)))
        }
    }

    fun linkAnnotations(fromAnnotationId: Int, toAnnotationId: Int, relation: String = "related") {
        if (fromAnnotationId <= 0 || toAnnotationId <= 0 || fromAnnotationId == toAnnotationId) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertKnowledgeLink(
                KnowledgeLink(
                    fromAnnotationId = fromAnnotationId,
                    toAnnotationId = toAnnotationId,
                    relation = relation.ifBlank { "related" }
                )
            )
        }
    }

    fun runAi(instruction: String, context: String) {
        if (context.isBlank()) {
            _aiState.value = AiUiState.Error("Select or provide text first.")
            return
        }
        viewModelScope.launch {
            _aiState.value = AiUiState.Loading
            _aiState.value = when (val result = aiAssistant.ask(instruction, context)) {
                is AiResult.Success -> AiUiState.Success(instruction, result.text)
                is AiResult.Error -> AiUiState.Error(result.message)
            }
        }
    }

    fun saveAiResultAsInsight(bookId: String, locationInfo: String = "", locationIndex: Int = 0) {
        val current = _aiState.value as? AiUiState.Success ?: return
        addKnowledgeNote(
            bookId = bookId,
            type = "AI Insight",
            content = current.text,
            note = "Generated by Gemini: ${current.instruction}",
            tags = "ai,insight",
            locationInfo = locationInfo,
            locationIndex = locationIndex
        )
    }

    fun clearAiState() {
        _aiState.value = AiUiState.Idle
    }

    fun exportKnowledgeAsMarkdown(): String {
        val notes = allAnnotations.value
        if (notes.isEmpty()) return "# Digital Sanctuary\n\nNo notes yet."
        return buildString {
            appendLine("# Digital Sanctuary — Knowledge Export")
            appendLine()
            notes.groupBy { it.bookTitle.ifBlank { "Unassigned" } }.forEach { (book, entries) ->
                appendLine("## $book")
                appendLine()
                entries.forEach { entry ->
                    appendLine("### ${entry.type}")
                    if (entry.locationInfo.isNotBlank()) appendLine("_${entry.locationInfo}_")
                    appendLine()
                    appendLine(entry.content)
                    if (entry.note.isNotBlank()) appendLine("\n> ${entry.note}")
                    if (entry.tags.isNotBlank()) appendLine("\nTags: ${entry.tags}")
                    appendLine()
                }
            }
        }
    }

    fun updateLibraryQuery(query: String) {
        _libraryFilterState.value = _libraryFilterState.value.copy(query = query)
    }

    fun updateLibraryStatus(status: LibraryStatusFilter) {
        _libraryFilterState.value = _libraryFilterState.value.copy(status = status)
    }

    fun updateLibraryCategory(category: String?) {
        _libraryFilterState.value = _libraryFilterState.value.copy(category = category)
    }

    fun toggleFavoritesFilter() {
        val current = _libraryFilterState.value
        _libraryFilterState.value = current.copy(favoritesOnly = !current.favoritesOnly)
    }

    fun updateLibrarySort(sort: LibrarySort) {
        _libraryFilterState.value = _libraryFilterState.value.copy(sort = sort)
    }

    fun clearLibraryFilters() {
        _libraryFilterState.value = LibraryFilterState()
    }

    fun addNewBook(
        title: String,
        author: String,
        category: String,
        description: String,
        isbn: String = "",
        publisher: String = "",
        pageCount: Int = 0,
        readingStatus: String = ReadingStatus.TO_READ,
        coverUrl: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            repository.insertBook(
                Book(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    author = author.trim(),
                    category = category.trim(),
                    progress = if (readingStatus == ReadingStatus.COMPLETED) 1f else 0f,
                    isFinished = readingStatus == ReadingStatus.COMPLETED,
                    coverUrl = coverUrl.trim(),
                    description = description.trim(),
                    isbn = isbn.trim(),
                    publisher = publisher.trim(),
                    pageCount = pageCount.coerceAtLeast(0),
                    readingStatus = readingStatus,
                    dateAdded = now,
                    startedAt = if (readingStatus == ReadingStatus.READING) now else 0L,
                    finishedAt = if (readingStatus == ReadingStatus.COMPLETED) now else 0L,
                    lastReadTimestamp = now
                )
            )
        }
    }

    fun updateBookDetails(
        book: Book,
        title: String,
        author: String,
        category: String,
        description: String,
        isbn: String,
        publisher: String,
        pageCount: Int,
        readingStatus: String,
        coverUrl: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val completed = readingStatus == ReadingStatus.COMPLETED
            repository.updateBook(
                book.copy(
                    title = title.trim(),
                    author = author.trim(),
                    category = category.trim(),
                    description = description.trim(),
                    isbn = isbn.trim(),
                    publisher = publisher.trim(),
                    pageCount = pageCount.coerceAtLeast(0),
                    readingStatus = readingStatus,
                    coverUrl = coverUrl.trim(),
                    progress = if (completed) 1f else book.progress.coerceIn(0f, 0.99f),
                    isFinished = completed,
                    startedAt = if (readingStatus == ReadingStatus.READING && book.startedAt == 0L) now else book.startedAt,
                    finishedAt = if (completed) (book.finishedAt.takeIf { it > 0L } ?: now) else 0L
                )
            )
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBookById(bookId)
            if (_selectedBookId.value == bookId) _selectedBookId.value = null
        }
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookById(bookId)?.let { repository.updateBook(it.copy(isFavorite = !it.isFavorite)) }
        }
    }

    fun updateBookProgress(bookId: String, progress: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookById(bookId)?.let { currentBook ->
                val clampedProgress = progress.coerceIn(0f, 1f)
                val now = System.currentTimeMillis()
                val status = normalizedReadingStatus(currentBook.readingStatus, clampedProgress)
                val finished = status == ReadingStatus.COMPLETED
                repository.updateBook(
                    currentBook.copy(
                        progress = clampedProgress,
                        isFinished = finished,
                        readingStatus = status,
                        startedAt = if (clampedProgress > 0f && currentBook.startedAt == 0L) now else currentBook.startedAt,
                        finishedAt = if (finished) (currentBook.finishedAt.takeIf { it > 0L } ?: now) else 0L,
                        lastReadTimestamp = now
                    )
                )
            }
        }
    }

    fun toggleJengaMode() = updateSettings { it.copy(jengaMode = !it.jengaMode) }

    fun updateTextSize(increase: Boolean) = updateSettings { current ->
        val size = if (increase) (current.textSize + 2).coerceAtMost(32) else (current.textSize - 2).coerceAtLeast(12)
        current.copy(textSize = size)
    }

    fun updateBleachLevel(level: String) = updateSettings { it.copy(bleachLevel = level) }
    fun updateRefreshMode(mode: String) = updateSettings { it.copy(refreshMode = mode) }
    fun toggleHdSymbolLogic() = updateSettings { it.copy(hdSymbolLogic = !it.hdSymbolLogic) }
    fun updateAnimationDuration(duration: Int) = updateSettings { it.copy(animationDuration = duration) }

    fun addAnnotation(annotation: Annotation) {
        viewModelScope.launch(Dispatchers.IO) { repository.insertAnnotation(annotation) }
    }

    fun deleteAnnotation(id: Int) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteAnnotationById(id) }
    }

    private fun updateSettings(transform: (AppSetting) -> AppSetting) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSettings(transform(repository.getSettings() ?: AppSetting()))
        }
    }

    private fun normalizeTags(tags: String): String = tags
        .split(',')
        .map { it.trim().lowercase() }
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString(",")
}
