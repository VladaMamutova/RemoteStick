package ru.vladamamutova.remotestick.service

/**
 * Тип пакета, передаваемого между клиентом и сервером.
 * */
enum class PacketTypes (val value: Byte) {
    PING(1),
    HELLO(2),
    OK(3),
    ERROR(4),
    BYE(5),
    MESSAGE(6);

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        private val values = values()
        fun fromByte(value: Byte) = values.firstOrNull { it.value == value }
    }
}