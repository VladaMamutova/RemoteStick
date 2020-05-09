package main.kotlin.service

import main.kotlin.service.PacketTypes.*
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteStickServer: Runnable {
    companion object {
        private const val port: Int = 56000
    }

    private val server = ServerSocket()
    private val isServerAlive = AtomicBoolean(false) // thread-safe boolean
    private val clientMap: MutableMap<String, Socket> = mutableMapOf()

    private fun closeServer() {
        if (server.isBound && !server.isClosed) {
            for (client in clientMap.values) {
                client.getOutputStream().sendPacket(NetworkPacket(BYE))
                client.close()
                println("Client ${client.inetAddress.hostAddress} closed.")
            }
            server.close()
            println("\nServer stopped.")
        }
    }

    override fun run() {
        // Создаём конечную точку с IP-адресом и портом.
        val socketAddress = InetSocketAddress(InetAddress.getLocalHost().hostAddress,
            port
        )
        server.reuseAddress = true
        try {
            server.bind(socketAddress) // Привязываем серверный сокет к настроенной конечной точке.
            isServerAlive.set(true)
            println("\nServer ${server.inetAddress.hostAddress}:$port started")
            println("Wait for connection...")

            while (isServerAlive.get()) {
                // Ждем соединение.
                val client: Socket = server.accept()
                try {
                    val reader = Scanner(client.getInputStream())
                    val writer: OutputStream = client.getOutputStream()
                    val packet = NetworkPacket(reader.nextLine())
                    // Проверяем цель запроса клиента: пинг или подключение.
                    // В остальных случаях пропускам пакет.
                    when (packet.type) {
                        PING -> {
                            sendOkPacket(writer)
                            println("\nPing from ${client.inetAddress.hostAddress}")
                            client.close()
                        }
                        HELLO -> {
                            if (clientMap.size < 2) {
                                // В пакете - имя подключённого устройства.
                                val name = packet.body.get("name")?.asString +
                                        " (${client.inetAddress.hostAddress})"
                                clientMap[name] = client
                                sendOkPacket(writer)
                                println("\nClient $name connected")
                                println("Number of clients: ${clientMap.size}")
                                thread {
                                    runClientHandler(client)
                                    clientMap.remove(name)
                                    println("Number of clients: ${clientMap.size}")
                                }
                            } else {
                                sendErrorPacket(writer, "Уже подключено 2 устройства")
                                println(
                                    "Client ${client.inetAddress.hostAddress} cannot be connected " +
                                            "due to restrictions on the number of clients"
                                )
                                println("Number of clients: ${clientMap.size}")
                                client.close()
                            }
                        }
                        else -> client.close()
                    }
                } catch (ex: Exception) {
                    client.close()
                }
            }
        } catch (ex: Exception) {
            println("Error: " + ex.message)
        } finally {
            closeServer()
        }
    }

    private fun runClientHandler(client: Socket) {
        val reader = Scanner(client.getInputStream())

        // Пока работа сервера не прекращена и клиент не отключился,
        // получаем сообщения от клиента и обрабатываем их.
        while (isServerAlive.get() && !client.isClosed) {
            try {
                if (reader.hasNextLine()) {
                    val packet = NetworkPacket(reader.nextLine())
                    when (packet.type) {
                        BYE -> { // Клиент отключился.
                            client.close()
                            println(
                                "Client ${client.inetAddress.hostAddress} " +
                                        "disconnected"
                            )
                        }
                        else -> println(
                            "The package type from " +
                                    "${client.inetAddress.hostAddress} is not defined"
                        )
                    }
                }
            } catch (ex: Exception) {
                println("Error: " + ex.message)
            }
        }
    }

    /**
     * Отправляет сетевой пакет типа OK с указанием имени сервера
     * (в теле пакета - свойство name).
     * */
    private fun sendOkPacket(writer: OutputStream) {
        writer.sendPacket(
            NetworkPacket(OK, "name", server.inetAddress.hostName)
        )
    }

    /**
     * Отправляет сетевой пакет типа ERROR с указанием сообщения об ошибке
     * (в теле пакета - свойство message).
     * */
    private fun sendErrorPacket(writer: OutputStream, message: String) {
        writer.sendPacket(
            NetworkPacket(ERROR, "message", message)
        )
    }

    private fun OutputStream.sendPacket(packet: NetworkPacket) {
        this.write(packet.toUTFBytes())
    }

    fun getClientName() =
        if (clientMap.isNotEmpty())
        clientMap.keys.joinToString(", ")
        else "не подключён"

    fun stop() {
        isServerAlive.compareAndSet(true, false)
        closeServer()
    }

    fun isServerAlive() = isServerAlive.get()
}