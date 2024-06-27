package dev.patrickgold.florisboard.ime.media.gif

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.patrickgold.florisboard.ime.media.ViewModelFactory
import dev.patrickgold.florisboard.ime.media.emote.EmoteViewModel

@Composable
fun GifScreen(application: Application, modifier: Modifier = Modifier) {
    val gifViewModel: GifViewModel = viewModel(
        factory = ViewModelFactory(application)
    )
    val gifs by gifViewModel.gifs.collectAsState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(gifs) { gifUrl ->
                AsyncImage(
                    model = gifUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        gifViewModel.loadGifs()
    }
}
