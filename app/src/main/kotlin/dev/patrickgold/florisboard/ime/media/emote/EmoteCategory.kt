package dev.patrickgold.florisboard.ime.media.emote

import dev.patrickgold.florisboard.R

enum class EmoteCategory(val id: Int, val displayName: String, val iconRes: Int) {
    PEPE(1, "Pepe", R.drawable.pepe_unamused),
    FUNNY(2, "Funny", R.drawable.kekw),
    SAD(3, "Sad", R.drawable.pepe_hands),
    ANGRY(4, "Angry", R.drawable.wojak_crying_angry),
    DUMB(5, "Dumb", R.drawable.pepe_feels_dumb),
    MISC(6, "Misc", R.drawable.please_no);

    fun toCategory() = Category(id, displayName, iconRes)
}
