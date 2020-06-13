package ru.vladamamutova.remotestick.service

import android.os.Build
import ru.vladamamutova.remotestick.plugins.*
import ru.vladamamutova.remotestick.service.PacketTypes.*
import java.net.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteStickClient private constructor() : PluginMediator {
    private lateinit var client: DatagramSocket
    private lateinit var serverAddress: SocketAddress

    // BlockingQueue позволяет передавать элементы из одного потока
    // для обработки в другой поток без явных хлопот о проблемах синхронизации.
    private val packetQueue: BlockingQueue<NetworkPacket> = LinkedBlockingQueue()

    var connected = AtomicBoolean(false) // thread-safe boolean
    var errorMessage: String = ""
        private set

    val mousePlugin = MousePlugin(this)
    val keyboardPlugin = KeyboardPlugin(this)
    val mediaPlugin = MediaPlugin(this)
    val browserPlugin = BrowserPlugin(this)
    val presentationPlugin = PresentationPlugin(this)

    companion object {
        private const val PORT: Int = 56000
        private const val BUFFER_SIZE: Int = 256
        private const val PING_HELLO_TIMEOUT: Int = 2000

        private lateinit var instance: RemoteStickClient

        val myInstance: RemoteStickClient
            get() {
                if (!this::instance.isInitialized) {
                    instance = RemoteStickClient()
                }

                return instance
            }

        fun pingServer(serverIp: InetAddress): String {
            val socket = DatagramSocket()
            val bytes = NetworkPacket(PING).toUTFBytes()
            socket.send(
                DatagramPacket(
                    bytes, bytes.size, InetSocketAddress(serverIp, PORT)
                )
            )

            val networkPacket: NetworkPacket
            val buffer = ByteArray(BUFFER_SIZE)
            val packet = DatagramPacket(buffer, buffer.size)
            try {
                // Выставляем тайм-аут на блокирующие операции
                // и пытаемся за это время получить ответ на PING.
                socket.soTimeout = PING_HELLO_TIMEOUT
                networkPacket = NetworkPacket(String(packet.data, 0, packet.length))
            } catch (ex: Exception) {
                throw Exception(
                    "Невозможно подключиться к серверу с заданным IP-адресом"
                )
            }

            val response: String
            when (networkPacket.type) {
                OK -> { // В ответе - сетевое имя компьютера.
                    response = networkPacket.body.get("name").asString
                }
                ERROR -> { // В ответе - сообщение об ошибке.
                    throw Exception(networkPacket.body.get("message").asString)
                }
                else -> {
                    throw Exception("Сервер не отвечает")
                }
            }
            return response
        }
    }

    fun connect(serverIp: InetAddress): String {
        errorMessage = ""
        serverAddress = InetSocketAddress(serverIp, PORT)
        client = DatagramSocket()
        client.sendHelloPacket(serverAddress)

        val networkPacket: NetworkPacket
        val buffer = ByteArray(BUFFER_SIZE)
        val packet = DatagramPacket(buffer, buffer.size)
        try {
            // Выставляем тайм-аут на блокирующие операции
            // и пытаемся за это время получить ответ на HELLO.
            client.soTimeout = PING_HELLO_TIMEOUT
            client.receive(packet)
            networkPacket = NetworkPacket(String(packet.data, 0, packet.length))
        } catch (ex: Exception) {
            throw Exception("Невозможно подключиться к серверу с заданным IP-адресом")
        }

        val response: String
        when (networkPacket.type) {
            OK -> { // В ответе - сетевое имя компьютера.
                response = networkPacket.body.get("name").asString
                connected.compareAndSet(false, true)
            }
            ERROR -> { // В ответе - сообщение об ошибке.
                throw Exception(networkPacket.body.get("message").asString)
            }
            else -> {
                throw Exception("Сервер не отвечает")
            }
        }
        return response
    }

    fun run() {
        if (connected.get()) {
            // Выставляем бесконечный тайм-аут на получение сообщений от сервера.
            client.soTimeout = 0

            try {
                client.use {
                    thread { read() }
                    while (connected.get()) {
                        if (packetQueue.isNotEmpty()) {
                            client.sendPacket(packetQueue.take(), serverAddress)
                        }
                    }
                    client.sendByePacket(serverAddress)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun read() {
        while (connected.get()) {
            try {
                val buffer = ByteArray(BUFFER_SIZE)
                val packet = DatagramPacket(buffer, buffer.size)
                client.receive(packet)
                val networkPacket = NetworkPacket(String(packet.data, 0, packet.length))

                if (networkPacket.type == BYE) {
                    stop()
                    errorMessage = "Сервер перестал отвечать"
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    /**
     * Отправляет сетевой пакет типа HELLO с указанием имени клиента
     * (в теле пакета - свойство name).
     */
    private fun DatagramSocket.sendHelloPacket(address: SocketAddress) {
        this.sendPacket(NetworkPacket(HELLO, "name", Build.MODEL), address)
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

    override fun sendPacket(packet: NetworkPacket) {
        packetQueue.put(packet)
    }

    fun stop() {
        connected.compareAndSet(true, false)
    }
}
