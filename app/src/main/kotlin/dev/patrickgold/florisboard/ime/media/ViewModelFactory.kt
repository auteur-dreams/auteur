package dev.patrickgold.florisboard.ime.media

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.patrickgold.florisboard.ime.media.emoji.EmojiViewModel
// import dev.patrickgold.florisboard.ime.media.emoji.EmojiViewModel
import dev.patrickgold.florisboard.ime.media.emote.EmoteViewModel
import dev.patrickgold.florisboard.ime.media.gif.GifViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Emoji View Model
            modelClass.isAssignableFrom(EmojiViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                EmojiViewModel(application) as T
            }

            // Emote View Model
            modelClass.isAssignableFrom(EmoteViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                EmoteViewModel(application) as T
            }

            // GIF View Model
            modelClass.isAssignableFrom(GifViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                GifViewModel(application) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
