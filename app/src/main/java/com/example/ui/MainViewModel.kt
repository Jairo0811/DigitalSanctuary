package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Annotation
import com.example.data.AppDatabase
import com.example.data.AppSetting
import com.example.data.Book
import com.example.data.ReadingStatus
import com.example.data.Repository
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

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository

    val allBooks: StateFlow<List<Book>>
    val allAnnotations: StateFlow<List<Annotation>>
    val settingsState: StateFlow<AppSetting>

    private val _selectedBookId = MutableStateFlow<String?>("architecture_attention")
    val selectedBookId: StateFlow<String?> = _selectedBookId.asStateFlow()

    private val _libraryFilterState = MutableStateFlow(LibraryFilterState())
    val libraryFilterState: StateFlow<LibraryFilterState> = _libraryFilterState.asStateFlow()
    val libraryBooks: StateFlow<List<Book>>

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

        settingsState = repository.settingsFlow
            .map { it ?: AppSetting() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppSetting()
            )

        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getSettings() == null) {
                repository.saveSettings(AppSetting())
            }
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
            val newBook = Book(
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
            repository.insertBook(newBook)
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
            if (_selectedBookId.value == bookId) {
                _selectedBookId.value = null
            }
        }
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookById(bookId)?.let { book ->
                repository.updateBook(book.copy(isFavorite = !book.isFavorite))
            }
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

    fun toggleJengaMode() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getSettings() ?: AppSetting()
            repository.saveSettings(current.copy(jengaMode = !current.jengaMode))
        }
    }

    fun updateTextSize(increase: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getSettings() ?: AppSetting()
            val newSize = if (increase) {
                (current.textSize + 2).coerceAtMost(28)
            } else {
                (current.textSize - 2).coerceAtLeast(14)
            }
            repository.saveSettings(current.copy(textSize = newSize))
        }
    }

    fun updateBleachLevel(level: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getSettings() ?: AppSetting()
            repository.saveSettings(current.copy(bleachLevel = level))
        }
    }

    fun updateRefreshMode(mode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getSettings() ?: AppSetting()
            repository.saveSettings(current.copy(refreshMode = mode))
        }
    }

    fun toggleHdSymbolLogic() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getSettings() ?: AppSetting()
            repository.saveSettings(current.copy(hdSymbolLogic = !current.hdSymbolLogic))
        }
    }

    fun updateAnimationDuration(duration: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getSettings() ?: AppSetting()
            repository.saveSettings(current.copy(animationDuration = duration))
        }
    }

    fun addAnnotation(annotation: Annotation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAnnotation(annotation)
        }
    }

    fun deleteAnnotation(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAnnotationById(id)
        }
    }
}
