package ru.vladamamutova.remotestick.utils

interface VolumeListener {
    fun beforeVolumeChanged(value: Int)
    fun volumeChanged(value: Int, step: Int)
    fun afterVolumeChanged(value: Int)
}
