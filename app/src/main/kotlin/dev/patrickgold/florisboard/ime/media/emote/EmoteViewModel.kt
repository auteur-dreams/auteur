package dev.patrickgold.florisboard.ime.media.emote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class EmoteViewModel(application: Application) : AndroidViewModel(application) {
    private val _emotes = MutableStateFlow<List<Emote>>(emptyList())
    val emotes: StateFlow<List<Emote>> = _emotes.asStateFlow()

    fun getRemoteEmotesByCategory(categoryName: String) {
        viewModelScope.launch {
            EmoteRepository.getRemoteEmotesByCategory(categoryName).collect { fetchedEmotes ->
                _emotes.value = fetchedEmotes
            }
        }
    }


    // Include other category-related data management methods
}
