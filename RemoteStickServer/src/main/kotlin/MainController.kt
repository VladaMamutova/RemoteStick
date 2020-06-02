package main.kotlin

import javafx.fxml.FXML
import javafx.scene.control.Label
import java.net.InetAddress

class MainController {

    @FXML
    lateinit var name: Label
    lateinit var ipAddress: Label
    lateinit var clientName: Label

    @FXML
    fun initialize() {
        name.text = InetAddress.getLocalHost().hostName
        ipAddress.text = InetAddress.getLocalHost().hostAddress
    }
}
