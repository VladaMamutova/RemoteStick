package ru.vladamamutova.remotestick.service

import android.os.Build
import ru.vladamamutova.remotestick.plugins.MousePlugin
import ru.vladamamutova.remotestick.plugins.PluginMediator
import ru.vladamamutova.remotestick.service.PacketTypes.*
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteStickClient private constructor() : PluginMediator {
    private lateinit var client: Socket
    private var reader: Scanner? = null
    private var writer: OutputStream? = null
    private var connected = AtomicBoolean(false) // thread-safe boolean
    private val packetQueue: Queue<NetworkPacket> = LinkedList()

    var errorMessage: String = ""
    private set

    val mousePlugin = MousePlugin(this)

    companion object {
        private const val port: Int = 56000
        private const val connectionTimeout: Int = 1500

        private lateinit var instance: RemoteStickClient

        val myInstance: RemoteStickClient
            get() {
                if (!this::instance.isInitialized) {
                    instance =
                        RemoteStickClient()
                }

                return instance
            }

        fun pingServer(serverIp: InetAddress): String {
            val socket = Socket()
            try {
                socket.connect(InetSocketAddress(serverIp,
                    port
                ), connectionTimeout
                )
            } catch (ex: Exception) {
                throw Exception(
                    "Невозможно подключиться к серверу с заданным IP-адресом"
                )
            }

            socket.use {
                val response: String
                val reader = Scanner(socket.getInputStream())
                val writer = socket.getOutputStream()
                writer!!.write(NetworkPacket(PING).toUTFBytes())
                val packet = NetworkPacket(reader.nextLine())
                when (packet.type) {
                    OK -> { // В ответе - сетевое имя компьютера.
                        response = packet.body.get("name").asString
                    }
                    ERROR -> { // В ответе - сообщение об ошибке.
                        throw Exception(packet.body.get("message").asString)
                    }
                    else -> {
                        throw Exception("Сервер не отвечает")
                    }
                }
                return response
            }
        }
    }

    // Когда что-то случается с компонентом, он шлёт посреднику
    // оповещение. После получения извещения посредник может
    // либо сделать что-то самостоятельно, либо перенаправить
    // запрос другому компоненту.
    /*override fun notify(type: PacketTypes, event: String) {
       for (plugin in plugins){
           if (type == plugin.type) {
               plugin.handleEvent(event)
           }
       }
    }*/

    fun connect(serverIp : InetAddress): String {
        client = Socket()
        try {
            client.connect(InetSocketAddress(serverIp,
                port
            ), connectionTimeout
            )
        } catch (ex: Exception) {
            throw Exception(
                "Невозможно подключиться к серверу с заданным IP-адресом"
            )
        }

        reader = Scanner(client.getInputStream())
        writer = client.getOutputStream()
        errorMessage = ""

        writer!!.sendPacket(NetworkPacket(HELLO, "name", Build.MODEL))

        val response: String
        val packet = NetworkPacket(reader!!.nextLine())
        when (packet.type) {
            OK -> { // В ответе - сетевое имя компьютера.
                response = packet.body.get("name").asString
                connected.compareAndSet(false, true)
            }
            ERROR -> { // В ответе - сообщение об ошибке.
                throw Exception(packet.body.get("message").asString)
            }
            else -> {
                throw Exception("Сервер не отвечает")
            }
        }
        return response
    }

    private fun OutputStream.sendPacket(packet: NetworkPacket) {
        this.write(packet.toUTFBytes())
    }

    override fun sendPacket(packet: NetworkPacket) {
        packetQueue.add(packet)
    }

    fun run() {
        try {
            client.use {
                thread { read() }
                while (connected.get()) {
                    if(packetQueue.isNotEmpty()) {
                        writer?.write(packetQueue.remove().toUTFBytes())
                    }
                }
                writer!!.sendPacket(NetworkPacket(BYE))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun read() {
        while (connected.get()) {
            if (reader!!.hasNextLine()) {
                val packet = NetworkPacket(reader!!.nextLine())
                if (packet.type == BYE) {
                    stop()
                    errorMessage = "Сервер перестал отвечать"
                }
            }
        }
    }

    fun stop() {
        connected.compareAndSet(true, false)
    }
}
