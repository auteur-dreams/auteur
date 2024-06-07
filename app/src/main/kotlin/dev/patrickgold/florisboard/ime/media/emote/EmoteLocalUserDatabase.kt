package dev.patrickgold.florisboard.ime.media.emote

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.patrickgold.florisboard.lib.util.Converters

@Database(entities = [Emote::class, EmoteCategoryJoin::class, Category::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EmoteLocalUserDatabase : RoomDatabase() {
    abstract fun emoteDao(): EmoteDao

    companion object {
        @Volatile
        private var INSTANCE: EmoteLocalUserDatabase? = null

        fun getDatabase(context: Context): EmoteLocalUserDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): EmoteLocalUserDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                EmoteLocalUserDatabase::class.java,
                "emote_database"
            )
                .fallbackToDestructiveMigration() // Handle migrations appropriately in production apps
                .build()
        }
    }
}
