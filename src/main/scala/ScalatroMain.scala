package scalatro

import runtime.Runtime
import view.{GameViews, Resources}

import cats.effect.unsafe.implicits.global
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

@main
def main(): Unit = MainApp.main(Array())

object MainApp extends JFXApp3:
  override def start(): Unit =
    this.stage = PrimaryStage()
    val cssUrl = getClass.getResource(Resources.stylesheet)
    // placeholder root; first render swaps it
    val scene = Scene(new StackPane(), 1400, 800)
    scene.getStylesheets.add(cssUrl.toExternalForm)
    stage.setTitle("Scalatro")
    stage.setScene(scene)
    stage.centerOnScreen()
    stage.show()

    Runtime(GameViews(scene)).run.unsafeRunAndForget()
