package com.jairomatias.digitalsanctuary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Book::class, Annotation::class, Bookmark::class, KnowledgeLink::class, AppSetting::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN isbn TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE books ADD COLUMN publisher TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE books ADD COLUMN pageCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN readingStatus TEXT NOT NULL DEFAULT 'TO_READ'")
                db.execSQL("ALTER TABLE books ADD COLUMN rating INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN dateAdded INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN startedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN finishedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE books SET readingStatus = CASE WHEN isFinished = 1 THEN 'COMPLETED' WHEN progress > 0 THEN 'READING' ELSE 'TO_READ' END")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN localUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE books ADD COLUMN documentFormat TEXT NOT NULL DEFAULT 'NONE'")
                db.execSQL("ALTER TABLE books ADD COLUMN currentLocation INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN totalLocations INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE annotations ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE annotations ADD COLUMN locationIndex INTEGER NOT NULL DEFAULT 0")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS bookmarks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "bookId TEXT NOT NULL, " +
                        "locationIndex INTEGER NOT NULL, " +
                        "label TEXT NOT NULL, " +
                        "createdAt INTEGER NOT NULL)"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_bookmarks_bookId_locationIndex ON bookmarks (bookId, locationIndex)")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS knowledge_links (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "fromAnnotationId INTEGER NOT NULL, " +
                        "toAnnotationId INTEGER NOT NULL, " +
                        "relation TEXT NOT NULL, " +
                        "createdAt INTEGER NOT NULL)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_knowledge_links_fromAnnotationId ON knowledge_links (fromAnnotationId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_knowledge_links_toAnnotationId ON knowledge_links (toAnnotationId)")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digital_sanctuary_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) { populateDatabase(database.appDao()) }
            }
        }

        private suspend fun populateDatabase(dao: AppDao) {
            dao.insertSettings(AppSetting())
            val now = System.currentTimeMillis()
            dao.insertBooks(
                listOf(
                    Book(
                        id = "meditations",
                        title = "Meditations",
                        author = "Marcus Aurelius",
                        category = "Philosophy",
                        progress = 0.68f,
                        quote = "You have power over your mind - not outside events.",
                        description = "Personal writings on Stoic practice and disciplined attention.",
                        readingStatus = ReadingStatus.READING,
                        dateAdded = now,
                        startedAt = now,
                        lastReadTimestamp = now
                    ),
                    Book(
                        id = "design_everyday_things",
                        title = "The Design of Everyday Things",
                        author = "Don Norman",
                        category = "Design",
                        progress = 0.12f,
                        description = "A foundational work on cognitive design, affordances and usability.",
                        readingStatus = ReadingStatus.READING,
                        dateAdded = now - 5_000,
                        startedAt = now - 5_000,
                        lastReadTimestamp = now - 5_000
                    ),
                    Book(
                        id = "thinking_fast_slow",
                        title = "Thinking, Fast and Slow",
                        author = "Daniel Kahneman",
                        category = "Cognition",
                        progress = 1f,
                        isFinished = true,
                        description = "An exploration of fast intuitive and slow deliberative thinking.",
                        readingStatus = ReadingStatus.COMPLETED,
                        dateAdded = now - 10_000,
                        startedAt = now - 10_000,
                        finishedAt = now - 9_000,
                        lastReadTimestamp = now - 10_000
                    ),
                    Book(
                        id = "sapiens",
                        title = "Sapiens",
                        author = "Yuval Noah Harari",
                        category = "History",
                        progress = 0.45f,
                        description = "A broad survey of the history of Homo sapiens.",
                        readingStatus = ReadingStatus.READING,
                        dateAdded = now - 15_000,
                        startedAt = now - 15_000,
                        lastReadTimestamp = now - 15_000
                    ),
                    Book(
                        id = "deep_work",
                        title = "Deep Work",
                        author = "Cal Newport",
                        category = "Productivity",
                        progress = 0.05f,
                        description = "Rules for focused success in a distracted world.",
                        readingStatus = ReadingStatus.READING,
                        dateAdded = now - 20_000,
                        startedAt = now - 20_000,
                        lastReadTimestamp = now - 20_000
                    ),
                    Book(
                        id = "architecture_attention",
                        title = "The Architecture of Attention",
                        author = "Dr. Elias Vance",
                        category = "Cognitive Friction",
                        progress = 0.18f,
                        description = "A Digital Sanctuary sample text about attention, interfaces and cognitive friction.",
                        readingStatus = ReadingStatus.READING,
                        dateAdded = now - 25_000,
                        startedAt = now - 25_000,
                        lastReadTimestamp = now - 25_000
                    )
                )
            )

            dao.insertAnnotation(
                Annotation(
                    bookId = "architecture_attention",
                    type = "Insight",
                    content = "Friction can be designed as a protective boundary for attention.",
                    note = "Connect this with Deep Work and interface ethics.",
                    bookTitle = "The Architecture of Attention",
                    bookAuthor = "Dr. Elias Vance",
                    locationInfo = "Chapter 3",
                    tags = "attention,design"
                )
            )
        }
    }
}
