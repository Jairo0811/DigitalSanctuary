package com.example.data

import kotlinx.coroutines.flow.Flow

class Repository(private val appDao: AppDao) {
    val allBooks: Flow<List<Book>> = appDao.getAllBooksFlow()
    val allAnnotations: Flow<List<Annotation>> = appDao.getAllAnnotationsFlow()
    val settingsFlow: Flow<AppSetting?> = appDao.getSettingsFlow()

    fun getBookByIdFlow(id: String): Flow<Book?> = appDao.getBookByIdFlow(id)

    suspend fun getBookById(id: String): Book? = appDao.getBookById(id)

    suspend fun insertBook(book: Book) = appDao.insertBook(book)

    suspend fun updateBook(book: Book) = appDao.updateBook(book)

    suspend fun deleteBookById(id: String) = appDao.deleteBookById(id)

    suspend fun insertAnnotation(annotation: Annotation) = appDao.insertAnnotation(annotation)

    suspend fun deleteAnnotationById(id: Int) = appDao.deleteAnnotationById(id)

    suspend fun getSettings(): AppSetting? = appDao.getSettings()

    suspend fun saveSettings(setting: AppSetting) = appDao.insertSettings(setting)
}
