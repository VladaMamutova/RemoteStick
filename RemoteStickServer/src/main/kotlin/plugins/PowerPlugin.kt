package main.kotlin.plugins

import main.kotlin.Win32
import main.kotlin.service.NetworkPacket
import main.kotlin.service.PacketType

class PowerPlugin : Plugin() {
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

    override val type: PacketType
        get() = PacketType.POWER

    override fun handlePacket(packet: NetworkPacket) {
        try {
            if (packet.type == type) {
                if (packet.body.has(Command.name)) {
                    when (packet.body.get(Command.name).asString) {
                        Command.SLEEP.value -> sleep()
                        Command.HIBERNATE.value -> hibernate()
                        Command.LOCK.value -> lock()
                        Command.RESTART.value -> restart()
                        Command.POWER_OFF.value -> powerOff()
                    }
                }
            }
        } catch (ex: Exception) {
            println("ERROR: ${ex.message}")
        }
    }

    private fun sleep() = Win32().suspendSystem(false)
    private fun hibernate() = Win32().suspendSystem(true)
    private fun lock() = Win32().sendKeys(intArrayOf(SpecialKey.WIN.ordinal), 'L')
    private fun restart() = Win32().restart()
    private fun powerOff() = Win32().powerOff()
}
