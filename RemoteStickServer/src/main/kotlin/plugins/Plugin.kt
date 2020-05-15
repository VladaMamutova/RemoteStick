package main.kotlin.plugins

import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes


abstract class Plugin {
    abstract val type: PacketTypes
    abstract fun handlePacket(packet: NetworkPacket)
}