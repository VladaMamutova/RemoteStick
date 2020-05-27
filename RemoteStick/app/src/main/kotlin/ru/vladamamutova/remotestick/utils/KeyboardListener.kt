package ru.vladamamutova.remotestick.utils

import ru.vladamamutova.remotestick.plugins.SpecialKey

interface KeyboardListener {
    fun onKeyPress(key: Char): Boolean
    fun onSpecialKeyPress(specialKey: SpecialKey)
}
