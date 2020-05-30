package ru.vladamamutova.remotestick.utils

interface MouseActionListener {
    fun onLeftClick()
    fun onMiddleClick()
    fun onRightClick()
    fun onLeftDown()
    fun onLeftUp()
    fun onMove(dx: Int, dy: Int)
    fun onScroll(dy: Int)
}
