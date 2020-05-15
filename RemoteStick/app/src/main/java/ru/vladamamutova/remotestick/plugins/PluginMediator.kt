package ru.vladamamutova.remotestick.plugins

import ru.vladamamutova.remotestick.service.NetworkPacket

/**
 * Интерфейс посредника плагинов. Определяет интерфейс для обмена
 * информацией с плагинами. Содержит метод оповещания посредника о событиях,
 * произошедших в плагинах.
 */
interface PluginMediator {
    /**
     * Оповещает посредника о событии, произошедшем в плагине,
     * с помощью ссылки на плагин и наименования события.
     */
    //fun notify(packetType: Plugin, event: String)

    fun sendPacket(packet: NetworkPacket)
}