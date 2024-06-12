package dev.patrickgold.florisboard.ime.input

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import android.widget.TextView

class RichInputConnection(parent: InputMethodService) {
    private var baseIC: InputConnection? = null
    private var emojiSearchIC: EditTextInputConnection? = null
    private var shouldUseEmojiSearch: Boolean = false

    fun setEmojiSearchIC(textView : TextView) {
        if (textView == null) return;
        emojiSearchIC = EditTextInputConnection(textView);
    }

    fun setShouldUseEmojiSearchIC(useOtherIC: Boolean) {
        shouldUseEmojiSearch = useOtherIC
    }

    fun isConnected(): Boolean {
        return getIC() != null
    }

    private fun getIC(): InputConnection? {
        return if (shouldUseEmojiSearch) emojiSearchIC ?: baseIC else baseIC
    }

    fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        return getIC()?.commitText(text, newCursorPosition) ?: false
    }

    fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        return getIC()?.deleteSurroundingText(beforeLength, afterLength) ?: false
    }


}
