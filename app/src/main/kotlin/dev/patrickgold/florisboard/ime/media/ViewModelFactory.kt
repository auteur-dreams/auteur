package dev.patrickgold.florisboard.ime.media

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.patrickgold.florisboard.ime.media.emoji.EmojiViewModel
import dev.patrickgold.florisboard.ime.media.emote.EmoteViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Main App
            modelClass.isAssignableFrom(EmojiViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                EmojiViewModel(application) as T
            }
            modelClass.isAssignableFrom(EmoteViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                EmoteViewModel(application) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
