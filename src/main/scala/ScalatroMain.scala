package scalatro

import controller.GameController
import view.fxController.*
import view.GameViews

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

@main
def main(): Unit = MainApp.main(Array())

object MainApp extends JFXApp3:
  override def start(): Unit =
    this.stage = PrimaryStage()
    val fxmlUrl = getClass.getResource("/scalatro/scene.fxml")
    val cssUrl = getClass.getResource("/scalatro/styles.css")
    val loader = new FXMLLoader(fxmlUrl)
    val root: Parent = loader.load()

    val scene = Scene(root)
    scene.getStylesheets.add(cssUrl.toExternalForm)
    stage.setTitle("ScalaFX and SBT")
    stage.setScene(scene)
    stage.show()

    val fxController: FxController = loader.getController

    val program: IO[Unit] =
      for ctrl <- GameController(GameViews(scene)).start()
      yield ()

    program.unsafeRunAndForget()
