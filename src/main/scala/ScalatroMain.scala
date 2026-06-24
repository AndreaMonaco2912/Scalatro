package scalatro

import controller.GameController
import view.{
  FxController,
  FxRoundEndController,
  FxRoundLostController,
  FxRoundWonController,
  FxShopController
}

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import javafx.application.Platform
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

object SceneRouter:

  def switchTo[C](scene: Scene)(fxml: String): IO[C] =
    IO.async_ { cb =>
      Platform.runLater { () =>
        try
          val loader = new FXMLLoader(getClass.getResource(fxml))
          val root: Parent = loader.load()
          scene.setRoot(root)
          cb(Right(loader.getController[C]))
        catch case t: Throwable => cb(Left(t))
      }
    }

class GameViews(scene: Scene):

  def gameplay: IO[FxController] =
    SceneRouter.switchTo[FxController](scene)("/scalatro/scene.fxml")

  def roundWon: IO[FxRoundWonController] =
    SceneRouter.switchTo[FxRoundWonController](scene)("/scalatro/roundWon.fxml")

  def roundLost: IO[FxRoundLostController] =
    SceneRouter.switchTo[FxRoundLostController](scene)(
      "/scalatro/roundLost.fxml"
    )

  def shop: IO[FxShopController] =
    SceneRouter.switchTo[FxShopController](scene)("/scalatro/shop.fxml")
