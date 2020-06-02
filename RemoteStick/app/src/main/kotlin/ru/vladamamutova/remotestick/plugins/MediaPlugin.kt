package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes

class MediaPlugin(owner: PluginMediator) : Plugin(owner) {
    private enum class Volume(val value: String) {
        UP("up"),
        DOWN("down"),
        MUTE("mute");

        companion object {
            const val name = "volume"
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

    override val type: PacketTypes
        get() = PacketTypes.MEDIA

    private var mute: Boolean = false
    private var volume: Int = 0

    private fun createPacket(volume: Volume): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Volume.name, volume.value)
        })
    }

    private fun createPacket(playback: Playback): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Playback.name, playback.value)
        })
    }

    fun volumeUp() = owner.sendPacket(createPacket(Volume.UP))
    fun volumeDown() = owner.sendPacket(createPacket(Volume.DOWN))
    fun volumeMute(): Boolean {
        owner.sendPacket(createPacket(Volume.MUTE))
        mute = !mute
        return mute
    }

    fun playPause() = owner.sendPacket(createPacket(Playback.PLAY_PAUSE))
    fun nextTrack() = owner.sendPacket(createPacket(Playback.NEXT))
    fun previousTrack() = owner.sendPacket(createPacket(Playback.PREVIOUS))
    fun stop() = owner.sendPacket(createPacket(Playback.STOP))
}
