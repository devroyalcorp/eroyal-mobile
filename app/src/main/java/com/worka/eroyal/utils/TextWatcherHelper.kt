package com.worka.eroyal.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 08/03/20.
 */
class TextWatcherHelper(var onValidate:() -> Unit) : TextWatcher{
    override fun afterTextChanged(s: Editable?) {
       onValidate.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onValidate.invoke()
    }
}
