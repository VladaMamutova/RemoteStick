package ru.vladamamutova.remotestick.plugins

import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes
import ru.vladamamutova.remotestick.utils.KeyboardListener

class KeyboardPlugin(owner: PluginMediator) : Plugin(owner), KeyboardListener {
    private enum class Type(val value: String) {
        SPECIAL_KEYS("special keys"),
        SYMBOL("symbol");
    }

    override val type: PacketTypes
        get() = PacketTypes.KEYBOARD

    private fun createPacket(specialKeys: Array<SpecialKey>? = null, symbol: Char? = null): NetworkPacket {
        val body = JsonObject()
        if (specialKeys != null) {
            body.add(Type.SPECIAL_KEYS.value, Gson().toJsonTree(specialKeys))
        }

        if (symbol != null) {
            body.addProperty(Type.SYMBOL.value, symbol)
        }
        return createPacket(body)
    }

    override fun sendSymbol(symbol: Char) {
        owner.sendPacket(createPacket(symbol = symbol))
    }

    override fun sendSpecialKeys(specialKeys: Array<SpecialKey>) {
        owner.sendPacket(createPacket(specialKeys))
    }

    override fun sendKeys(specialKeys: Array<SpecialKey>, symbol: Char) {
        owner.sendPacket(createPacket(specialKeys, symbol))
    }
}
