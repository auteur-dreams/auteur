package dev.patrickgold.florisboard.ime.media.emote

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.patrickgold.florisboard.ime.media.ViewModelFactory


@Composable
fun EmoteScreen(application: Application, modifier: Modifier = Modifier) {
    val emoteViewModel: EmoteViewModel = viewModel(
        factory = ViewModelFactory(application)
    )
    val emotes by emoteViewModel.emotes.collectAsState()
    val context = LocalContext.current

    Log.d("EmoteScreen", "emotes size: ${emotes.size}")

    // Download some base default emotes for the keyboard
    // Call getRemoteEmotesByCategory only once when the composable is first composed
    LaunchedEffect(Unit) {
        emoteViewModel.getRemoteEmotesByCategory("all_emotes", context)
    }

    LaunchedEffect(emotes) {
        Log.d("EmoteScreen", "emotes size after get all_emotes: ${emotes.size}")
    }

    if (emotes.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Emotes Available")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(emotes) { emote ->
                EmoteButton(emote) { clickedEmote ->
                    emoteViewModel.onEmoteClick(clickedEmote)
                }
            }
        }
    }
}

@Composable
fun EmoteButton(emote: Emote, onClick: (Emote) -> Unit) {
    Log.d("EmoteButton", "Rendering emote: ${emote.name}, remote URL: ${emote.remoteUrl}, Local Path: ${emote.localPath}")
    AsyncImage(
        model = emote.localPath.takeIf { it.isNotBlank() } ?: emote.remoteUrl,
        contentDescription = emote.name,
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick(emote) }
    )
}
