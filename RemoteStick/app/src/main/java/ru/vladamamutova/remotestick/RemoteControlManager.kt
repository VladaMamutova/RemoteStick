package ru.vladamamutova.remotestick

import android.os.Build
import java.io.OutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteControlManager private constructor() {

    private lateinit var client: Socket
    private var reader: Scanner? = null
    private var writer: OutputStream? = null

    private var codeCommand: Byte = 5

    var connected = AtomicBoolean(false) // thread-safe boolean
    private set

    var errorMessage: String = ""
    private set

    companion object {
        private const val port: Int = 56000

        // Коды команд.
        private const val codePing: Byte = 1
        private const val codeHello: Byte = 2
        private const val codeOk: Byte = 3
        private const val codeError: Byte = 4
        private const val codeBye: Byte = 5
        private const val codeMessage: Byte = 6

        private lateinit var instance: RemoteControlManager

        val myInstance: RemoteControlManager
            get() {
                if (!this::instance.isInitialized) {
                    instance = RemoteControlManager()
                }

                return instance
            }

        fun pingServer(serverIp: InetAddress): String {
            val socket = Socket(serverIp, port)
            socket.use {
                var response: String
                val reader = Scanner(socket.getInputStream())
                val writer = socket.getOutputStream()
                writer!!.write(
                    (codePing.toString() + '\n')
                        .toByteArray(Charsets.UTF_8)
                )

                response = reader.nextLine()
                if ((response[0] - '0').toByte() == codeOk) {
                    // В ответе - сетевое имя компьютера.
                    response = response.removeRange(0, 1)
                } else {
                    throw Exception(response.removeRange(0, 1))
                }
                return response
            }
        }
    }

    fun connect(serverIp : InetAddress) {
        client = Socket(serverIp, port)
        reader = Scanner(client.getInputStream())
        writer = client.getOutputStream()
        errorMessage = ""

        write(codeHello, Build.MODEL)

        try {
            val response: String = reader!!.nextLine()
            if ((response[0] - '0').toByte() == codeOk) {
                connected.compareAndSet(false, true)
            } else {
                throw Exception(response.removeRange(0, 1))
            }
        }
        catch (ex: Exception) {
            throw Exception("Невозможно подключиться к серверу.\n" + ex.message)
        }
    }

    fun run() {
        try {
            client.use {
                thread { read() }
                while (connected.get()) {
                    when (codeCommand) {
                        codeMessage -> {
                            write(codeMessage, "Hello!")
                            codeCommand = 0
                        }
                    }
                }
                write(codeBye, "")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun write(code: Byte, message: String) {
        writer!!.write((code.toString() + message + '\n').toByteArray(Charsets.UTF_8))
    }

    private fun read() {
        while (connected.get()) {
            if (reader!!.hasNextLine()) {
                val message = reader!!.nextLine()
                if ((message[0] - '0').toByte() == codeBye) {
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
