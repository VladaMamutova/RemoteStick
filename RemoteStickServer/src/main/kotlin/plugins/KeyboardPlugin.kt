package main.kotlin.plugins

import com.google.gson.Gson
import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType

class KeyboardPlugin : Plugin() {
    private enum class Type(val value: String) {
        SPECIAL_KEYS("special keys"),
        SYMBOL("symbol");
    }

    override val type: PacketType
        get() = PacketType.KEYBOARD

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                var specialKeys: Array<SpecialKey>? = null
                var symbol: Char? = null
                if (packet.body.has(Type.SPECIAL_KEYS.value)) {
                    specialKeys = Gson().fromJson(
                        packet.body.get(Type.SPECIAL_KEYS.value),
                        Array<SpecialKey>::class.java
                    )
                }

                if (packet.body.has(Type.SYMBOL.value)) {
                    symbol = packet.body.get(Type.SYMBOL.value).asCharacter
                }

                if (specialKeys != null && symbol != null) {
                    sendKeys(specialKeys, symbol)
                } else if (symbol != null) {
                    sendSymbol(symbol)
                } else if (specialKeys != null) {
                    sendSpecialKeys(specialKeys)
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun sendSymbol(symbol: Char) {
        Win32().sendSymbol(symbol)
    }

    private fun sendSpecialKeys(specialKeys: Array<SpecialKey>) {
        val specialKeysArray = IntArray(specialKeys.size)
        for (i in specialKeys.indices) {
            specialKeysArray[i] = specialKeys[i].ordinal
        }

        Win32().sendSpecialKeys(specialKeysArray)
    }

    private fun sendKeys(specialKeys: Array<SpecialKey>, symbol: Char) {
        val specialKeysArray = IntArray(specialKeys.size)
        for (i in specialKeys.indices) {
            specialKeysArray[i] = specialKeys[i].ordinal
        }

        Win32().sendKeys(specialKeysArray, symbol)
    }
}
