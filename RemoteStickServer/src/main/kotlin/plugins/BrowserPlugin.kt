package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.plugins.SpecialKey.*
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes

class BrowserPlugin : Plugin() {
    private enum class Command(val value: String) {
        BACK("back"),
        FORWARD("forward"),
        HOME("home"),
        REFRESH("refresh"),
        NEW_TAB("new tab"),
        CLOSE_TAB("close tab"),
        ZOOM_IN("zoom in"),
        ZOOM_OUT("zoom out"),
        FULL_SCREEN("full screen");
        // incognito mode
        // на вкладку вперёд
        // на вкладку назад
        // загрузки
        // выделить строку для поиска

        companion object {
            const val name = "command"
        }
    }

    override val type: PacketTypes
        get() = PacketTypes.BROWSER

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                if (packet.body.has(Command.name)) {
                    when (packet.body.get(Command.name).asString) {
                        Command.BACK.value -> back()
                        Command.FORWARD.value -> forward()
                        Command.HOME.value -> home()
                        Command.REFRESH.value -> refresh()
                        Command.NEW_TAB.value -> newTab()
                        Command.CLOSE_TAB.value -> closeTab()
                        Command.ZOOM_IN.value -> zoomIn()
                        Command.ZOOM_OUT.value -> zoomOut()
                        Command.FULL_SCREEN.value -> fullScreen()
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
    private fun newTab() = Win32().sendKeys(intArrayOf(CTRL.ordinal), 'T')
    private fun closeTab() = Win32().sendKeys(intArrayOf(CTRL.ordinal), 'W')
    private fun zoomIn() = Win32().sendKeys(intArrayOf(CTRL.ordinal), '+')
    private fun zoomOut() = Win32().sendKeys(intArrayOf(CTRL.ordinal), '-')
    private fun fullScreen() = Win32().sendSpecialKeys(intArrayOf(F11.ordinal))
}
