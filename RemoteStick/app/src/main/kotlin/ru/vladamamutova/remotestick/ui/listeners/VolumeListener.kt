package ru.vladamamutova.remotestick.ui.listeners

interface VolumeListener {
    fun beforeVolumeChanged(value: Int)
    fun volumeChanged(value: Int, step: Int)
    fun afterVolumeChanged(value: Int)
}
