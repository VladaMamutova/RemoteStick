package main.kotlin

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.MalformedURLException
import java.net.URL

class RemoteControlManager {
    private val inetAddress: InetAddress = InetAddress.getLocalHost()

    init {
        println("Host name: ${inetAddress.hostName}, " +
                "Current IP address : ${inetAddress.hostAddress}")
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
}