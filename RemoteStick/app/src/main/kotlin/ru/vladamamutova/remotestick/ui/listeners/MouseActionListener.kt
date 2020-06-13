package ru.vladamamutova.remotestick.ui.listeners

interface MouseActionListener {
    fun onLeftClick()
    fun onMiddleClick()
    fun onRightClick()
    fun onLeftDown()
    fun onLeftUp()
    fun onMove(dx: Int, dy: Int)
    fun onScroll(dy: Int)
}
