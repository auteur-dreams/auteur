package dev.patrickgold.florisboard.ime.media.emote

import android.app.Application
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.patrickgold.florisboard.FlorisImeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class EmoteViewModel(application: Application) : AndroidViewModel(application) {
    private val emoteRepository = EmoteRepository
    private val emoteDownloader = EmoteDownloader(emoteRepository)
    private val _emotes = MutableStateFlow<List<Emote>>(emptyList())
    val emotes: StateFlow<List<Emote>> = _emotes.asStateFlow()

    init {
        // Load local emotes when the ViewModel is created
        loadLocalEmotes()
    }

    fun getRemoteEmotesByCategory(categoryName: String, context: Context) {
        viewModelScope.launch {
            EmoteRepository.getRemoteEmotesByCategory(categoryName).collect { fetchedEmotes ->
                fetchedEmotes.forEach { emote ->
                    handleEmoteDownload(emote, context)
                }
                loadLocalEmotes() // Update local emotes after downloading
            }
        }
    }

    private fun handleEmoteDownload(emote : Emote, context : Context){
        viewModelScope.launch(Dispatchers.IO) {
            val result = emoteDownloader.downloadEmote(emote.name, emote.drawableName, context, emote.categories, emote.fileType)
            if(result != null) {
                Log.d(TAG, "Successfully downloaded emote: ${result.name}, local path: ${result.localPath}")
            } else {
                Log.d(TAG, "Failed to download emote: ${emote.name}")
            }
        }
    }

    fun onEmoteClick(emote: Emote) {
        if (emote.localPath.isNotBlank()) {
            val file = File(emote.localPath)
            Log.d(TAG, "File exists: ${file.exists()}, File path: ${file.absolutePath}")
            val imageUri = FileProvider.getUriForFile(
                getApplication(),
                getApplication<Application>().packageName + ".provider",
                file
            )
            Log.d(TAG, "URI: $imageUri")
            sendImageToInputField(imageUri)
        }
    }

    private fun sendImageToInputField(imageUri: Uri) {
        val description = "Emote Image" // Content description
        val mimeType = MIME_TYPE_PNG // Assumes PNG only
        val inputConnection = FlorisImeService.currentInputConnection()
        val editorInfo = FlorisImeService.currentInputEditorInfo()
        val flags: Int
        if (Build.VERSION.SDK_INT >= 25) {
            flags = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
        } else {
            flags = 0
            try {
                getApplication<Application>().grantUriPermission(
                    editorInfo?.packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                Log.e(TAG, "grantUriPermission failed packageName=${editorInfo?.packageName} contentUri=$imageUri", e)
            }
        }

        if (inputConnection == null || editorInfo == null) {
            Log.e(TAG, "Input connection or editor info is null")
            return
        }

        Log.d(TAG, "Preparing to commit content. URI: $imageUri")
        Log.d(TAG, "Target package: ${editorInfo.packageName}")
        Log.d(TAG, "MIME types supported by target: ${EditorInfoCompat.getContentMimeTypes(editorInfo).contentToString()}")

        val clipDescription = ClipDescription(description, arrayOf(mimeType))
        val inputContentInfo = InputContentInfoCompat(imageUri, clipDescription, null)
        try {
            val result = InputConnectionCompat.commitContent(
                inputConnection,
                editorInfo,
                inputContentInfo,
                flags,
                null
            )
            if (result) {
                Log.d(TAG, "Image successfully committed.")
            } else {
                Log.e(TAG, "Failed to commit image.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during commitContent: ${e.message}", e)
        }
    }

    private fun loadLocalEmotes() {
        viewModelScope.launch {
            EmoteRepository.getAllLocalEmotes().collect { localEmotes ->
                _emotes.value = localEmotes
            }
        }
    }

    companion object {
        private const val TAG = "EmoteViewModel"
        private const val MIME_TYPE_PNG = "image/png"
    }
}
