package dev.patrickgold.florisboard.ime.media

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.patrickgold.florisboard.app.apptheme.gray300
import dev.patrickgold.florisboard.ime.media.emoji.EmojiData
import dev.patrickgold.florisboard.ime.media.emoji.EmojiScreen
import dev.patrickgold.florisboard.ime.media.emote.EmoteScreen
import dev.patrickgold.florisboard.ime.media.gif.GifScreen

@Composable
fun MediaScreen(
    fullEmojiMappings: EmojiData,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(MediaTab.EMOJI) }
    val tabs = MediaTab.entries.toTypedArray()

    Column(modifier = modifier) {
        CustomTabRow(
            selectedTabIndex = selectedTab.ordinal,
            tabs = tabs,
            onTabSelected = { selectedTab = it }
        )

        when (selectedTab) {
            MediaTab.EMOJI -> EmojiScreen(fullEmojiMappings)
            MediaTab.EMOTE -> EmoteScreen()
            MediaTab.GIF -> GifScreen()
        }
    }
}

@Composable
fun CustomTabRow(
    selectedTabIndex: Int,
    tabs: Array<MediaTab>,
    onTabSelected: (MediaTab) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.Gray,
            indicator = {},
            divider = {},
            modifier = Modifier
                .height(28.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selectedTabIndex == index) gray300
                            else Color.Transparent
                        ),
                    text = {
                        Text(
                            tab.title,
                            fontWeight = FontWeight.Bold,
                            color = if(selectedTabIndex == index) Color.Black else Color.Gray
                        )
                    }
                )
            }
        }
    }
}

enum class MediaTab(val title: String) {
    EMOJI("Emoji"),
    EMOTE("Emote"),
    GIF("GIF")
}
