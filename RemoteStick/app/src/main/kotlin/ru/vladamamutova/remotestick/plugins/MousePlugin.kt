package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes
import ru.vladamamutova.remotestick.ui.listeners.MouseActionListener

class MousePlugin(owner: PluginMediator) : Plugin(owner),
    MouseActionListener {
    private enum class Action(val value: String) {
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

    override val type: PacketTypes
        get() = PacketTypes.MOUSE

    private fun createPacket(
        action: Action,
        properties: Map<String, String> = mapOf()
    )
            : NetworkPacket {
        val body = JsonObject().apply { addProperty(Action.name, action.value) }
        for (property in properties) {
            body.addProperty(property.key, property.value)
        }
        return createPacket(body)
    }

    override fun onLeftClick() {
        owner.sendPacket(createPacket(Action.LEFT_CLICK))
    }

    override fun onMiddleClick() {
        owner.sendPacket(createPacket(Action.MIDDLE_CLICK))
    }

    override fun onRightClick() {
        owner.sendPacket(createPacket(Action.RIGHT_CLICK))
    }

    override fun onLeftDown() {
        owner.sendPacket(createPacket(Action.LEFT_DOWN))
    }

    override fun onLeftUp() {
        owner.sendPacket(createPacket(Action.LEFT_UP))
    }

    override fun onMove(dx: Int, dy: Int) {
        owner.sendPacket(
            createPacket(
                Action.MOVE, mapOf("dx" to dx.toString(), "dy" to dy.toString())
            )
        )
    }

    override fun onScroll(dy: Int) {
        owner.sendPacket(
            createPacket(
                Action.SCROLL, mapOf("dy" to dy.toString())
            )
        )
    }
}
