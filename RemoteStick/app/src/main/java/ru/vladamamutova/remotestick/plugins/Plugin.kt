package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes


abstract class Plugin(val owner: PluginMediator) {
    abstract val type: PacketTypes
    fun createPacket(body: JsonObject) = NetworkPacket(type, body)
}
