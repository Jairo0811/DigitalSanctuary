package com.jairomatias.digitalsanctuary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM books ORDER BY lastReadTimestamp DESC")
    fun getAllBooksFlow(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookById(id: String): Book?

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookByIdFlow(id: String): Flow<Book?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)

    @Update
    suspend fun updateBook(book: Book)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: String)

    @Query("SELECT * FROM annotations ORDER BY timestamp DESC")
    fun getAllAnnotationsFlow(): Flow<List<Annotation>>

    @Query("SELECT * FROM annotations WHERE id = :id LIMIT 1")
    suspend fun getAnnotationById(id: Int): Annotation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: Annotation): Long

    @Update
    suspend fun updateAnnotation(annotation: Annotation)

    @Query("DELETE FROM annotations WHERE id = :id")
    suspend fun deleteAnnotationById(id: Int)

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarksFlow(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY locationIndex")
    fun getBookmarksForBookFlow(bookId: String): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId AND locationIndex = :locationIndex LIMIT 1")
    suspend fun getBookmark(bookId: String, locationIndex: Int): Bookmark?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM knowledge_links ORDER BY createdAt DESC")
    fun getKnowledgeLinksFlow(): Flow<List<KnowledgeLink>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgeLink(link: KnowledgeLink)

    @Query("DELETE FROM knowledge_links WHERE id = :id")
    suspend fun deleteKnowledgeLink(id: Int)

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSetting?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(setting: AppSetting)
}
