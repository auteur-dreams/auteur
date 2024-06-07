package dev.patrickgold.florisboard.ime.media.emote

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object EmoteRepository {
    private val TAG = "EmoteRepository"
    private lateinit var emoteDao : EmoteDao
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context) {
        emoteDao = EmoteLocalUserDatabase.getDatabase(context).emoteDao()
    }

    fun getAllLocalEmotes(): Flow<List<Emote>> = EmoteLocalRepository.getAllLocalEmotes()

    suspend fun getRemoteEmotesByCategory(category: String): StateFlow<List<Emote>> = EmoteRemoteRepository.getEmotesByCategory(category)

    suspend fun insert(emote: Emote) {
        repositoryScope.launch {
            EmoteLocalRepository.insert(emote)
        }
    }

    suspend fun update(emote: Emote) {
        repositoryScope.launch {
            EmoteLocalRepository.update(emote)
        }
    }

    suspend fun delete(emote: Emote) {
        repositoryScope.launch {
            EmoteLocalRepository.delete(emote)
        }
    }

    suspend fun getLocalEmoteByName(name: String): Emote? {
        return EmoteLocalRepository.getEmoteByNameSync(name)
    }

    fun getEmotesByCategory(categoryName: String): Flow<List<Emote>> = emoteDao.getEmotesByCategory(categoryName)

    suspend fun deleteAllEmotes() {
        repositoryScope.launch {
            EmoteLocalRepository.deleteAllEmotes()
        }
    }

    suspend fun insertOrUpdateEmote(emote: Emote) {
        repositoryScope.launch {
            EmoteLocalRepository.insertOrUpdateEmote(emote)
        }
    }

    // DEBUG
    fun printAllLocalEmotes() { EmoteLocalRepository.printAllLocalEmoteMetaData() }
}
