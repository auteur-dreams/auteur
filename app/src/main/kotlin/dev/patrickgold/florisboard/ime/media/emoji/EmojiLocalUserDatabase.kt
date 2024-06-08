package dev.patrickgold.florisboard.ime.media.emoji

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.patrickgold.florisboard.lib.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@Database(entities = [Emoji::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EmojiLocalUserDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao

    companion object {
        @Volatile
        private var INSTANCE: EmojiLocalUserDatabase? = null

        fun getDatabase(context: Context): EmojiLocalUserDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): EmojiLocalUserDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                EmojiLocalUserDatabase::class.java,
                "emoji_database"
            )
                .fallbackToDestructiveMigration() // Handle migrations appropriately in production apps
                .addCallback(DatabaseCallback(context)) // Add callback for populating data
                .build()
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // populateEmojiDatabase(database.emojiDao())
                }
            }
        }

        private fun populateEmojiDatabase(emojiDao: EmojiDao) {
            val presetEmojis = loadEmojisFromTextFile("ime/media/emoji/root.txt")
            CoroutineScope(Dispatchers.IO).launch {
                emojiDao.insertAll(presetEmojis)
            }
        }

        private fun loadEmojisFromTextFile(filePath: String): List<Emoji> {
            val emojis = mutableListOf<Emoji>()
            val file = File(context.filesDir, filePath)
            if (file.exists()) {
                file.forEachLine { line ->
                    val parts = line.split(";")
                    if (parts.size == 3) {
                        val symbol = parts[0]
                        val name = parts[1]
                        val keywords = parts[2].split("|")
                        emojis.add(Emoji(symbol, name, keywords))
                    }
                }
            } else {
                // Handle the case where the file doesn't exist
                Log.d("EmojiLocalUserDatabase", "Failed to load emojis from text file")
            }
            return emojis
        }
    }
}
