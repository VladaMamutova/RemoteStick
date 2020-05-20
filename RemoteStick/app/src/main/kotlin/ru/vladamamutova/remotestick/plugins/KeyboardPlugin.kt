package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes

class KeyboardPlugin(owner: PluginMediator) : Plugin(owner) {
    private enum class Type(val value: String) {
        SYMBOL("symbol"),
        SHORTCUTS("shortcuts");

        companion object {
            const val name = "type"
        }
    }

    override val type: PacketTypes
        get() = PacketTypes.KEYBOARD

    private fun createPacket(type: Type, keys: List<Char>): NetworkPacket {
        val body = JsonObject().apply { addProperty(Type.name, type.value) }
        for (i in keys.indices) {
            body.addProperty("key${i + 1}", keys[i])
        }
        return createPacket(body)
    }

    fun sendKey(char: Char) {
        owner.sendPacket(createPacket(Type.SYMBOL, listOf(char)))
    }
}
