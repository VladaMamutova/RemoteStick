package ru.vladamamutova.remotestick

import java.io.OutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class RemoteControlManager {

    private val port: Int = 56000
    private lateinit var client: Socket

    private var reader: Scanner? = null
    private var writer: OutputStream? = null

    private val connected = AtomicBoolean(false) // thread-safe boolean

    private var codeCommand: Byte = 1

    // Коды команд.
    private val codeOk: Byte = 1
    private val codeError: Byte = 2
    private val codeMessage: Byte = 3


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

    fun pingServer(serverIp: InetAddress): String {
        // Если сокет клиента уже был инициализирован, закрываем его.
        if (::client.isInitialized) {
            closeClient()
        }

        try {
            client = Socket(serverIp, port)
            client.use {
                var response : String
                try {
                    reader = Scanner(client.getInputStream())
                    writer = client.getOutputStream()

                    response = reader!!.nextLine()
                    val code = (response[0] - '0').toByte()
                    if (code == codeOk) {
                        response = "Подключено к компьютеру " + response.removeRange(0, 1)
                        connected.compareAndSet(false, true)
                    }
                    else {
                        // TODO: if (code != codeOk)
                    }
                }
                catch (e:Exception) {
                    // Сокет должен быть закрыт при любой
                    // ошибке, кроме ошибки конструктора сокета.
                    // Закрытие сокета приведёт также к закрытию
                    // потоков ввода и вывода.
                    client.close()
                    response = e.message.orEmpty()
                }

                return response
            }
        } catch (e : Exception) {
            return e.message.orEmpty()
        }
    }

    fun run() {
        try {
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
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        finally {
            closeClient()
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
