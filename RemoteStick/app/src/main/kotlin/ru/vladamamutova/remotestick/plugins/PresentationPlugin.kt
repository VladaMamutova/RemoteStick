package ru.vladamamutova.remotestick.plugins

import com.google.gson.JsonObject
import ru.vladamamutova.remotestick.service.NetworkPacket
import ru.vladamamutova.remotestick.service.PacketType

class PresentationPlugin(owner: PluginMediator) : Plugin(owner) {
    private enum class Command(val value: String) {
        NEXT("next"),
        PREVIOUS("previous"),
        START("start"),
        CONTINUE("continue"),
        STOP("stop"),
        SET_POINTER("set pointer"),
        RESTORE_CURSOR("restore cursor");

        companion object {
            const val name = "command"
        }
    }

    override val type: PacketType
        get() = PacketType.PRESENTATION

    var pointerChecked: Boolean = false; private set

    private fun createPacket(command: Command): NetworkPacket {
        return createPacket(JsonObject().apply {
            addProperty(Command.name, command.value)
        })
    }

    fun togglePointer() {
        pointerChecked = !pointerChecked
        if (pointerChecked) {
            owner.sendPacket(createPacket(Command.SET_POINTER))
        } else {
            owner.sendPacket(createPacket(Command.RESTORE_CURSOR))
        }
    }

    fun next() = owner.sendPacket(createPacket(Command.NEXT))
    fun previous() = owner.sendPacket(createPacket(Command.PREVIOUS))
    fun start() = owner.sendPacket(createPacket(Command.START))
    fun continuePresentation() = owner.sendPacket(createPacket(Command.CONTINUE))
    fun stop() = owner.sendPacket(createPacket(Command.STOP))
}
