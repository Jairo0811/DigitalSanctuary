package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Annotation
import com.example.data.AppDatabase
import com.example.data.Book
import com.example.data.AppSetting
import com.example.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        val appDatabase = AppDatabase.getDatabase(application, viewModelScope)
        repository = Repository(appDatabase.appDao())

        allBooks = repository.allBooks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allAnnotations = repository.allAnnotations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Generate a default setting if settings database returns null
        settingsState = repository.settingsFlow
            .map { it ?: AppSetting() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppSetting()
            )

        // Ensure settings exist at bootstrap (fallback trigger)
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getSettings() == null) {
                repository.saveSettings(AppSetting())
            }
        }
    }

    fun selectBook(bookId: String?) {
        _selectedBookId.value = bookId
        if (bookId != null) {
            // Update last read timestamp on background thread
            viewModelScope.launch(Dispatchers.IO) {
                repository.getBookById(bookId)?.let { book ->
                    repository.updateBook(book.copy(lastReadTimestamp = System.currentTimeMillis()))
                }
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

    fun updateBookProgress(bookId: String, progress: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookById(bookId)?.let { currentBook ->
                val isFinished = progress >= 0.99f
                repository.updateBook(
                    currentBook.copy(
                        progress = progress,
                        isFinished = isFinished,
                        lastReadTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun addNewBook(title: String, author: String, category: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = title.lowercase().replace(" ", "_")
            val newBook = Book(
                id = id,
                title = title,
                author = author,
                category = category,
                progress = 0.0f,
                coverUrl = "", // Empty triggers nice custom styled card
                description = description,
                lastReadTimestamp = System.currentTimeMillis()
            )
            repository.insertBook(newBook)
        }
    }
}
