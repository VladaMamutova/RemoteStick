package ru.vladamamutova.remotestick.ui.listeners

import ru.vladamamutova.remotestick.plugins.SpecialKey

interface KeyboardListener {
    fun onKeyPress(key: Char): Boolean
    fun onSpecialKeyPress(specialKey: SpecialKey)
}
