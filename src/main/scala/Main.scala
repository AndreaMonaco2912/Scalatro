package scalatro

import model.commons.Rank.Ace
import model.commons.Suit.{Diamonds, Hearts}
import model.commons.{Card, HandType, JokerType}
import model.rng.seed.{
  InitialHandWithCards,
  InitialHandWithHandType,
  JokerPackContains,
  SeedFinder
}
import runtime.Runtime
import view.{GameViews, Resources}

import cats.effect.unsafe.implicits.global
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object Main:
  def main(args: Array[String]): Unit =
    args.headOption match
      case Some("-s") | Some("--seed-search") =>
        ScalatroSeedSearch.main(Array.empty)
      case None  => ScalatroMain.main(Array.empty)
      case other =>
        System.err.println(s"Unknown mode: $other")
        System.err.println("Usage: java -jar scalatro.jar [-s|--seed-search]")
        sys.exit(1)

object ScalatroMain extends JFXApp3:
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

object ScalatroSeedSearch:
  def main(arg1: Array[String]): Unit =
    val constraints = Seq(
      InitialHandWithCards(Seq(Card(Ace, Hearts), Card(Ace, Diamonds)), 1),
      InitialHandWithHandType(HandType.FullHouse, 1),
      JokerPackContains(JokerType.CleverJoker, 1)
    )
    val seed = SeedFinder.findSeed(constraints)
    println(s"Found seed: $seed")
