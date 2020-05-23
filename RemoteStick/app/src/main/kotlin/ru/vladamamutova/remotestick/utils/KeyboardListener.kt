package ru.vladamamutova.remotestick.utils

import ru.vladamamutova.remotestick.plugins.SpecialKey

interface KeyboardListener {
    fun sendSymbol(symbol: Char)
    fun sendSpecialKeys(specialKeys: Array<SpecialKey>)
    fun sendKeys(specialKeys: Array<SpecialKey>, symbol: Char)
}
