package ru.vladamamutova.remotestick

import android.os.Build
import ru.vladamamutova.remotestick.service.PacketTypes.*
import ru.vladamamutova.remotestick.service.NetworkPacket
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteControlManager private constructor() {
    private lateinit var client: Socket
    private var reader: Scanner? = null
    private var writer: OutputStream? = null

    private var codeCommand: Byte = 6

    var connected = AtomicBoolean(false) // thread-safe boolean
    private set

    var errorMessage: String = ""
    private set

    companion object {
        private const val port: Int = 56000
        private const val connectionTimeout: Int = 2000

        private lateinit var instance: RemoteControlManager

        val myInstance: RemoteControlManager
            get() {
                if (!this::instance.isInitialized) {
                    instance = RemoteControlManager()
                }

                return instance
            }

        fun pingServer(serverIp: InetAddress): String {
            val socket = Socket()
            socket.connect(InetSocketAddress(serverIp, port), connectionTimeout)

            socket.use {
                val response: String
                val reader = Scanner(socket.getInputStream())
                val writer = socket.getOutputStream()
                writer!!.write(NetworkPacket(PING, "").toUTFBytes())
                val packet = NetworkPacket(reader.nextLine())
                if (packet.type == OK) {
                    // В ответе - сетевое имя компьютера.
                    response = packet.body
                } else {
                    throw Exception(packet.body)
                }
                return response
            }
        }
    }

    fun connect(serverIp : InetAddress) {
        client = Socket()
        client.connect(InetSocketAddress(serverIp, port), connectionTimeout)

        reader = Scanner(client.getInputStream())
        writer = client.getOutputStream()
        errorMessage = ""

        writer!!.write(NetworkPacket(HELLO, Build.MODEL).toUTFBytes())

        val packet = NetworkPacket(reader!!.nextLine())
        if (packet.type == OK) {
            connected.compareAndSet(false, true)
        } else {
            throw Exception(packet.body)
        }
    }

    fun run() {
        try {
            client.use {
                thread { read() }
                while (connected.get()) {
                    when (codeCommand) {
                        MESSAGE.value -> {
                            writer!!.write(
                                NetworkPacket(
                                    MESSAGE,
                                    "Hello"
                                ).toUTFBytes())
                            codeCommand = 0
                        }
                        else -> Thread.sleep(50)
                    }
                }
                writer!!.write(NetworkPacket(BYE, "").toUTFBytes())
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
