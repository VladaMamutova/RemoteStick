package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes

class BrowserPlugin(owner: PluginMediator) : Plugin(owner) {
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

    override val type: PacketTypes
        get() = PacketTypes.BROWSER

    private fun createPacket(command: Command): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Command.name, command.value)
        })
    }

    fun back() = owner.sendPacket(createPacket(Command.BACK))
    fun forward() = owner.sendPacket(createPacket(Command.FORWARD))
    fun home() = owner.sendPacket(createPacket(Command.HOME))
    fun refresh() = owner.sendPacket(createPacket(Command.REFRESH))
    fun zoomIn() = owner.sendPacket(createPacket(Command.ZOOM_IN))
    fun zoomOut() = owner.sendPacket(createPacket(Command.ZOOM_OUT))
    fun fullScreen() = owner.sendPacket(createPacket(Command.FULL_SCREEN))
    fun previousTab() = owner.sendPacket(createPacket(Command.PREVIOUS_TAB))
    fun nextTab() = owner.sendPacket(createPacket(Command.NEXT_TAB))
    fun newTab() = owner.sendPacket(createPacket(Command.NEW_TAB))
    fun closeTab() = owner.sendPacket(createPacket(Command.CLOSE_TAB))
    fun lastTab() = owner.sendPacket(createPacket(Command.LAST_TAB))
}
