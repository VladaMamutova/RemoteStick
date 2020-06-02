package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes

class MediaPlugin : Plugin() {
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

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                if (packet.body.has(Volume.name)) {
                    when (packet.body.get(Volume.name).asString) {
                        Volume.UP.value -> volumeUp()
                        Volume.DOWN.value -> volumeDown()
                        Volume.MUTE.value -> volumeMute()
                    }
                } else if (packet.body.has(Playback.name)) {
                    when (packet.body.get(Playback.name).asString) {
                        Playback.PLAY_PAUSE.value -> playPause()
                        Playback.NEXT.value -> nextTrack()
                        Playback.PREVIOUS.value -> previousTrack()
                        Playback.STOP.value -> stop()
                    }
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private var mute: Boolean = false
    private var volume: Int = 0

    private fun volumeUp() = Win32().volumeUp()
    fun volumeDown() = Win32().volumeDown()
    fun volumeMute() = Win32().volumeMute()

    private fun playPause() = Win32().playPause()
    private fun nextTrack() = Win32().nextTrack()
    private fun previousTrack() = Win32().prevTrack()
    private fun stop() = Win32().stop()
}
