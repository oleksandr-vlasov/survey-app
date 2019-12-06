package com.github.iojjj.survey.core.text

import android.text.Editable
import android.text.TextWatcher

/**
 * Empty implementation of [TextWatcher].
 */
abstract class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable) {
        /* no-op */
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        /* no-op */
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        /* no-op */
    }

}