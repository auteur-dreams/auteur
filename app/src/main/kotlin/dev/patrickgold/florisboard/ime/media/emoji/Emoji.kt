/*
 * Copyright (C) 2022 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.media.emoji

import androidx.compose.runtime.Composable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.keyboard.AbstractKeyData
import dev.patrickgold.florisboard.ime.keyboard.ComputingEvaluator
import dev.patrickgold.florisboard.ime.keyboard.KeyData
import dev.patrickgold.florisboard.ime.popup.PopupSet
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.key.KeyType
import dev.patrickgold.florisboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries
import java.util.stream.IntStream
import kotlin.streams.toList

enum class EmojiSkinTone(val id: Int) {
    DEFAULT(0x0),
    LIGHT_SKIN_TONE(0x1F3FB),
    MEDIUM_LIGHT_SKIN_TONE(0x1F3FC),
    MEDIUM_SKIN_TONE(0x1F3FD),
    MEDIUM_DARK_SKIN_TONE(0x1F3FE),
    DARK_SKIN_TONE(0x1F3FF);

    companion object {
        @Composable
        fun listEntries() = listPrefEntries {
            entry(
                key = DEFAULT,
                label = stringRes(
                    R.string.enum__emoji_skin_tone__default,
                    "emoji" to "\uD83D\uDC4B" // 👋
                ),
            )
            entry(
                key = LIGHT_SKIN_TONE,
                label = stringRes(
                    R.string.enum__emoji_skin_tone__light_skin_tone,
                    "emoji" to "\uD83D\uDC4B\uD83C\uDFFB" // 👋🏻
                ),
            )
            entry(
                key = MEDIUM_LIGHT_SKIN_TONE,
                label = stringRes(
                    R.string.enum__emoji_skin_tone__medium_light_skin_tone,
                    "emoji" to "\uD83D\uDC4B\uD83C\uDFFC" // 👋🏼
                ),
            )
            entry(
                key = MEDIUM_SKIN_TONE,
                label = stringRes(
                    R.string.enum__emoji_skin_tone__medium_skin_tone,
                    "emoji" to "\uD83D\uDC4B\uD83C\uDFFD" // 👋🏽
                ),
            )
            entry(
                key = MEDIUM_DARK_SKIN_TONE,
                label = stringRes(
                    R.string.enum__emoji_skin_tone__medium_dark_skin_tone,
                    "emoji" to "\uD83D\uDC4B\uD83C\uDFFE" // 👋🏾
                ),
            )
            entry(
                key = DARK_SKIN_TONE,
                label = stringRes(
                    R.string.enum__emoji_skin_tone__dark_skin_tone,
                    "emoji" to "\uD83D\uDC4B\uD83C\uDFFF" // 👋🏿
                ),
            )
        }
    }
}

enum class EmojiHairStyle(val id: Int) {
    DEFAULT(0x0),
    RED_HAIR(0x1F9B0),
    CURLY_HAIR(0x1F9B1),
    WHITE_HAIR(0x1F9B2),
    BALD(0x1F9B3);
}

@Entity
data class Emoji(
    @PrimaryKey val value: String,
    val name: String,
    val keywords: List<String>
) : KeyData {
    @Ignore
    override val type: KeyType = KeyType.CHARACTER
    @Ignore
    override val code: Int = KeyCode.UNSPECIFIED
    @Ignore
    override val label: String = value
    @Ignore
    override val groupId: Int = 0

    @Ignore
    override val popup: PopupSet<AbstractKeyData>? = null

    @Ignore
    val skinTone: EmojiSkinTone = EmojiSkinTone.entries.firstOrNull { value.codePoints().toList().contains(it.id) }
        ?: EmojiSkinTone.DEFAULT

    @Ignore
    val hairStyle: EmojiHairStyle = EmojiHairStyle.entries.firstOrNull { value.codePoints().toList().contains(it.id) }
        ?: EmojiHairStyle.DEFAULT

    override fun compute(evaluator: ComputingEvaluator): KeyData = this

    override fun asString(isForDisplay: Boolean): String = value

    override fun toString(): String = "Emoji { value=$value, name=$name, keywords=$keywords }"
}
