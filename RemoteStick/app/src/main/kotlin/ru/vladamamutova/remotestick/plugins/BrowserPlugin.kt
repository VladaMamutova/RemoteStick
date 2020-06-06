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

    private fun createPacket(command: Command): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Command.name, command.value)
        })
    }

    fun back() = owner.sendPacket(createPacket(Command.BACK))
    fun forward() = owner.sendPacket(createPacket(Command.FORWARD))
    fun home() = owner.sendPacket(createPacket(Command.HOME))
    fun refresh() = owner.sendPacket(createPacket(Command.REFRESH))
    fun newTab() = owner.sendPacket(createPacket(Command.NEW_TAB))
    fun closeTab() = owner.sendPacket(createPacket(Command.CLOSE_TAB))
    fun zoomIn() = owner.sendPacket(createPacket(Command.ZOOM_IN))
    fun zoomOut() = owner.sendPacket(createPacket(Command.ZOOM_OUT))
    fun fullScreen() = owner.sendPacket(createPacket(Command.FULL_SCREEN))
}
