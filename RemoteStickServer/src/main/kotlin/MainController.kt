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
    lateinit var name: Label
    lateinit var ipAddress: Label

    @FXML
    private fun click(event: ActionEvent) {
        btn.text = "You've clicked!"
    }

    @FXML
    fun initialize() {
        name.text = InetAddress.getLocalHost().hostName
        ipAddress.text = InetAddress.getLocalHost().hostAddress
    }
}
