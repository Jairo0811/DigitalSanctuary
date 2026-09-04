package com.jairomatias.digitalsanctuary.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PersistenceIntegrityTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private var database: AppDatabase? = null

    @After
    fun tearDown() {
        database?.close()
        context.deleteDatabase(MIGRATION_DB)
    }

    @Test
    fun `deleting a book cascades annotations bookmarks and knowledge links`() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        database = db
        val dao = db.appDao()

        dao.insertBook(sampleBook("book-1"))
        val firstNote = dao.insertAnnotation(sampleAnnotation("book-1", "First note")).toInt()
        val secondNote = dao.insertAnnotation(sampleAnnotation("book-1", "Second note")).toInt()
        dao.insertBookmark(Bookmark(bookId = "book-1", locationIndex = 2, label = "Page 3"))
        dao.insertKnowledgeLink(KnowledgeLink(fromAnnotationId = firstNote, toAnnotationId = secondNote))

        dao.deleteBookById("book-1")

        assertEquals(0, dao.getAllBooksFlow().first().size)
        assertEquals(0, dao.getAllAnnotationsFlow().first().size)
        assertEquals(0, dao.getAllBookmarksFlow().first().size)
        assertEquals(0, dao.getKnowledgeLinksFlow().first().size)
    }

    @Test
    fun `migration 3 to 4 preserves valid relationships and removes orphan rows`() = runBlocking {
        createVersion3Database()

        val db = Room.databaseBuilder(context, AppDatabase::class.java, MIGRATION_DB)
            .addMigrations(AppDatabase.MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()
        database = db
        db.openHelper.writableDatabase
        val dao = db.appDao()

        assertNotNull(dao.getBookById("valid-book"))
        assertEquals(listOf(1), dao.getAllAnnotationsFlow().first().map { it.id })
        assertEquals(listOf(1), dao.getAllBookmarksFlow().first().map { it.id })
        assertEquals(listOf(1), dao.getKnowledgeLinksFlow().first().map { it.id })

        dao.deleteBookById("valid-book")
        assertEquals(0, dao.getAllAnnotationsFlow().first().size)
        assertEquals(0, dao.getAllBookmarksFlow().first().size)
        assertEquals(0, dao.getKnowledgeLinksFlow().first().size)
    }

    private fun createVersion3Database() {
        context.deleteDatabase(MIGRATION_DB)
        val file = context.getDatabasePath(MIGRATION_DB)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL(
                "CREATE TABLE books (" +
                    "id TEXT NOT NULL, title TEXT NOT NULL, author TEXT NOT NULL, category TEXT NOT NULL, " +
                    "progress REAL NOT NULL, quote TEXT NOT NULL, isFinished INTEGER NOT NULL, coverUrl TEXT NOT NULL, " +
                    "description TEXT NOT NULL, lastReadTimestamp INTEGER NOT NULL, isbn TEXT NOT NULL DEFAULT '', " +
                    "publisher TEXT NOT NULL DEFAULT '', pageCount INTEGER NOT NULL DEFAULT 0, " +
                    "readingStatus TEXT NOT NULL DEFAULT 'TO_READ', rating INTEGER NOT NULL DEFAULT 0, " +
                    "isFavorite INTEGER NOT NULL DEFAULT 0, dateAdded INTEGER NOT NULL DEFAULT 0, " +
                    "startedAt INTEGER NOT NULL DEFAULT 0, finishedAt INTEGER NOT NULL DEFAULT 0, " +
                    "localUri TEXT NOT NULL DEFAULT '', documentFormat TEXT NOT NULL DEFAULT 'NONE', " +
                    "currentLocation INTEGER NOT NULL DEFAULT 0, totalLocations INTEGER NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(id))"
            )
            db.execSQL(
                "CREATE TABLE annotations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, bookId TEXT NOT NULL, type TEXT NOT NULL, " +
                    "content TEXT NOT NULL, note TEXT NOT NULL, bookTitle TEXT NOT NULL, bookAuthor TEXT NOT NULL, " +
                    "locationInfo TEXT NOT NULL, timestamp INTEGER NOT NULL, tags TEXT NOT NULL DEFAULT '', " +
                    "locationIndex INTEGER NOT NULL DEFAULT 0)"
            )
            db.execSQL(
                "CREATE TABLE bookmarks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, bookId TEXT NOT NULL, locationIndex INTEGER NOT NULL, " +
                    "label TEXT NOT NULL, createdAt INTEGER NOT NULL)"
            )
            db.execSQL("CREATE UNIQUE INDEX index_bookmarks_bookId_locationIndex ON bookmarks (bookId, locationIndex)")
            db.execSQL(
                "CREATE TABLE knowledge_links (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fromAnnotationId INTEGER NOT NULL, " +
                    "toAnnotationId INTEGER NOT NULL, relation TEXT NOT NULL, createdAt INTEGER NOT NULL)"
            )
            db.execSQL("CREATE INDEX index_knowledge_links_fromAnnotationId ON knowledge_links (fromAnnotationId)")
            db.execSQL("CREATE INDEX index_knowledge_links_toAnnotationId ON knowledge_links (toAnnotationId)")
            db.execSQL(
                "CREATE TABLE settings (" +
                    "id INTEGER NOT NULL, jengaMode INTEGER NOT NULL, textSize INTEGER NOT NULL, bleachLevel TEXT NOT NULL, " +
                    "hdSymbolLogic INTEGER NOT NULL, animationDuration INTEGER NOT NULL, refreshMode TEXT NOT NULL, " +
                    "PRIMARY KEY(id))"
            )

            db.execSQL(
                "INSERT INTO books (id, title, author, category, progress, quote, isFinished, coverUrl, description, lastReadTimestamp) " +
                    "VALUES ('valid-book', 'Valid', 'Author', 'Test', 0.0, '', 0, '', '', 1)"
            )
            db.execSQL(
                "INSERT INTO annotations (id, bookId, type, content, note, bookTitle, bookAuthor, locationInfo, timestamp, tags, locationIndex) VALUES " +
                    "(1, 'valid-book', 'Insight', 'Valid note', '', 'Valid', 'Author', '', 1, '', 0), " +
                    "(2, 'missing-book', 'Insight', 'Orphan note', '', 'Missing', 'Author', '', 2, '', 0)"
            )
            db.execSQL(
                "INSERT INTO bookmarks (id, bookId, locationIndex, label, createdAt) VALUES " +
                    "(1, 'valid-book', 1, 'Valid bookmark', 1), " +
                    "(2, 'missing-book', 2, 'Orphan bookmark', 2)"
            )
            db.execSQL(
                "INSERT INTO knowledge_links (id, fromAnnotationId, toAnnotationId, relation, createdAt) VALUES " +
                    "(1, 1, 1, 'related', 1), " +
                    "(2, 1, 2, 'related', 2)"
            )
            db.execSQL("INSERT INTO settings VALUES (1, 1, 18, 'High', 1, 100, 'Normal')")
            db.version = 3
        }
    }

    private fun sampleBook(id: String) = Book(
        id = id,
        title = "Test Book",
        author = "Test Author",
        category = "Testing",
        progress = 0f
    )

    private fun sampleAnnotation(bookId: String, content: String) = Annotation(
        bookId = bookId,
        type = "Insight",
        content = content,
        bookTitle = "Test Book",
        bookAuthor = "Test Author"
    )

    private companion object {
        const val MIGRATION_DB = "digital-sanctuary-migration-test.db"
    }
}
