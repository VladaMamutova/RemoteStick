package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes

class BrowserPlugin : Plugin() {
    private enum class Command(val value: String) {
        BACK("back"),
        FORWARD("forward"),
        REFRESH("refresh"),
        HOME("home"),
        FAVORITES("favorites"), //
        SEARCH("search"), //
        STOP("stop"), //
        ZOOM_IN("zoom in"),
        ZOOM_OUT("zoom out"),
        FULL_SCREEN("full screen"),
        OPEN_NEW("open new"),
        CLOSE_ACTIVE("close active");
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
                        Command.BACK.value -> sendBrowserKey(BrowserKey.BACK)
                        Command.FORWARD.value -> sendBrowserKey(BrowserKey.FORWARD)
                        Command.REFRESH.value -> sendBrowserKey(BrowserKey.REFRESH)
                        Command.HOME.value -> sendBrowserKey(BrowserKey.HOME)
                        Command.FAVORITES.value -> sendBrowserKey(BrowserKey.FAVORITES)
                        Command.SEARCH.value -> sendBrowserKey(BrowserKey.SEARCH)
                        Command.STOP.value -> sendBrowserKey(BrowserKey.STOP)
                    }
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun sendBrowserKey(browserKey: BrowserKey) {
        Win32().sendBrowserKey(browserKey.ordinal)
    }
}
