package scalatro

import controller.SingleRoundController
import model.round.RoundAction
import view.{FxController, FxView}

import cats.effect.IO
import cats.effect.std.Queue
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

    // Functional program bootstrapping
    val program: IO[Unit] =
      for
        queue <- Queue.unbounded[IO, RoundAction]
        view = FxView(fxController, queue) // injects queue into fxController
        ctrl = SingleRoundController(view, queue)
        _ <- ctrl.start()
      yield ()

    // This instruction must be the last one, since we are at top-level entry-point
    program.unsafeRunAndForget()

object SceneRouter:

  def switchTo(scene: Scene)(fxml: String): Unit =
    val root: Parent = FXMLLoader.load(getClass.getResource(fxml))
    scene.setRoot(root)
