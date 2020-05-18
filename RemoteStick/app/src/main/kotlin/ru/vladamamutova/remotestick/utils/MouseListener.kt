package ru.vladamamutova.remotestick.utils

interface MouseActionListener {
    fun onLeftClick()
    fun onRightClick()
    fun onDoubleClick()
    fun onMove(dx: Int, dy: Int)
}
