package main.kotlin

import java.io.*
import java.net.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class RemoteControlManager: Runnable {
    private lateinit var server: ServerSocket
    private val isServerAlive = AtomicBoolean(false) // thread-safe boolean
    private val clientMap : MutableMap<String, Socket> = mutableMapOf()

    companion object {
        private const val port: Int = 56000

        // Коды команд.
        private const val codePing: Byte = 1
        private const val codeHello: Byte = 2
        private const val codeOk: Byte = 3
        private const val codeError: Byte = 4
        private const val codeBye: Byte = 5
        private const val codeMessage: Byte = 6
    }

    private fun closeServer() {
        if (!server.isClosed) {
            for (client in clientMap.values) {
                val writer: OutputStream = client.getOutputStream()
                writer.write((codeBye.toString() + '\n').toByteArray(Charsets.UTF_8))
                client.close()
                println("Client ${client.inetAddress.hostAddress} closed.")
            }
            server.close()
            println("\nServer stopped.")
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

                println("\nServer ${server.inetAddress.hostAddress}:$port started")
                println("Wait for connection...")

                while (isServerAlive.get()) {
                    // Ждем соединение.
                    val client: Socket = server.accept()
                    try {
                        // Проверяем цель подключения клиента: пинг или подключение.
                        val reader = Scanner(client.getInputStream())
                        val writer: OutputStream = client.getOutputStream()
                        val request = reader.nextLine()
                        when ((request[0] - '0').toByte()) {
                            codePing -> {
                                writer.write((codeOk.toString()
                                        + server.inetAddress.hostName
                                        + '\n').toByteArray(Charsets.UTF_8))
                                println("\nPing from ${client.inetAddress.hostAddress}")
                                client.close()
                            }
                            codeHello -> {
                                if (clientMap.size < 2) {
                                    val name = request.removeRange(0, 1) +
                                            " (${client.inetAddress.hostAddress})"
                                    clientMap[name] = client
                                    writer.write((codeOk.toString()
                                            + server.inetAddress.hostName
                                            + '\n').toByteArray(Charsets.UTF_8))
                                    println("\nClient $name connected")
                                    println("Number of clients: ${clientMap.size}")
                                    thread {
                                        runClientHandler(client)
                                        clientMap.remove(name)
                                        println("Number of clients: ${clientMap.size}")
                                    }
                                } else {
                                    writer.write((codeError.toString() +
                                            "Уже подключено 2 устройства." + '\n')
                                            .toByteArray(Charsets.UTF_8))
                                    println("Client ${client.inetAddress.hostAddress} cannot be connected " +
                                            "due to restrictions on the number of clients")
                                    println("Number of clients: ${clientMap.size}")
                                    client.close()
                                }
                            }
                        }
                    }
                    catch(ex: Exception) {
                        client.close()
                    }
                }
            }
        } catch (ex: Exception) {
            println(ex.message)
        }
        finally {
            closeServer()
        }
    }

    private fun runClientHandler(client : Socket) {
        val reader = Scanner(client.getInputStream())
        val writer: OutputStream = client.getOutputStream()

        // Пока работа сервера не прекращена и клиент не отключился,
        // получаем сообщения от клиента и обрабатываем их.
        while (isServerAlive.get() && !client.isClosed) {
            try {
                if (reader.hasNextLine()) {
                    var message = reader.nextLine()
                    val code = (message[0] - '0').toByte()
                    message = message.removeRange(0, 1)
                    when (code) {
                        codeMessage -> { // Клиент отправил сообщение.
                            println(message)
                            writer.write((message + '\n').toByteArray(Charsets.UTF_8))
                        }
                        codeBye -> { // Клиент отключился.
                            client.close()
                            println("Client ${client.inetAddress.hostAddress} " +
                                    "disconnected")
                        }
                    }
                }
            } catch (ex: Exception) {
                println(ex.message)
            }
        }
    }

    fun stop() {
        isServerAlive.compareAndSet(true, false)
        closeServer()
    }
}