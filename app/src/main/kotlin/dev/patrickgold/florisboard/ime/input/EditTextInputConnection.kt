package dev.patrickgold.florisboard.ime.input

import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.style.SuggestionSpan
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.CorrectionInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.TextView

/**
 * Source: https://stackoverflow.com/a/39460124
 */
class EditTextInputConnection(private val mTextView: TextView) : BaseInputConnection(mTextView, true) {
    // Keeps track of nested begin/end batch edit to ensure this connection always has a
    // balanced impact on its associated TextView.
    // A negative value means that this connection has been finished by the InputMethodManager.
    private var mBatchEditNesting = 0

    override fun getEditable(): Editable? {
        return mTextView.editableText
    }

    override fun beginBatchEdit(): Boolean {
        synchronized(this) {
            if (mBatchEditNesting >= 0) {
                mTextView.beginBatchEdit()
                mBatchEditNesting++
                return true
            }
        }
        return false
    }

    override fun endBatchEdit(): Boolean {
        synchronized(this) {
            if (mBatchEditNesting > 0) {
                // When the connection is reset by the InputMethodManager and reportFinish
                // is called, some endBatchEdit calls may still be asynchronously received from the
                // IME. Do not take these into account, thus ensuring that this IC's final
                // contribution to mTextView's nested batch edit count is zero.
                mTextView.endBatchEdit()
                mBatchEditNesting--
                return true
            }
        }
        return false
    }

    override fun clearMetaKeyStates(states: Int): Boolean {
        val content = editable ?: return false
        val kl = mTextView.keyListener
        kl?.clearMetaKeyState(mTextView, content, states)
        return true
    }

    override fun commitCompletion(text: CompletionInfo): Boolean {
        mTextView.beginBatchEdit()
        mTextView.onCommitCompletion(text)
        mTextView.endBatchEdit()
        return true
    }

    /**
     * Calls the [TextView.onCommitCorrection] method of the associated TextView.
     */
    override fun commitCorrection(correctionInfo: CorrectionInfo): Boolean {
        mTextView.beginBatchEdit()
        mTextView.onCommitCorrection(correctionInfo)
        mTextView.endBatchEdit()
        return true
    }

    override fun performEditorAction(actionCode: Int): Boolean {
        mTextView.onEditorAction(actionCode)
        return true
    }

    override fun performContextMenuAction(id: Int): Boolean {
        mTextView.beginBatchEdit()
        mTextView.onTextContextMenuItem(id)
        mTextView.endBatchEdit()
        return true
    }

    override fun getExtractedText(request: ExtractedTextRequest?, flags: Int): ExtractedText? {
        val et = ExtractedText()
        return if (mTextView.extractText(request, et)) {
            if (flags and GET_EXTRACTED_TEXT_MONITOR != 0) {
                // mTextView.setExtracting(request)
            }
            et
        } else {
            null
        }
    }

    override fun performPrivateCommand(action: String?, data: Bundle?): Boolean {
        mTextView.onPrivateIMECommand(action, data)
        return true
    }

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        if (mTextView == null) {
            return super.commitText(text, newCursorPosition)
        }
        if (text is Spanned) {
            val spans = text.getSpans(0, text.length, SuggestionSpan::class.java)
            // mIMM.registerSuggestionSpansForNotification(spans)
        }

        // mTextView.resetErrorChangedFlag()
        val success = super.commitText(text, newCursorPosition)
        // mTextView.hideErrorIfUnchanged()

        return success
    }

    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        // It is possible that any other bit is used as a valid flag in a future release.
        // We should reject the entire request in such a case.
        val KNOWN_FLAGS_MASK = InputConnection.CURSOR_UPDATE_IMMEDIATE or InputConnection.CURSOR_UPDATE_MONITOR
        val unknownFlags = cursorUpdateMode and KNOWN_FLAGS_MASK.inv()
        if (unknownFlags != 0) {
            return false
        }

        return false
    }
}
