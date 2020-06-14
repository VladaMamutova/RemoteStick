package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType

class MousePlugin : Plugin() {
    enum class Action(val value: String) {
        RIGHT_CLICK("right click"),
        MIDDLE_CLICK("middle click"),
        LEFT_CLICK("left click"),
        LEFT_DOWN("left down"),
        LEFT_UP("left up"),
        MOVE("move"),
        SCROLL("scroll");

        companion object {
            const val name = "action"
        }
    }

    override val type: PacketType
        get() = PacketType.MOUSE

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                when (packet.body.get(Action.name).asString) {
                    Action.LEFT_CLICK.value -> onLeftClick()
                    Action.MIDDLE_CLICK.value -> onMiddleClick()
                    Action.RIGHT_CLICK.value -> onRightClick()
                    Action.LEFT_DOWN.value -> onLeftDown()
                    Action.LEFT_UP.value -> onLeftUp()
                    Action.MOVE.value -> {
                        onMove(
                            packet.body.get("dx").asInt,
                            packet.body.get("dy").asInt
                        )
                    }
                    Action.SCROLL.value -> onScroll(packet.body.get("dy").asInt)
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun onLeftClick() = Win32().leftClick()
    private fun onMiddleClick() = Win32().middleClick()
    private fun onRightClick() = Win32().rightClick()
    private fun onLeftDown() = Win32().leftDown()
    private fun onLeftUp() = Win32().leftUp()
    private fun onMove(dx: Int, dy: Int) = Win32().move(dx, dy)
    private fun onScroll(dy: Int) = Win32().scroll(dy)
}
