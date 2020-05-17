package ru.vladamamutova.remotestick.plugins

import ru.vladamamutova.remotestick.service.NetworkPacket

interface PluginMediator {
    fun sendPacket(packet: NetworkPacket)
}
