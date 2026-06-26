package scalatro
package view

import model.commons.*

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Node
import javafx.scene.control.{Button, Label, Tooltip}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
abstract class FxPackController[A] extends Initializable:

  @FXML private var packBox: HBox = uninitialized
  @FXML private var continueButton: Button = uninitialized

  protected def renderItem(item: A): Node

  protected def imageNode(image: Image): ImageView =
    val iv = new ImageView(image)
    iv.setFitWidth(85)
    iv.setFitHeight(125)
    iv.setPreserveRatio(true)
    iv.getStyleClass.add("pack-card")
    iv

  private var actionQueue: Option[Queue[IO, Unit]] = None

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    continueButton.setOnAction(_ => onContinue())

  def setActionQueue(queue: Queue[IO, Unit]): Unit =
    actionQueue = Some(queue)

  def showItems(items: Seq[A]): Unit =
    Platform.runLater { () =>
      packBox.getChildren.clear()
      items.foreach(item => packBox.getChildren.add(renderItem(item)))
    }

  private def onContinue(): Unit =
    actionQueue.foreach(_.offer(()).unsafeRunAndForget())

class FxCardPackController extends FxPackController[Card]:
  override protected def renderItem(card: Card): Node =
    imageNode(Images.card(card))

class FxPlanetPackController extends FxPackController[Planet]:
  override protected def renderItem(planet: Planet): Node =
    val label = new Label(s"$planet\n${planet.handType}")
    label.getStyleClass.add("planet-card")
    label

class FxJokerPackController extends FxPackController[Joker]:
  override protected def renderItem(joker: Joker): Node =
    val iv = imageNode(Images.joker(joker))
    Tooltip.install(iv, new Tooltip(joker.description))
    iv
