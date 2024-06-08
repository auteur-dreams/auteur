package dev.patrickgold.florisboard.ime.media.emoji

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.patrickgold.florisboard.ime.media.emoji.Emoji

@Dao
interface EmojiDao {
    @Insert
    fun insert(emote: Emoji)

    @Update
    fun update(emote: Emoji)

    @Delete
    fun delete(emote: Emoji)

    @Query("SELECT * FROM emoji WHERE name LIKE :query OR keywords LIKE :query")
    fun searchEmojis(query: String): List<Emoji>

    @Insert
    suspend fun insertAll(emojis: List<Emoji>)
}
