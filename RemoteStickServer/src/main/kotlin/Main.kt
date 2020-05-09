package main.kotlin

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader.load
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.stage.Stage
import main.kotlin.service.RemoteStickServer
import java.net.InetAddress
import kotlin.concurrent.thread


// Главный класс приложения JavaFX наследуется от javafx.application.Application
class Main : Application() {
    private lateinit var remoteControlManager: RemoteStickServer

    override fun start(stage: Stage?) {
        // Stage представляет пользовательский интерфейс,
        // является контейнером верхнего уровня для всех графических элементов.

        // Установливаем сцену для объекта Stage.
        stage?.scene = Scene(load(Main::class.java.getResource(layout)))
        stage?.title = "Remote Stick"
        stage?.width = 300.0
        stage?.height = 170.0
        stage?.show() // Отображаем окно на экране.

        println("Host address: " + InetAddress.getLocalHost().hostAddress)
        println("Host name: " + InetAddress.getLocalHost().hostName)

        remoteControlManager = RemoteStickServer()
        val clientName: Label = stage?.scene!!.lookup("#clientName") as Label
        clientName.text = remoteControlManager.getClientName()

        try {
            // Пытаемся загрузить библиотеку.
            Win32()
            Thread(remoteControlManager).start()
            thread {
                while (remoteControlManager.isServerAlive()) {
                    if (clientName.text != remoteControlManager.getClientName()) {
                        // Обновляем имя клиента в потоке приложения.
                        Platform.runLater {
                            clientName.text = remoteControlManager.getClientName()
                        }
                    } else {
                        Thread.sleep(500)
                    }
                }
            }
        } catch (throwable: Throwable) {
            var message: String = throwable.message.orEmpty()
            var inner: Throwable? = throwable.cause
            while (inner != null) {
                message += (if (message == "") "" else " ") + inner.message
                inner = inner.cause
            }
            val alert = Alert(Alert.AlertType.ERROR).apply {
                title = "Ошибка загрузки библиотеки win32.dll"
                contentText = "$message\nРабота приложения будет завершена."
                headerText = null
            }
            alert.showAndWait()
            Platform.exit()
        }
    }

    override fun stop() {
        remoteControlManager.stop()
        super.stop()
    }

    companion object { // для создания статического метода main, который должна увидеть JVM.
        private const val layout = "/main.fxml"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java) // Здесь вызывается метод start.
        }
    }
}
