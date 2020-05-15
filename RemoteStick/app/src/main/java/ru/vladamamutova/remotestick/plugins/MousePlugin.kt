package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes

class MousePlugin(owner: PluginMediator) : Plugin(owner) {
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

/*
    override fun handleEvent(event: String) {
        when(event){
            Action.RIGHT_CLICK.value -> sendRightClick()
            Action.LEFT_CLICK.value -> sendLeftClick()
        }
    }*/

    private fun createPacket(action: Action,
                             properties: Map<String, String> = mapOf())
            : NetworkPacket {
        val body = JsonObject().apply { addProperty(Action.name, action.value) }
        for (property in properties) {
            body.addProperty(property.key, property.value)
        }
        return createPacket(body)
    }

    fun sendLeftClick() {
        owner.sendPacket(createPacket(Action.LEFT_CLICK))
    }

    fun sendRightClick() {
        owner.sendPacket(createPacket(Action.RIGHT_CLICK))
    }

    fun sendDoubleClick() {
        owner.sendPacket(createPacket(Action.DOUBLE_CLICK))
    }
}