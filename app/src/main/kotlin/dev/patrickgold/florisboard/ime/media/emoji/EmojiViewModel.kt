package dev.patrickgold.florisboard.ime.media.emoji

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EmojiViewModel(application: Application) : AndroidViewModel(application) {

    private val emojiDao: EmojiDao = EmojiLocalUserDatabase.getDatabase(application).emojiDao()
    private val _searchResults = MutableLiveData<List<Emoji>>()
    val searchResults: LiveData<List<Emoji>> = _searchResults

    fun searchEmojis(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = emojiDao.searchEmojis("%$query%")
            _searchResults.postValue(results)
        }
    }
}
