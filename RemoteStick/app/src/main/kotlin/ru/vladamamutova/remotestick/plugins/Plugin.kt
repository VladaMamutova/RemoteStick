package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketType


abstract class Plugin(protected val owner: PluginMediator) {
    abstract val type: PacketType
    fun createPacket(body: JsonObject) = NetworkPacket(type, body)
}
