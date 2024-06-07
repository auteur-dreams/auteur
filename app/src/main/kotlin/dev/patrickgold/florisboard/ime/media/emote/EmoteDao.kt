package dev.patrickgold.florisboard.ime.media.emote

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmoteDao {
    @Insert
    fun insert(emote: Emote)

    @Update
    fun update(emote: Emote)

    @Delete
    fun delete(emote: Emote)

    @Insert
    fun insert(join: EmoteCategoryJoin)

    @Query("SELECT * FROM emotes WHERE localPath IS NOT NULL")
    fun getAllLocalEmotes(): Flow<List<Emote>>

    @Query("SELECT * FROM emotes")
    fun getAllEmotesSync(): List<Emote>

    @Query("SELECT * FROM emotes WHERE isDownloaded = 1")
    fun getAllLocalEmotesSync(): List<Emote>

    @Query("SELECT * FROM emotes WHERE name = :name LIMIT 1")
    fun getEmoteByNameSync(name: String): Emote

    @Query("SELECT emotes.* FROM emotes " +
        "INNER JOIN emote_category_join ON emotes.id = emote_category_join.emoteId " +
        "INNER JOIN categories ON emote_category_join.categoryId = categories.id " +
        "WHERE categories.categoryName = :categoryName")
    fun getEmotesByCategory(categoryName: String): Flow<List<Emote>>

    @Query("SELECT COUNT(*) FROM emotes WHERE name = :name")
    fun countEmotesByName(name: String): Int

    @Query("DELETE FROM emotes")
    fun deleteAllEmotes()
}
