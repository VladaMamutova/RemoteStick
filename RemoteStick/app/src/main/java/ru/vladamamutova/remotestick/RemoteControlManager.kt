package ru.vladamamutova.remotestick

import java.io.OutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteControlManager(private val serverIp: InetAddress) {

    private val port: Int = 56000
    private var client: Socket? = null

    private var reader: Scanner? = null
    private var writer: OutputStream? = null

    private val connected = AtomicBoolean(true) // thread-safe boolean

    private var codeCommand: Byte = 1

    // Коды команд.
    private val codeMsg: Byte = 1

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

    fun run() {
        try {
            client = Socket(serverIp, port)

            reader = Scanner(client!!.getInputStream())
            writer = client!!.getOutputStream()

            /*thread { read() }*/
            while (connected.get()) {
                when (codeCommand) {
                    codeMsg -> {
                        // Устанавливаем кодировку символов UTF-8
                        write(codeMsg, "Hello!")
                        codeCommand = 0
                    }
                }
            }
        } catch (ex : Exception) {
            connected.compareAndSet(true, false)
            client?.close()
        }
    }

    private fun write(code: Byte, message: String) {
        writer!!.write((code.toString() + message + '\n').toByteArray(Charsets.UTF_8))
    }

    private fun read() {
        while (connected.get()) {
            println(reader!!.nextLine())
        }
    }
}
