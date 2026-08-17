package com.example.data

import kotlinx.coroutines.flow.Flow

class Repository(private val appDao: AppDao) {
    val allBooks: Flow<List<Book>> = appDao.getAllBooksFlow()
    val allAnnotations: Flow<List<Annotation>> = appDao.getAllAnnotationsFlow()
    val allBookmarks: Flow<List<Bookmark>> = appDao.getAllBookmarksFlow()
    val knowledgeLinks: Flow<List<KnowledgeLink>> = appDao.getKnowledgeLinksFlow()
    val settingsFlow: Flow<AppSetting?> = appDao.getSettingsFlow()

    fun getBookByIdFlow(id: String): Flow<Book?> = appDao.getBookByIdFlow(id)
    fun getBookmarksForBookFlow(bookId: String): Flow<List<Bookmark>> = appDao.getBookmarksForBookFlow(bookId)

    suspend fun getBookById(id: String): Book? = appDao.getBookById(id)
    suspend fun insertBook(book: Book) = appDao.insertBook(book)
    suspend fun updateBook(book: Book) = appDao.updateBook(book)
    suspend fun deleteBookById(id: String) = appDao.deleteBookById(id)

    suspend fun getAnnotationById(id: Int): Annotation? = appDao.getAnnotationById(id)
    suspend fun insertAnnotation(annotation: Annotation): Long = appDao.insertAnnotation(annotation)
    suspend fun updateAnnotation(annotation: Annotation) = appDao.updateAnnotation(annotation)
    suspend fun deleteAnnotationById(id: Int) = appDao.deleteAnnotationById(id)

    suspend fun getBookmark(bookId: String, locationIndex: Int): Bookmark? = appDao.getBookmark(bookId, locationIndex)
    suspend fun insertBookmark(bookmark: Bookmark) = appDao.insertBookmark(bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark) = appDao.deleteBookmark(bookmark)

    suspend fun insertKnowledgeLink(link: KnowledgeLink) = appDao.insertKnowledgeLink(link)
    suspend fun deleteKnowledgeLink(id: Int) = appDao.deleteKnowledgeLink(id)

    suspend fun getSettings(): AppSetting? = appDao.getSettings()
    suspend fun saveSettings(setting: AppSetting) = appDao.insertSettings(setting)
}
