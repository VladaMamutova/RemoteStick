package main.kotlin

import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.concurrent.atomic.AtomicBoolean


// Главный класс приложения JavaFX наследуется от javafx.application.Application
class Main : Application() {
    private lateinit var serverThread : Thread
    private lateinit var remoteControlManager: RemoteControlManager

    override fun start(stage: Stage?) {
        // Stage представляет пользовательский интерфейс,
        // является контейнером верхнего уровня для всех графических элементов.

        // Установливаем сцену для объекта Stage.
        stage?.scene = Scene(load<Parent?>(Main::class.java.getResource(layout)))
        stage?.title = "First Application"
        stage?.width = 300.0
        stage?.height = 250.0
        stage?.show() // Отображаем окно на экране.

        println("Host address: " + InetAddress.getLocalHost().hostAddress)
        println("Host name: " + InetAddress.getLocalHost().hostName)
        val n = NetworkInterface.getNetworkInterfaces()
        while (n.hasMoreElements()) {
            val e = n.nextElement()
            val a = e.inetAddresses
            while (a.hasMoreElements()) {
                val addr = a.nextElement()
                println("  " + addr.hostAddress)
            }
        }

        remoteControlManager = RemoteControlManager()
        println("External IP: ${remoteControlManager.getCurrentIP()}")
        serverThread = Thread(remoteControlManager)
        serverThread.start()
    }

    override fun stop() {
        remoteControlManager.stop()
        super.stop()
    }
    companion object { // для создания статического метода main, который должна увидеть JVM.
        private const val layout = "../resources/Main.fxml"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java) // Здесь вызывается метод start.
        }
    }
}
