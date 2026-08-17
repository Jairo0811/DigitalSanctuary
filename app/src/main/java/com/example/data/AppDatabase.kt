package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Book::class, Annotation::class, AppSetting::class], version = 2, exportSchema = false)
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

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digital_sanctuary_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.appDao())
                }
            }
        }

        suspend fun populateDatabase(dao: AppDao) {
            dao.insertSettings(AppSetting())
            val now = System.currentTimeMillis()

            val meditationsCover = "https://lh3.googleusercontent.com/aida-public/AB6AXuCNoTFq2rPvLbXA6Rw9kifUjRTgK8hkYC1Q5IHKi73cxI_hdPgOPFMxVhVOiHhJTyRydRST9sK5dtzWriXmiMvdW3M-MIhEWQAk725RUEnUWT2LounDQrpw6yYa9zgHNmy23OmmgyydFmQEMfkM4NDzEoX7eXrhQjdCNWuaPhMYwN6SLzLUrbwBMTE4IMU_kyy3GIo0vmcO3myKBQ9hdn9TzWfY9xHh7pjZtsae0giKYgvvoKPVnS92PgvHC2BDfAaX58Pby6XQBoY"
            val targetDesignCover = "https://lh3.googleusercontent.com/aida-public/AB6AXuCHoQQOzwpvZhVHxMR8lCez3a0V_OUPIV5Qa9p4S_uGjNH4SX0__Y8eyOF7aMQFG3-lHtYUfzl_kUCJSJX6-Mbp_xmmrRo0L1eESIvKauZJPu-4Uq5TAQyqV8L7bx4JvWzOFfypP8WCUXZCY8nO8a3Ebu1PyVWD0AEuCtP_PmgFw62FrCWDSHnW2SQropsiVelApQYtVAFjKKOzsbbChAoUIfYfk_eBRT7jbHbc1QhPJRcsZOq5YSRDPCkx1xfTx6s8YovNYqty2Oc"
            val fastSlowCover = "https://lh3.googleusercontent.com/aida-public/AB6AXuAyqsG9U5J1hNJZdGXj8aUVo9C53fYwuFE6O2srzjW9-VvUGlrcWXOq_9DTTUfV3tIjiqKFMQVYBxlH1J6KC7qjf7Sf6zF0kiS9SHHt72TKSe6_wxWHmCCsvJx4Ml3UByDb3Nx56rTff81S3tL2TkZlKV7t5PzmKNRVsWGOVPNXDoPdJ4igrdLnZC4Ac0gX_RAsbZ02hjroc3Vhok78DXa-CgqtKCKESCfODbNHLSGgaN1ZAh72Tt1zr_0JF1U-nBUbG6qqeuVbDxU"
            val sapiensCover = "https://lh3.googleusercontent.com/aida-public/AB6AXuDglJl5uLL6REyW8WYchzAaCJ9ufFFOUjLJla6rfL1IEBZ0J9tH3ddYjoStmG8PsSJJ_TzEPsNbBx045VeVubrXawoE97a5h_QgXHOJAOOJCKNsrZiPkY_P7laKR7Hm6YYRoz-ou72bNijySFkgLgfjP7unzFSTj7QaGttwtFae2bAvGTJ2F2c1UD99RBpvOadEg4XChteTpS4oFgZzDHvQc8e050sVoYQ-CWcAJ8PcA41gLHhzgDokqSGTQUhzqcnHT2aXJ21V_ao"

            val books = listOf(
                Book(
                    id = "meditations",
                    title = "Meditations",
                    author = "Marcus Aurelius",
                    category = "Philosophy",
                    progress = 0.68f,
                    quote = "You have power over your mind - not outside events. Realize this, and you will find strength.",
                    coverUrl = meditationsCover,
                    description = "A series of personal writings by Marcus Aurelius, Roman Emperor, outlining his ideas on Stoic philosophy.",
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
                    coverUrl = targetDesignCover,
                    description = "A best-selling book on cognitive engineering and aesthetic ergonomics by cognitive scientist Don Norman.",
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
                    progress = 1.0f,
                    isFinished = true,
                    coverUrl = fastSlowCover,
                    description = "An exploration of the mind's dual systems of fast, emotional, and intuitive thinking combined with slow, deliberative choices.",
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
                    coverUrl = sapiensCover,
                    description = "A bold survey of human history detailing the evolution and cultural achievements of Homo sapiens from prehistoric bands to algorithms.",
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
                    description = "Rules for focused success in a distracted world, advocating for intense intellectual focus as a modern superpower.",
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
                    description = "An academic treatise exploring Digital Ergonomics, Visual-Syntactic Text Formatting, and reclaiming focus away from hyper-extraction loops.",
                    readingStatus = ReadingStatus.READING,
                    dateAdded = now - 25_000,
                    startedAt = now - 25_000,
                    lastReadTimestamp = now - 25_000
                )
            )
            dao.insertBooks(books)

            dao.insertAnnotation(
                Annotation(
                    bookId = "understanding_media",
                    type = "Thesis",
                    content = "The medium, or process, of our time—electric technology—is reshaping and restructuring patterns of social interdependence and every aspect of our personal life.",
                    note = "A core structural transformation note regarding media determinism.",
                    bookTitle = "Understanding Media: The Extensions of Man",
                    bookAuthor = "Marshall McLuhan",
                    locationInfo = "p. 8"
                )
            )

            dao.insertAnnotation(
                Annotation(
                    bookId = "designing_organizations",
                    type = "Insight",
                    content = "In an information-rich world, the wealth of information means a dearth of something else: a scarcity of whatever it is that information consumes. What information consumes is rather obvious: it consumes the attention of its recipients.",
                    note = "Connects strongly with the current distraction-economy models. Need to cross-reference with Cal Newport's work on Deep Work.",
                    bookTitle = "Designing Organizations for an Information-Rich World",
                    bookAuthor = "Herbert Simon",
                    locationInfo = "Herbert Simon Lecture"
                )
            )

            dao.insertAnnotation(
                Annotation(
                    bookId = "everyday_things",
                    type = "Source",
                    content = "The Design of Everyday Things",
                    note = "Imported via Calibre integration",
                    bookTitle = "The Design of Everyday Things",
                    bookAuthor = "Don Norman",
                    locationInfo = "Affordances, UX"
                )
            )
        }
    }
}
