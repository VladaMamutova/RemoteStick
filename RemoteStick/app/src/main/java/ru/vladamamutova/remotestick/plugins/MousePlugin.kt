package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes
import ru.vladamamutova.remotestick.utils.MouseActionListener

class MousePlugin(owner: PluginMediator) : Plugin(owner), MouseActionListener {
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

/*
    override fun handleEvent(event: String) {
        when(event){
            Action.RIGHT_CLICK.value -> sendRightClick()
            Action.LEFT_CLICK.value -> sendLeftClick()
        }
    }*/

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

    override fun onRightClick() {
        owner.sendPacket(createPacket(Action.RIGHT_CLICK))
    }

    override fun onDoubleClick() {
        owner.sendPacket(createPacket(Action.DOUBLE_CLICK))
    }

    override fun onMove(dx: Int, dy: Int) {
        owner.sendPacket(
            createPacket(
                Action.MOVE, mapOf("dx" to dx.toString(), "dy" to dy.toString())
            )
        )
    }
}