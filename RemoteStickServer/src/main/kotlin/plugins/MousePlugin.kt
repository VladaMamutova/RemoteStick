package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes

class MousePlugin : Plugin() {
    enum class Action(val value: String) {
        RIGHT_CLICK("right click"),
        LEFT_CLICK("left click"),
        DOUBLE_CLICK("double click");

        companion object {
            const val name = "action"
        }
    }

    override val type: PacketTypes
        get() = PacketTypes.MOUSE

    override fun handlePacket(packet: NetworkPacket) {
        if(packet.type == type) {
            when (packet.body.get(Action.name).asString) {
                Action.LEFT_CLICK.value -> sendLeftClick()
                Action.RIGHT_CLICK.value -> sendRightClick()
                Action.DOUBLE_CLICK.value -> sendDoubleClick()
            }
        }
    }

    private fun sendLeftClick() {
        Win32().leftClick()
    }

    private fun sendRightClick() {
        Win32().rightClick()
    }

    private fun sendDoubleClick() {
        Win32().doubleClick()
    }
}