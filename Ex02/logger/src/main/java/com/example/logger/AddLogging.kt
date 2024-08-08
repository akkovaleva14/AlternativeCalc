package com.example.logger

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.MutableLiveData

fun EditText.addLogging(name: String, liveData: MutableLiveData<String>) {
    val textWatcher = object : TextWatcher {
        private var previousText: String? = null

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            previousText = s?.toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newText = s?.toString()
            if (previousText != newText) {
                Logger.i(name, "Previous value: $previousText, New value: $newText")
                liveData.value = newText
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // No additional action needed after text is changed
        }
    }
    this.addTextChangedListener(textWatcher)
}
