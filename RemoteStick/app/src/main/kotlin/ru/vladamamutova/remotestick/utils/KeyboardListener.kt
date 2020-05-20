package ru.vladamamutova.remotestick.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import ru.vladamamutova.remotestick.plugins.KeyboardPlugin

class KeyboardListener(private var keyboardPlugin: KeyboardPlugin) : TextWatcher {

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        Log.d("TAG", charSequence.toString())
        Log.d("TAG", "start = $start, before = $before, count = $count")
        keyboardPlugin.sendKey(charSequence[before])
    }

}