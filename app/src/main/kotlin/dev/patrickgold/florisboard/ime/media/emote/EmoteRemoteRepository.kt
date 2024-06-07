package dev.patrickgold.florisboard.ime.media.emote

import android.util.Log

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

object EmoteRemoteRepository {
    private val TAG = "EmoteRemoteRepository"
    private val firestoreDb = FirebaseFirestore.getInstance()
    private val repositoryRemoteScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Fetches all emotes from Firestore and exposes them as StateFlow
    fun getAllEmotes(): StateFlow<List<Emote>> {
        return flow {
            val snapshot = firestoreDb.collection("emotes").get().await()
            val emotes = snapshot.toObjects(Emote::class.java)
            emit(emotes)
        }.stateIn(repositoryRemoteScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), emptyList())
    }

    // Fetches emotes by category from Firestore and exposes them as StateFlow
    fun getEmotesByCategory(category: String): StateFlow<List<Emote>> {
        val categoryNameLower = category.lowercase()
        return flow {
            try {
                Log.d(TAG, "Attempting to fetch emotes for category: $categoryNameLower")
                val snapshot = firestoreDb.collection("emotes")
                    .whereArrayContains("categories", categoryNameLower)
                    .get()
                    .await()
                val emotes = snapshot.toObjects(Emote::class.java)
                Log.d(TAG, "Fetched ${emotes.size} emotes for category: $categoryNameLower")
                emit(emotes)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching emotes by category", e)
                emit(emptyList<Emote>())  // Emitting empty list on error
            }
        }.stateIn(repositoryRemoteScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), emptyList())
    }
}
