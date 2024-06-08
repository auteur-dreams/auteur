package dev.patrickgold.florisboard.lib.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.patrickgold.florisboard.ime.media.emoji.EmojiHairStyle
import dev.patrickgold.florisboard.ime.media.emoji.EmojiSkinTone

object Converters {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromEmojiSkinTone(value: Int): EmojiSkinTone {
        return EmojiSkinTone.values().first { it.id == value }
    }

    @TypeConverter
    fun toEmojiSkinTone(skinTone: EmojiSkinTone): Int {
        return skinTone.id
    }

    @TypeConverter
    fun fromEmojiHairStyle(value: Int): EmojiHairStyle {
        return EmojiHairStyle.values().first { it.id == value }
    }

    @TypeConverter
    fun toEmojiHairStyle(hairStyle: EmojiHairStyle): Int {
        return hairStyle.id
    }
}
