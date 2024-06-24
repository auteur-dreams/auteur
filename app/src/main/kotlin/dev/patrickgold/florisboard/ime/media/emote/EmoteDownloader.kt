package dev.patrickgold.florisboard.ime.media.emote

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EmoteDownloader(private val emoteRepository: EmoteRepository) {
    private val TAG = "EmoteDownloader"
    private val ONE_MEGABYTE: Long = 1024 * 1024

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun downloadEmote(name: String, drawableName: String, context: Context, categories: List<String>?, fileType: String): Emote? {
        val emote = emoteRepository.getLocalEmoteByName(name)
        if (emote?.localPath != null && File(emote.localPath).exists()) {
            Log.d(TAG, "Emote already downloaded: ${emote.name}")
            return emote
        } else {
            try {
                val imageRef = firebaseStorage.getReference("$fileType/$drawableName.$fileType")
                Log.d(TAG, "Attempting to download emote: $name from path: ${imageRef.path}")
                val bytes = imageRef.getBytes(ONE_MEGABYTE).await()
                val localPath = saveToLocalFile(bytes, drawableName, context)
                Log.d(TAG, "Emote saved locally at path: $localPath")
                val newEmote = Emote(name = name, drawableName = drawableName, categories = categories, localPath = localPath)
                emoteRepository.insertOrUpdateEmote(newEmote)
                Log.d(TAG, "Emote inserted or updated in repository: $name")
                return newEmote
            } catch (e: Exception) {
                Log.e(TAG, "Failed to download emote: $name", e)
                return null
            }
        }
    }

    private fun saveToLocalFile(bytes: ByteArray, filename: String, context: Context): String {
        val file = File(context.filesDir, filename)
        try {
            FileOutputStream(file).use { it.write(bytes) }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving file: ${file.absolutePath}", e)
            throw e
        }
        return file.absolutePath
    }

}
