package ru.vladamamutova.remotestick.plugins

import android.os.Handler
import android.os.SystemClock
import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes
import ru.vladamamutova.remotestick.ui.listeners.KeyboardListener

class KeyboardPlugin(owner: PluginMediator) : Plugin(owner),
    KeyboardListener {
    companion object {
        private const val SHORTCUT_TIME = 500
    }

    private enum class Type(val value: String) {
        SPECIAL_KEYS("special keys"),
        SYMBOL("symbol");
    }

    override val type: PacketTypes
        get() = PacketTypes.KEYBOARD

    private var shortcutHandler: Handler? = null
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

    override fun onKeyPress(key: Char): Boolean {
        if (modifiersPressed.isNotEmpty()) { // клавиши-модификаторы зажаты
            // Отправляем сочетание клавиш:
            // модификатор + символ или
            // модификатор + модификатор + символ.
            sendShortcutToHandler(modifiersPressed.toTypedArray(), key.toUpperCase())
            owner.sendPacket(createPacket(modifiersPressed.toTypedArray(), key))
            modifiersPressed.clear()
        } else { // нет зажатых клавиш-модификаторов
            // Проверяем была ли коротко нажата клавиша-модификатор и время её нажатия.
            if (lastModifierClicked != null &&
                SystemClock.elapsedRealtime() - lastModifierClickTime < SHORTCUT_TIME
            ) {
                // Отправляем сочетание клавиш (модификатор + символ).
                sendShortcutToHandler(arrayOf(lastModifierClicked!!), key.toUpperCase())
                owner.sendPacket(createPacket(arrayOf(lastModifierClicked!!), key))
                lastModifierClicked = null
                lastModifierClickTime = 0
            } else {
                // Отправляем одиночный символ.
                owner.sendPacket(createPacket(symbol = key))
                return true // отправлен одиночный символ
            }
        }

        return false // отправлено сочетание клавиш с символом
    }

    override fun onSpecialKeyPress(specialKey: SpecialKey) {
        if (modifiersPressed.isNotEmpty()) { // клавиши-модификаторы зажаты
            if (!modifiersPressed.contains(specialKey)) {
                // Отправляем сочетание клавиш:
                // модификатор + модификатор или
                // модификатор + модификатор + модификатор, или
                // модификатор + модификатор + немодификатор.
                val keys: MutableList<SpecialKey> = modifiersPressed.toMutableList()
                keys.add(specialKey)
                sendShortcutToHandler(keys.toTypedArray())
                owner.sendPacket(createPacket(keys.toTypedArray()))
                modifiersPressed.clear()
            }
        } else { // нет зажатых клавиш-модификаторов
            if (specialKey == SpecialKey.WIN) {
                // Нажатие Win сразу отправляем.
                sendShortcutToHandler(arrayOf(specialKey))
                owner.sendPacket(createPacket(arrayOf(specialKey)))
            } else if (specialKey.isModifier()) {
                // При нажатии любой другой клавиши-модификатора, кроме Win:
                // Ctrl, Shift, Alt - сохраняем её и время нажатия.
                lastModifierClicked = specialKey
                lastModifierClickTime = SystemClock.elapsedRealtime()
            } else {
                // При нажатии другой специальной клавиши-немодификатора
                // проверяем была ли коротко нажата клавиша-модификатор и время её нажатия.
                if (lastModifierClicked != null &&
                    SystemClock.elapsedRealtime() - lastModifierClickTime < SHORTCUT_TIME
                ) {
                    // Отправляем сочетание клавиш (модификатор + немодификатор).
                    val keys = arrayOf(lastModifierClicked!!, specialKey)
                    sendShortcutToHandler(keys)
                    owner.sendPacket(createPacket(keys))
                    lastModifierClicked = null
                    lastModifierClickTime = 0
                } else {
                    // Отправляем одиночную специальную клавишу-немодификатор.
                    owner.sendPacket(createPacket(arrayOf(specialKey)))
                }
            }
        }
    }

    private fun sendShortcutToHandler(specialKeys: Array<SpecialKey>, symbol: Char? = null) {
        var message = specialKeys.joinToString(" + ")
        if (symbol != null) {
            message += " + $symbol"
        }

        shortcutHandler?.let { it.sendMessage(it.obtainMessage(0, message)) }
    }

    private fun updateModifiersPressed(specialKey: SpecialKey) {
        if (specialKey.isModifier()) {
            if (modifiersPressed.contains(specialKey)) {
                modifiersPressed.remove(specialKey)
            } else {
                modifiersPressed.add(specialKey)
            }
        }
    }

    fun holdDownCtrl() {
        updateModifiersPressed(SpecialKey.CTRL)
    }

    fun holdDownShift() {
        updateModifiersPressed(SpecialKey.SHIFT)
    }

    fun holdDownAlt() {
        updateModifiersPressed(SpecialKey.ALT)
    }

    fun holdDownWin() {
        updateModifiersPressed(SpecialKey.WIN)
    }

    fun sendSearchBarKeys() {
        sendShortcutToHandler(arrayOf(SpecialKey.WIN), 'S')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.WIN), 'S'))
    }

    fun sendExplorerKeys() {
        sendShortcutToHandler(arrayOf(SpecialKey.WIN), 'E')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.WIN), 'E'))
    }

    fun sendDesktopKeys() {
        sendShortcutToHandler(arrayOf(SpecialKey.WIN), 'D')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.WIN), 'D'))
    }

    fun sendCopyKeys() {
        sendShortcutToHandler(arrayOf(SpecialKey.CTRL), 'C')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.CTRL), 'C'))
    }

    fun sendPasteKeys() {
        sendShortcutToHandler(arrayOf(SpecialKey.CTRL), 'V')
        owner.sendPacket(createPacket(arrayOf(SpecialKey.CTRL), 'V'))
    }

    fun setShortcutHandler(shortcutHandler: Handler) {
        this.shortcutHandler = shortcutHandler
    }

    fun holdUpModifiers() {
        modifiersPressed.clear()
    }
}
