package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketTypes

class MousePlugin : Plugin() {
    enum class Action(val value: String) {
        RIGHT_CLICK("right click"),
        LEFT_CLICK("left click"),
        DOUBLE_CLICK("double click"),
        MOVE("move");

        companion object {
            const val name = "action"
        }
    }

    override val type: PacketTypes
        get() = PacketTypes.MOUSE

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                when (packet.body.get(Action.name).asString) {
                    Action.LEFT_CLICK.value -> onLeftClick()
                    Action.RIGHT_CLICK.value -> onRightClick()
                    Action.DOUBLE_CLICK.value -> onDoubleClick()
                    Action.MOVE.value -> {
                        onMove(packet.body.get("dx").asInt, packet.body.get("dy").asInt)
                    }
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun onLeftClick() {
        Win32().leftClick()
    }

    private fun onRightClick() {
        Win32().rightClick()
    }

    private fun onDoubleClick() {
        Win32().doubleClick()
    }

    private fun onMove(dx: Int, dy: Int) {
        Win32().moveMouse(dx, dy)
    }
}