package scalatro

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label, TextField}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

class ScalatroController extends Initializable:

  override def initialize(url: URL, rb: ResourceBundle): Unit = {}

class StartController extends Initializable:
  @FXML private var mirror: Label = uninitialized
  @FXML private var input: TextField = uninitialized

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit =
    mirror.textProperty().bindBidirectional(input.textProperty())
