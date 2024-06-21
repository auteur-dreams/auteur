package dev.patrickgold.florisboard.ime.media.emote

import android.app.Application
import android.content.Context
import android.widget.ImageButton
import android.widget.ImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.imageLoader
import coil.request.ImageRequest
import dev.patrickgold.florisboard.ime.media.ViewModelFactory


@Composable
fun EmoteScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val emoteViewModel: EmoteViewModel = viewModel(
        factory = ViewModelFactory(application)
    )
    val emotes by emoteViewModel.emotes.collectAsState()

    // Download some base default emotes for the keyboard
    emoteViewModel.handleEmoteDownload( someEMOTE, context)


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
                EmoteButton(emote, context) { clickedEmote ->
                    emoteViewModel.onEmoteClick(clickedEmote)
                }
            }
        }
    }
}

@Composable
fun EmoteButton(emote: Emote, context: Context, onClick: (Emote) -> Unit) {
    val emoteButton = ImageButton(context)
    emoteButton.setContentDescription(emote.name)
    emoteButton.tag = emote.categories.toString()
    emoteButton.background = null
    emoteButton.scaleType = ImageView.ScaleType.FIT_CENTER
    emoteButton.setPadding(0, 0, 0, 0)

    // Asynchronously load image using Coil
    val imageLoader = context.imageLoader
    val request = ImageRequest.Builder(context)
        .data(if (emote.localPath.isNotBlank()) emote.localPath else emote.remoteUrl)
        .target { drawable ->
            emoteButton.setImageDrawable(drawable)
        }
        .build()
    imageLoader.enqueue(request)
}
