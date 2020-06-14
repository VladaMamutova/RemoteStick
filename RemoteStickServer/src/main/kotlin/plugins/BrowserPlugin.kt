package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.plugins.SpecialKey.*
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType

class BrowserPlugin : Plugin() {
    private enum class Command(val value: String) {
        BACK("back"),
        FORWARD("forward"),
        HOME("home"),
        REFRESH("refresh"),
        ZOOM_IN("zoom in"),
        ZOOM_OUT("zoom out"),
        FULL_SCREEN("full screen"),
        PREVIOUS_TAB("previous tab"),
        NEXT_TAB("next tab"),
        NEW_TAB("new tab"),
        CLOSE_TAB("close tab"),
        LAST_TAB("last tab");

        companion object {
            const val name = "command"
        }
    }

    override val type: PacketType
        get() = PacketType.BROWSER

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                if (packet.body.has(Command.name)) {
                    when (packet.body.get(Command.name).asString) {
                        Command.BACK.value -> back()
                        Command.FORWARD.value -> forward()
                        Command.HOME.value -> home()
                        Command.REFRESH.value -> refresh()
                        Command.ZOOM_IN.value -> zoomIn()
                        Command.ZOOM_OUT.value -> zoomOut()
                        Command.FULL_SCREEN.value -> fullScreen()
                        Command.PREVIOUS_TAB.value -> previousTab()
                        Command.NEXT_TAB.value -> nextTab()
                        Command.NEW_TAB.value -> newTab()
                        Command.CLOSE_TAB.value -> closeTab()
                        Command.LAST_TAB.value -> lastTab()
                    }
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun back() {
        Win32().sendSpecialKeys(intArrayOf(ALT.ordinal, LEFT.ordinal))
    }

    private fun forward() {
        Win32().sendSpecialKeys(intArrayOf(ALT.ordinal, RIGHT.ordinal))
    }

    private fun home() {
        Win32().sendSpecialKeys(intArrayOf(ALT.ordinal, HOME.ordinal))
    }

    private fun refresh() = Win32().sendSpecialKeys(intArrayOf(F5.ordinal))
    private fun zoomIn() = Win32().sendKeys(intArrayOf(CTRL.ordinal), '+')
    private fun zoomOut() = Win32().sendKeys(intArrayOf(CTRL.ordinal), '-')
    private fun fullScreen() = Win32().sendSpecialKeys(intArrayOf(F11.ordinal))

    private fun previousTab() {
        val keys = intArrayOf(CTRL.ordinal, SHIFT.ordinal, TAB.ordinal)
        Win32().sendSpecialKeys(keys)
    }

    private fun nextTab() {
        Win32().sendSpecialKeys(intArrayOf(CTRL.ordinal, TAB.ordinal))
    }

    private fun newTab() = Win32().sendKeys(intArrayOf(CTRL.ordinal), 'T')
    private fun closeTab() = Win32().sendKeys(intArrayOf(CTRL.ordinal), 'W')
    private fun lastTab() {
        Win32().sendKeys(intArrayOf(CTRL.ordinal, SHIFT.ordinal), 'T')
    }
}
