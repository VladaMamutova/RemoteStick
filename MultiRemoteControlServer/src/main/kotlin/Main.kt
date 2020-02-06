package main.kotlin

import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.scene.Scene

// Главный класс приложения JavaFX наследуется от javafx.application.Application
class Main : Application() {
    private val layout = "../resources/Main.fxml"

    override fun start(stage: Stage?) {
        // Stage представляет пользовательский интерфейс,
        // является контейнером верхнего уровня для всех графических элементов.

        // Установливаем сцену для объекта Stage.
        stage?.scene = Scene(load<Parent?>(Main::class.java.getResource(layout)))
        stage?.title = "First Application"
        stage?.width = 300.0
        stage?.height = 250.0
        stage?.show() // Отображаем окно на экране.

        val m = RemoteControlManager()
        println("External IP: ${m.getCurrentIP()}")
    }

    companion object { // для создания статического метода main, который должна увидеть JVM.
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java) // Здесь вызывается метод start.
        }
    }
}
