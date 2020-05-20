package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes

class KeyboardPlugin : Plugin() {
    private enum class Type(val value: String) {
        SYMBOL("symbol"),
        SHORTCUTS("shortcuts");

        companion object {
            const val name = "type"
        }
    }

    override val type: PacketTypes
        get() = PacketTypes.KEYBOARD

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                when (packet.body.get(Type.name).asString) {
                    Type.SYMBOL.value -> sendKey((packet.body.get("key1").asCharacter))
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun sendKey(char: Char) {
        Win32().sendKeys(char)
    }
}