package main.kotlin

import java.io.*
import java.net.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteControlManager: Runnable {
    lateinit var server: ServerSocket
    private val port: Int = 56000
    private val isServerAlive = AtomicBoolean(false) // thread-safe boolean

    // Коды команд.
    private val codeMsg: Byte = 1

    private fun closeServer() {
        if(!server.isClosed) {
            try {
                server.close()
                println("Server stopped.")
            } catch (ex: IOException) {
                println("Server wasn't stopped.")
            }
        }
    }

    // Для получения адреса компьютера в локальной сети достаточно
    // воспользоваться методом getHostAddress() класса InetAddress.

    // Получение внешнего IP - не такая простая задача.
    // Для получения внешного IP необходимо подключиться к какому-нибудь
    // серверу в интернете, который и вернет IP, под которым он видит
    // данный компьютер.
    //
    // Подключимся к серверу (сайту http://myip.by/), предоставляющему
    // услугу определения IP, получим ответ в виде html кода и выделим
    // из этого кода IP адрес.

    fun getCurrentIP(): String {
        var result = ""
        try {
            var reader: BufferedReader? = null
            try {
                val url = URL("https://myip.by/")
                val inputStream: InputStream
                inputStream = url.openStream()
                reader = BufferedReader(InputStreamReader(inputStream))
                val allText = StringBuilder()
                val buffer = CharArray(1024)

                var count: Int = reader.read(buffer)
                while (count != -1) {
                    allText.append(buffer, 0, count)
                    count = reader.read(buffer)
                }

                // Строка, содержащая IP, имеет следующий вид:
                // <a href="./whois?176.101.127.1">whois 176.101.127.1</a>
                val indStart: Int = allText.indexOf("\">whois ")
                val indEnd: Int = allText.indexOf("</a>", indStart)

                val ipAddress = String(allText.substring(indStart + "\">whois ".length,
                        indEnd).toCharArray())

                // Проверяем, что выбранный текст является IP-адресом.
                if (ipAddress.split(".").size >= 4) { // минимальная (неполная)
                    result = ipAddress
                }
            } catch (ex: MalformedURLException) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            } finally {
                try {
                    reader?.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    override fun run() {
        server = ServerSocket()
        // Создаём конечную точку с IP-адресом и портом.
        val socketAddress = InetSocketAddress(InetAddress.getLocalHost().hostAddress, port)
        server.reuseAddress = true
        try {
            server.bind(socketAddress) // Привязываем серверный сокет к настроенной конечной точке.
            if (server.isBound) {
                isServerAlive.set(true)

                println("Server ${server.inetAddress.hostAddress}:$port started.")
                println("Wait for connection...")

                while (isServerAlive.get()) {
                    // Ждем соединение.
                    val client: Socket = server.accept()

                    thread {
                        runClientHandler(client)
                    }
                }
                /*
                // Пока работа сервера не прекращена, получаем сообщения от клиента.
                while (isServerAlive.get()) {

                    // Получаем поток чтения сокета.
                    val inputStream = DataInputStream(handler.getInputStream())

                    if (inputStream.available() != 0) {
                        when (inputStream.readByte()) {
                            codeMsg -> { // Клиент отправил сообщение.
                                val data = StringBuilder()
                                val buffer = ByteArray(1024)
                                var count: Int = inputStream.read(buffer) // Читаем данные сообщения.
                                while (count != -1) {
                                    data.append(buffer, 0, count)
                                    count = inputStream.read(buffer)
                                }

                                if (data.isNotEmpty()) { // Преобразуем полученный набор байт в строку.
                                    val message = String(buffer, StandardCharsets.UTF_8)
                                    println(message)
                                }
                            }
                        }
                    }
                }

                // Освобождаем сокет клиента.
                handler.close()*/
            }
        } catch (ex: Exception) {
            println(ex.message)
            println(ex.printStackTrace())
        }
        finally {
            closeServer()
        }
    }

    private fun runClientHandler(client : Socket) {
        val reader = Scanner(client.getInputStream())
        val writer: OutputStream = client.getOutputStream()

        // Пока работа сервера не прекращена, получаем сообщения от клиента.
        while (isServerAlive.get() && !client.isClosed) {
            try {
                var message = reader.nextLine()
                val code = (message[0] - '0').toByte()
                message = message.removeRange(0, 1)
                when (code) {
                    codeMsg -> { // Клиент отправил сообщение.
                        println(message)
                        writer.write((message + '\n').toByteArray(Charsets.UTF_8))
                        /*  val data = StringBuilder()
                        val buffer = ByteArray(1024)
                        var count: Int = reader.(buffer) // Читаем данные сообщения.
                        while (count != -1) {
                            data.append(buffer, 0, count)
                            count = inputStream.read(buffer)
                        }

                        if (data.isNotEmpty()) { // Преобразуем полученный набор байт в строку.
                            val message = String(buffer, StandardCharsets.UTF_8)
                            println(message)
                        }*/
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        if (!client.isClosed) {
            try {
                // Освобождаем сокет клиента.
                // Закрытие сокета приведёт также к закрытию потоков ввода и вывода.
                client.close()
                println("Client closed.")
            } catch (ex: IOException) {
                println("Client wasn't closed.")
            }
        }
    }

    fun stop() {
        isServerAlive.compareAndSet(true, false)
        closeServer()
    }
}