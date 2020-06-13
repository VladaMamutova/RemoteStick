package ru.vladamamutova.remotestick.service

/**
 * Тип пакета, передаваемого между клиентом и сервером.
 * */
enum class PacketTypes (val value: Byte) {
    PING(0),
    HELLO(1),
    OK(2),
    ERROR(3),
    BYE(4),
    MOUSE(5),
    KEYBOARD(6),
    MEDIA(7),
    PRESENTATION(8),
    BROWSER(9),
    POWER(10);


    override fun toString(): String {
        return value.toString()
    }

    companion object {
        private val values = values()
        fun fromByte(value: Byte) = values.firstOrNull { it.value == value }
    }
}
