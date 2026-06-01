package scalatro

import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

@main
def main(): Unit =
  MainApp.main(Array())

object MainApp extends JFXApp3:
  override def start(): Unit =
    this.stage = PrimaryStage()
    val fxmlUrl = getClass.getResource("/scalatro/scene.fxml")
    val cssUrl = getClass.getResource("/scalatro/styles.css")

    val root: Parent = FXMLLoader.load(fxmlUrl)
    val scene = Scene(root)
    scene.getStylesheets.add(cssUrl.toExternalForm)
    stage.setTitle("ScalaFX and SBT")
    stage.setScene(scene)

    stage.show()

object SceneRouter:

  def switchTo(scene: Scene)(fxml: String): Unit =
    val root: Parent = FXMLLoader.load(getClass.getResource(fxml))
    scene.setRoot(root)
