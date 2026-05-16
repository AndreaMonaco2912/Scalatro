package scalatro

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Label

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized


class ScalatroController extends Initializable:
  @FXML private var label: Label = uninitialized

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    val javaVersion = System.getProperty("java.version")
    val javafxVersion = System.getProperty("javafx.version")
    label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".")