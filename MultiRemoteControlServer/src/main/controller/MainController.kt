package main.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button

class MainController {
    @FXML
    lateinit var btn: Button

    @FXML
    private fun click(event: ActionEvent) {
        btn.text = "You've clicked!"
    }
}
