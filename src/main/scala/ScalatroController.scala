package scalatro

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label, TextField}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

class ScalatroController extends Initializable:
  @FXML private var label: Label = uninitialized
  @FXML private var button: Button = uninitialized

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    val javaVersion = System.getProperty("java.version")
    val javafxVersion = System.getProperty("javafx.version")
    label.setText(
      "Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + "."
    )

    button.setOnMouseClicked(_ =>
      SceneRouter.switchTo(button.getScene)("/scalatro/start.fxml")
    )

class StartController extends Initializable:
  @FXML private var mirror: Label = uninitialized
  @FXML private var input: TextField = uninitialized

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit =
    mirror.textProperty().bindBidirectional(input.textProperty())
