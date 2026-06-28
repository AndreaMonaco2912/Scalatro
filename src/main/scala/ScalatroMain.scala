package scalatro

import cats.effect.unsafe.implicits.global
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import runtime.Runtime
import view.{GameViews, Resources}

@main
def main(): Unit = MainApp.main(Array())

object MainApp extends JFXApp3:
  override def start(): Unit =
    this.stage = PrimaryStage()
    val cssUrl = getClass.getResource(Resources.stylesheet)
    val scene = Scene(new StackPane()) // placeholder root; first render swaps it
    scene.getStylesheets.add(cssUrl.toExternalForm)
    stage.setTitle("Scalatro")
    stage.setScene(scene)
    stage.show()

    Runtime(GameViews(scene)).run.unsafeRunAndForget()