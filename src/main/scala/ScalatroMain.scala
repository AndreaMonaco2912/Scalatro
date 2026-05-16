package scalatro

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object ScalatroMain:
  @main
  def main(): Unit =
    Application.launch(classOf[MainApp])

class MainApp extends Application:
  override def start(stage: Stage): Unit =
    val fxmlUrl = getClass.getResource("/scalatro/scene.fxml")
    val cssUrl = getClass.getResource("/scalatro/styles.css")

    val root: Parent = FXMLLoader.load(fxmlUrl)
    val scene = Scene(root)
    scene.getStylesheets.add(cssUrl.toExternalForm)
    stage.setTitle("JavaFX and Gradle")
    stage.setScene(scene)
    stage.show()
