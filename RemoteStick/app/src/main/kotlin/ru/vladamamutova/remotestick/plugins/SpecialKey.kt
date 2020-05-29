package ru.vladamamutova.remotestick.plugins

enum class SpecialKey {
    BACKSPACE,
    ENTER,
    WIN,
    CTRL,
    SHIFT,
    ALT,
    ESC,
    TAB,
    INSERT,
    DELETE,
    HOME,
    END,
    PAGE_UP,
    PAGE_DOWN,
    UP,
    LEFT,
    RIGHT,
    DOWN,
    PRINT_SCREEN,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12;

    fun isModifier(): Boolean {
        return this == CTRL || this == SHIFT || this == ALT || this == WIN
    }

    companion object {
        private val values = values()
        fun fromString(value: String) = values.firstOrNull { it.name == value }
    }
}
