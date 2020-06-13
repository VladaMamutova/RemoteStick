package main.kotlin.service

import main.kotlin.plugins.*
import main.kotlin.service.PacketTypes.*
import java.net.*
import java.util.concurrent.atomic.AtomicBoolean


class RemoteStickServer: Runnable {
    companion object {
        private const val PORT: Int = 56000
        private const val BUFFER_SIZE: Int = 256
    }

    private lateinit var server: DatagramSocket
    private val running = AtomicBoolean(false) // thread-safe boolean
    private val clientMap: MutableMap<String, SocketAddress> = mutableMapOf()
    private val mousePlugin = MousePlugin()
    private val keyboardPlugin = KeyboardPlugin()
    private val mediaPlugin = MediaPlugin()
    private val browserPlugin = BrowserPlugin()
    private val presentationPlugin = PresentationPlugin()
    private val powerPlugin = PowerPlugin()

    private fun closeServer() {
        if (server.isBound && !server.isClosed) {
            for (client in clientMap) {
                server.sendByePacket(client.value)
                println("Client ${client.key} closed.")
            }
            clientMap.clear()
            println("Number of clients: ${clientMap.size}")
            server.close()
            println("\nServer stopped.")
        }
    }

    override fun run() {
        try {
            running.set(true)

            // Создаём и привязываем серверный сокет к локальному адресу и порту.
            server = DatagramSocket(PORT, InetAddress.getLocalHost())
            // Устанавливаем бесконечный тайм-аут на получение пакетов от клиентов.
            server.soTimeout = 0
            println("\nServer ${server.localAddress}:$PORT started")
            println("Wait for connection...")

            val packet = DatagramPacket(ByteArray(BUFFER_SIZE), BUFFER_SIZE)
            while (running.get()) {
                try {
                    server.receive(packet)
                    val networkPacket = NetworkPacket(String(packet.data, 0, packet.length))
                    when (networkPacket.type) {
                        PING -> {
                            server.sendOkPacket(packet.socketAddress)
                            println("\nPing from ${packet.socketAddress}")
                        }
                        HELLO -> {
                            server.sendOkPacket(packet.socketAddress)
                            // В пакете - имя подключённого устройства.
                            val name = networkPacket.body.get("name")?.asString +
                                    " (${(packet.socketAddress as InetSocketAddress)
                                        .hostName})"
                            clientMap[name] = packet.socketAddress
                            println("\nClient $name connected")
                            println("Number of clients: ${clientMap.size}")
                        }
                        MOUSE -> mousePlugin.handlePacket(networkPacket)
                        KEYBOARD -> keyboardPlugin.handlePacket(networkPacket)
                        MEDIA -> mediaPlugin.handlePacket(networkPacket)
                        BROWSER -> browserPlugin.handlePacket(networkPacket)
                        PRESENTATION -> presentationPlugin.handlePacket(networkPacket)
                        POWER -> powerPlugin.handlePacket(networkPacket)
                        BYE -> {
                            if (clientMap.containsValue(packet.socketAddress)) {
                                val name = clientMap.filterValues {
                                    it == packet.socketAddress
                                }
                                    .keys.first()
                                println("\nClient $name disconnected")
                                println("Number of clients: ${clientMap.size}")
                                clientMap.remove(name)
                            }
                        }
                        else -> {
                            val hostname = (packet.socketAddress as InetSocketAddress).hostName
                            println("The packet $networkPacket from $hostname is not defined")
                        }
                    }
                } catch (ex: Exception) {
                    println("Error: " + ex.message)
                }
            }
        } catch (ex: Exception) {
            println("Error: " + ex.message)
        } finally {
            closeServer()
        }
    }

    /**
     * Отправляет сетевой пакет типа OK с указанием имени сервера
     * (в теле пакета - свойство name).
     */
    private fun DatagramSocket.sendOkPacket(address: SocketAddress) {
        this.sendPacket(
            NetworkPacket(
                OK, "name", server.localAddress.hostName
            ), address
        )
    }

    /**
     * Отправляет сетевой пакет типа ERROR с указанием сообщения об ошибке
     * (в теле пакета - свойство message).
     */
    private fun DatagramSocket.sendErrorPacket(
        address: SocketAddress, message: String
    ) {
        this.sendPacket(NetworkPacket(ERROR, "message", message), address)
    }

    /**
     * Отправляет сетевой пакет типа BYE с пустым телом.
     */
    private fun DatagramSocket.sendByePacket(address: SocketAddress) {
        this.sendPacket(NetworkPacket(BYE), address)
    }

    /**
     * Отправляет сетевой пакет на указанный удалённый адрес.
     */
    private fun DatagramSocket.sendPacket(
        packet: NetworkPacket, address: SocketAddress
    ) {
        val bytes = packet.toUTFBytes()
        this.send(DatagramPacket(bytes, bytes.size, address))
    }

    fun getClientName() =
        if (clientMap.isNotEmpty()) {
            clientMap.keys.joinToString(", ")
        } else {
            "не подключён"
        }

    fun stop() {
        running.compareAndSet(true, false)
        closeServer()
    }

    fun isRunning() = running.get()
}
