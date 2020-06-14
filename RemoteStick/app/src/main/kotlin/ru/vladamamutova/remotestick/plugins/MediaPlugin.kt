package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketType

class MediaPlugin(owner: PluginMediator) : Plugin(owner) {
    private enum class Volume(val value: String) {
        CHANGE("change"),
        MUTE("mute");

        companion object {
            const val name = "volume"
            const val changeValue = "value"
        }
    }

    private enum class Playback(val value: String) {
        PLAY_PAUSE("play/pause"),
        NEXT("next"),
        PREVIOUS("previous"),
        STOP("stop");

        companion object {
            const val name = "playback"
        }
    }

    override val type: PacketType
        get() = PacketType.MEDIA

    var muteChecked: Boolean = false; private set
    private var volume: Int = 0

    private fun createPacket(playback: Playback): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Playback.name, playback.value)
        })
    }

    fun changeVolume(value: Int, step: Int) {
        val volumeDifference = (value - volume) / step
        if (volumeDifference != 0) {
            owner.sendPacket(createPacket(JsonObject().apply {
                addProperty(Volume.name, Volume.CHANGE.value)
                addProperty(Volume.changeValue, volumeDifference * step)
            }))
            // Если изменяется громкость, беззвучный режим выключается.
            if (muteChecked) {
                muteChecked = false
            }
        }
        volume = value
    }

    fun toggleMute() {
        muteChecked = !muteChecked
        owner.sendPacket(createPacket(JsonObject().apply {
            addProperty(Volume.name, Volume.MUTE.value)
        }))
    }

    fun playPause() = owner.sendPacket(createPacket(Playback.PLAY_PAUSE))
    fun nextTrack() = owner.sendPacket(createPacket(Playback.NEXT))
    fun previousTrack() = owner.sendPacket(createPacket(Playback.PREVIOUS))
    fun stop() = owner.sendPacket(createPacket(Playback.STOP))
}
