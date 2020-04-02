package main.kotlin

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import java.net.InetAddress

class MainController {
    @FXML
    lateinit var btn: Button

    @FXML
    lateinit var pcName: Label

    @FXML
    private fun click(event: ActionEvent) {
        btn.text = "You've clicked!"
    }

    @FXML
    fun initialize() {
        pcName.text = InetAddress.getLocalHost().hostName
    }
}
