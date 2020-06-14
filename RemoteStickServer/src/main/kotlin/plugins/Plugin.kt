package main.kotlin.plugins

import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType


abstract class Plugin {
    abstract val type: PacketType
    abstract fun handlePacket(packet: NetworkPacket)
}