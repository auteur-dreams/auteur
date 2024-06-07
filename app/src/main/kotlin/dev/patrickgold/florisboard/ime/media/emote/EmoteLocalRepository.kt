package dev.patrickgold.florisboard.ime.media.emote

import android.app.Application
import android.util.Log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object EmoteLocalRepository {
    private lateinit var emoteDao: EmoteDao
    private val TAG = "EmoteLocalRepository"
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(application: Application) {
        val db = EmoteLocalUserDatabase.getDatabase(application)
        emoteDao = db.emoteDao()
    }

    // Fetches all emotes that are stored locally in the database
    fun getAllLocalEmotes(): Flow<List<Emote>> = emoteDao.getAllLocalEmotes()

    fun getEmotesByCategory(categoryName: String): Flow<List<Emote>> = emoteDao.getEmotesByCategory(categoryName)

    // Get a specific emote by its name, synchronously
    fun getEmoteByNameSync(name: String): Emote = emoteDao.getEmoteByNameSync(name)

    fun deleteAllEmotes() {
        repositoryScope.launch {
            emoteDao.deleteAllEmotes()
            Log.d(TAG, "All emotes have been deleted from the database.")
        }
    }

    // Insert an emote into the database
    fun insert(emote: Emote) {
        repositoryScope.launch {
            val count = emoteDao.countEmotesByName(emote.name)
            if (count == 0) {
                emote.isDownloaded = true // Set downloaded before inserting
                emoteDao.insert(emote)
                Log.d(TAG, "Emote inserted: ${emote.name}, downloaded status: ${emote.isDownloaded}")
            } else {
                Log.d(TAG, "Insertion skipped, Emote already exists: ${emote.name}")
            }
        }
    }

    // Update an existing emote in the database
    fun update(emote: Emote) {
        repositoryScope.launch {
            emoteDao.update(emote)
        }
    }

    // Delete an emote from the database
    fun delete(emote: Emote) {
        repositoryScope.launch {
            emoteDao.delete(emote)
        }
    }

    fun insertOrUpdateEmote(emote: Emote) {
        repositoryScope.launch {
            val existingEmote = getEmoteByNameSync(emote.name)
            if (existingEmote == null) {
                emoteDao.insert(emote)
            } else {
                val updatedEmote = existingEmote.copy(
                    drawableName = emote.drawableName,
                    categories = emote.categories,
                    remoteUrl = emote.remoteUrl,
                    localPath = emote.localPath,
                    isDownloaded = true
                )
                emoteDao.update(updatedEmote)
            }
        }
    }

    fun printAllLocalEmoteMetaData() {
        val allLocalEmotes = emoteDao.getAllLocalEmotesSync()
        for(emote in allLocalEmotes) {
            Log.d(TAG, "Emote: " + emote.name
                + ", Drawable Name: " + emote.drawableName
                + ", Categories: " + emote.categories
                + ", Remote URL: " + emote.remoteUrl
                + ", Local Path: " + emote.localPath
                + ", Is Downloaded: " + emote.isDownloaded)
        }
    }
}
