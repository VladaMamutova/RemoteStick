package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.plugins.SpecialKey.*
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType

class PresentationPlugin : Plugin() {
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

    private var isLaserCursor = false

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                if (packet.body.has(Command.name)) {
                    when (packet.body.get(Command.name).asString) {
                        Command.NEXT.value -> next()
                        Command.PREVIOUS.value -> previous()
                        Command.START.value -> start()
                        Command.CONTINUE.value -> continuePresentation()
                        Command.STOP.value -> stop()
                        Command.SET_POINTER.value -> setLaserCursor()
                        Command.RESTORE_CURSOR.value -> restoreCursor()
                    }
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun next() = Win32().sendSpecialKeys(intArrayOf(PAGE_DOWN.ordinal))
    private fun previous() = Win32().sendSpecialKeys(intArrayOf(PAGE_UP.ordinal))
    private fun start() = Win32().sendSpecialKeys(intArrayOf(F5.ordinal))
    private fun continuePresentation() {
        Win32().sendSpecialKeys(intArrayOf(SHIFT.ordinal, F5.ordinal))
    }

    private fun stop() = Win32().sendSpecialKeys(intArrayOf(ESC.ordinal))
    private fun setLaserCursor() {
        if (!isLaserCursor) {
            Win32().setLaserCursor()
            isLaserCursor = true
        }
    }

    fun restoreCursor() {
        if (isLaserCursor) {
            Win32().restoreCursor()
            isLaserCursor = false
        }
    }
}
