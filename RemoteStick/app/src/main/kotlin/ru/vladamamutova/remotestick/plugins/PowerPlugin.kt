package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketTypes

class PowerPlugin(owner: PluginMediator) : Plugin(owner) {
    private enum class Command(val value: String) {
        SLEEP("sleep"),
        HIBERNATE("hibernate"),
        LOCK("lock"),
        RESTART("restart"),
        POWER_OFF("power off");

        companion object {
            const val name = "command"
        }
    }

    override val type: PacketTypes
        get() = PacketTypes.POWER

    private fun createPacket(command: Command): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Command.name, command.value)
        })
    }

    fun sleep() = owner.sendPacket(createPacket(Command.SLEEP))
    fun hibernate() = owner.sendPacket(createPacket(Command.HIBERNATE))
    fun lock() = owner.sendPacket(createPacket(Command.LOCK))
    fun restart() = owner.sendPacket(createPacket(Command.RESTART))
    fun powerOff() = owner.sendPacket(createPacket(Command.POWER_OFF))
}
