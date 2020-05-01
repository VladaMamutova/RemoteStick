package main.kotlin

import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.stage.Stage
import java.net.InetAddress

// Главный класс приложения JavaFX наследуется от javafx.application.Application
class Main : Application() {
    private lateinit var remoteControlManager: RemoteControlManager

    override fun start(stage: Stage?) {
        // Stage представляет пользовательский интерфейс,
        // является контейнером верхнего уровня для всех графических элементов.

        // Установливаем сцену для объекта Stage.
        stage?.scene = Scene(load(Main::class.java.getResource(layout)))
        stage?.title = "Remote Stick"
        stage?.width = 300.0
        stage?.height = 250.0
        stage?.show() // Отображаем окно на экране.

        println("Host address: " + InetAddress.getLocalHost().hostAddress)
        println("Host name: " + InetAddress.getLocalHost().hostName)

        remoteControlManager = RemoteControlManager()
        println("External IP: ${remoteControlManager.getCurrentIP()}")

        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Resource Path"
        alert.headerText = null
        //alert.contentText = System.getProperty("user.dir") + ("/lib/win32.dll").toString()
        alert.contentText = Main::class.java.getResource("/win32.dll").toString()
        alert.showAndWait()

        try {
            Win32()
            with(alert){
                title = "Library Path"
                contentText = Win32.dllPath
                showAndWait()
            }

            Win32().leftDoubleClick(0, 0)
        } catch (ex: Exception) {
            val alert2 = Alert(Alert.AlertType.ERROR)
            alert2.title = "Exception"
            alert2.headerText = null
            alert2.contentText = ex.message
            alert2.showAndWait()
        }
        catch (th: Throwable){
            val alert2 = Alert(Alert.AlertType.ERROR)
            alert2.title = "Throwable"
            alert2.headerText = null
            alert2.contentText = th.message
            alert2.showAndWait()
        }
        /* Thread.sleep(1000)
         Win32().leftDoubleClick(300, 300)*/
        //Thread(remoteControlManager).start()
    }

    override fun stop() {
        //remoteControlManager.stop()
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
