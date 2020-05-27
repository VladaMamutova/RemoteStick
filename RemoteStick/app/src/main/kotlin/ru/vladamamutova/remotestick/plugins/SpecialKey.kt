package ru.vladamamutova.remotestick.plugins

import android.view.Window

enum class SpecialKey {
    BACKSPACE,
    ENTER,
    WIN,
    CTRL,
    SHIFT,
    ALT;

    fun isModifier(): Boolean {
        return this == CTRL || this == SHIFT || this == ALT || this == WIN
    }
}
