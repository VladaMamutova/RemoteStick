package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType

class MediaPlugin : Plugin() {
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

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                if (packet.body.has(Volume.name)) {
                    when (packet.body.get(Volume.name).asString) {
                        Volume.CHANGE.value -> {
                            changeVolume(packet.body.get(Volume.changeValue).asInt)
                        }
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

    private fun changeVolume(volumeDifference: Int) {
        if (volumeDifference != 0) {
            Win32().changeVolume(volumeDifference)
        }
    }

    private fun volumeMute() = Win32().volumeMute()

    private fun playPause() = Win32().playPause()
    private fun nextTrack() = Win32().nextTrack()
    private fun previousTrack() = Win32().prevTrack()
    private fun stop() = Win32().stop()
}
