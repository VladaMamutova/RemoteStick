package ru.vladamamutova.remotestick.plugins

import android.os.SystemClock
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes
import ru.vladamamutova.remotestick.utils.KeyboardListener

class KeyboardPlugin(owner: PluginMediator) : Plugin(owner), KeyboardListener {
    companion object {
        private const val SHORTCUT_TIME = 500
    }

    private enum class Type(val value: String) {
        SPECIAL_KEYS("special keys"),
        SYMBOL("symbol");
    }

    override val type: PacketTypes
        get() = PacketTypes.KEYBOARD

    private var modifiersPressed: HashSet<SpecialKey> = HashSet()
    private var lastModifierClicked: SpecialKey? = null
    private var lastModifierClickTime: Long = 0

    private fun createPacket(
        specialKeys: Array<SpecialKey>? = null, symbol: Char? = null
    ): NetworkPacket {
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
        if (SystemClock.elapsedRealtime() - lastModifierClickTime < SHORTCUT_TIME) {
            Log.d("SHORTCUT", lastModifierClicked!!.name + " + " + symbol)
            owner.sendPacket(createPacket(arrayOf(lastModifierClicked!!), symbol))
        } else {
            Log.d("SYMBOL", symbol.toString())
            owner.sendPacket(createPacket(symbol = symbol))
        }
        
        lastModifierClicked = null
        lastModifierClickTime = 0
    }

    override fun sendSpecialKey(specialKey: SpecialKey) {
        if (SystemClock.elapsedRealtime() - lastModifierClickTime < SHORTCUT_TIME) {
            Log.d("SHORTCUT", lastModifierClicked!!.name + " + " + specialKey.name)
            owner.sendPacket(createPacket(arrayOf(lastModifierClicked!!, specialKey)))
        } else {
            Log.d("SPECIAL KEY", specialKey.name)
            owner.sendPacket(createPacket(arrayOf(specialKey)))
        }

        lastModifierClicked = null
        lastModifierClickTime = 0
    }

    override fun sendSpecialKeys(specialKeys: Array<SpecialKey>) {
        owner.sendPacket(createPacket(specialKeys))
    }

    override fun sendKeys(specialKeys: Array<SpecialKey>, symbol: Char) {
        owner.sendPacket(createPacket(specialKeys, symbol))
    }

    fun sendSearchBarKeys() {
        Log.d("SHORTCUT", SpecialKey.WIN.name + " + " + 'S')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.WIN), 'S'))
    }

    fun sendExplorerKeys() {
        Log.d("SHORTCUT", SpecialKey.WIN.name + " + " + 'E')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.WIN), 'E'))
    }

    fun pressCtrl() {
        lastModifierClicked = SpecialKey.CTRL
        lastModifierClickTime = SystemClock.elapsedRealtime()
        //modifiersPressed.add(SpecialKey.CTRL)
    }

    fun pressShift() {
        lastModifierClicked = SpecialKey.SHIFT
        lastModifierClickTime = SystemClock.elapsedRealtime()
        //modifiersPressed.add(SpecialKey.SHIFT)
    }

    fun pressAlt() {
        lastModifierClicked = SpecialKey.ALT
        lastModifierClickTime = SystemClock.elapsedRealtime()
        //modifiersPressed.add(SpecialKey.ALT)
    }

    fun pressWin() {
        lastModifierClicked = SpecialKey.WIN
        lastModifierClickTime = SystemClock.elapsedRealtime()
        //modifiersPressed.add(SpecialKey.WIN)
    }
}
