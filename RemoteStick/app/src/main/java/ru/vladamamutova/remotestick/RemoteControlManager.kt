package ru.vladamamutova.remotestick

import android.content.Context
import ru.vladamamutova.remotestick.utils.NetworkUtils
import java.io.OutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class RemoteControlManager private constructor() {

    private lateinit var client: Socket
    private var reader: Scanner? = null
    private var writer: OutputStream? = null

    private val connected = AtomicBoolean(false) // thread-safe boolean
    private var codeCommand: Byte = 5

    companion object {
        private const val port: Int = 56000

        // Коды команд.
        private const val codePing: Byte = 1
        private const val codeHello: Byte = 2
        private const val codeOk: Byte = 3
        private const val codeError: Byte = 4
        private const val codeMessage: Byte = 5

        private lateinit var instance: RemoteControlManager

        val myInstance: RemoteControlManager
            get() {
                if (!this::instance.isInitialized) {
                    instance = RemoteControlManager()
                }

                return instance
            }

        fun pingServer(serverIp: InetAddress): String {
            try {
                val socket = Socket(serverIp, port)
                socket.use {
                    var response: String
                    try {
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
                        }
                    } catch (e: Exception) {
                        socket.close()
                        response = e.message.orEmpty()
                    }
                    return response
                }
            } catch (e: Exception) {
                return e.message.orEmpty()
            }
        }
    }

    fun connect(context: Context, serverIp : InetAddress) {
        client = Socket(serverIp, port)
        reader = Scanner(client.getInputStream())
        writer = client.getOutputStream()
        write(codeHello, NetworkUtils.getLocalIpAddress(context).hostName)

        val response: String = reader!!.nextLine()
        if (response.isNotEmpty()) {
            when ((response[0] - '0').toByte()) {
                codeError -> {
                    throw Exception("Невозможно подключиться к серверу. "
                            + response.removeRange(0, 1)
                    )
                }
            }
        }
    }
/*    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            // Получаем поток вывода
            val out = DataOutputStream(client!!.getOutputStream())
            when (codeCommand) {
                codeMsg -> {
                    out.write(byteArrayOf(codeMsg))
                    message = "Hello!"
                    // Устанавливаем кодировку символов UTF-8
                    val outMsg: ByteArray = message!!.toByteArray(Charsets.UTF_8)
                    out.write(outMsg)
                    out.flush()
                    out.close()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }*/

    private fun closeClient() {
        if (!client.isClosed) {
            try {
                client.close()
            }
            catch (ex: Exception) { }
        }
    }

    fun run() {
        try {
            client.use {
                /*thread { read() }*/
                while (connected.get()) {
                    when (codeCommand) {
                        codeMessage -> {
                            // Устанавливаем кодировку символов UTF-8
                            write(codeMessage, "Hello!")
                            codeCommand = 0
                        }
                    }
                }
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
                println(reader!!.nextLine())
            }
        }
    }

    fun stop() {
        connected.compareAndSet(true, false)
    }
}
