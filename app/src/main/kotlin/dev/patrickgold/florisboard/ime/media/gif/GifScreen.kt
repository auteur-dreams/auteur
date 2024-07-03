package dev.patrickgold.florisboard.ime.media.gif

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import dev.patrickgold.florisboard.ime.media.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun GifScreen(application: Application, modifier: Modifier = Modifier) {
    val gifViewModel: GifViewModel = viewModel(
        factory = ViewModelFactory(application)
    )
    val gifs by gifViewModel.gifs.collectAsState()
    val trendingSearchTerms by gifViewModel.trendingSearchTerms.collectAsState()
    val categories by gifViewModel.categories.collectAsState()
    val context = LocalContext.current
    var searchTerm by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val gifEnabledLoader = ImageLoader.Builder(context)
        .components {
            if ( SDK_INT >= 28 ) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    Column(modifier = modifier.fillMaxWidth()) {
        // GIF Search Field
        TextField(
            value = searchTerm,
            onValueChange = { searchTerm = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text("Search GIFs") }
        )

        // Categories
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(categories) { category ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray)
                        .clickable {
                            coroutineScope.launch {
                                gifViewModel.loadGifs(category)
                            }
                        }
                ) {
                    AsyncImage(
                        model = gifs.randomOrNull(),
                        imageLoader = gifEnabledLoader,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize()
                    )
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        gifViewModel.loadTrendingSearchTerms()
        gifViewModel.loadCategories()
    }
}
