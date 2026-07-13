package scalatro

import ScalatroApp.stage
import model.rng.Types.Seed
import runtime.Runtime
import view.{GameViews, Resources}

import cats.effect.unsafe.implicits.global
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object ScalatroApp extends JFXApp3:

  private def parseSeed: Seed =
    parameters.raw.toList match
      case Nil =>
        Seed.random

      case ("-s" | "--seed") :: Nil =>
        System.err.println("Missing seed value after -s argument")
        sys.exit(1)

      case ("-s" | "--seed") :: value :: _ =>
        value.toLongOption match
          case Some(seed) => Seed(seed)
          case None       =>
            System.err.println(s"Invalid seed: $value")
            sys.exit(1)

      case _ =>
        System.err.println("Invalid seed arguments")
        sys.exit(1)

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

    Runtime(GameViews(scene), parseSeed).run.unsafeRunAndForget()
