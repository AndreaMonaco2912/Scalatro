package scalatro

import java.net.URL
import java.util.ResourceBundle
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label


class ScalatroController extends Initializable:
  @FXML private var label: Label = null

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    val javaVersion = System.getProperty("java.version")
    val javafxVersion = System.getProperty("javafx.version")
    label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".")