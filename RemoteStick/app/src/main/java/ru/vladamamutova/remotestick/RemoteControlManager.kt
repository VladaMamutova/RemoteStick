package ru.vladamamutova.remotestick

import android.os.AsyncTask
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket


class RemoteControlManager(private val serverIp: InetAddress)
    : AsyncTask<Void, Void, Void>() {

    private var client: Socket? = null
    private val port: Int = 56000

    private var codeCommand: Byte = 1
    private var msg: String? = null

    // Коды команд.
    private val codeMsg: Byte = 1

    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            client = Socket(serverIp, port)

            // Получаем поток вывода
            val out = DataOutputStream(client!!.getOutputStream())
            when (codeCommand) {
                codeMsg -> {
                    out.write(byteArrayOf(codeMsg))
                    msg = "Hello!"
                    // Устанавливаем кодировку символов UTF-8
                    val outMsg: ByteArray = msg!!.toByteArray(Charsets.UTF_8)
                    out.write(outMsg)
                    out.flush()
                    out.close()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }
}
