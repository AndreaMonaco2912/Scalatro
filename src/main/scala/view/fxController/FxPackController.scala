package scalatro
package view.fxController

import app.Msg.PackSelection
import model.commons.*
import view.Images

import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Node
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
abstract class FxPackController[A]
    extends Initializable,
      Dispatcher,
      ClickableDeck:

  @FXML private var packBox: HBox = uninitialized
  @FXML private var skipButton: Button = uninitialized
  @FXML private var deckHost: VBox = uninitialized

  protected def renderItem(item: A): Node
  protected def selectMsg(item: A): PackSelection

  protected def imageNode(image: Image): ImageView =
    val iv = new ImageView(image)
    iv.setFitWidth(85)
    iv.setFitHeight(125)
    iv.setPreserveRatio(true)
    iv.getStyleClass.add("pack-card")
    iv

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    skipButton.setOnAction(_ => dispatch(PackSelection.SkipPack))
    mountDeck(deckHost)

  def showItems(items: Seq[A]): Unit =
    Platform.runLater { () =>
      packBox.getChildren.clear()
      items.foreach { item =>
        val node = renderItem(item)
        node.setOnMouseClicked(_ => dispatch(selectMsg(item)))
        packBox.getChildren.add(node)
      }
    }

class FxCardPackController extends FxPackController[Card]:
  override protected def renderItem(card: Card): Node = imageNode(
    Images.card(card)
  )
  override protected def selectMsg(card: Card): PackSelection =
    PackSelection.SelectCard(card)

class FxPlanetPackController extends FxPackController[Planet]:
  override protected def renderItem(planet: Planet): Node = imageNode(
    Images.planet(planet)
  )
  override protected def selectMsg(planet: Planet): PackSelection =
    PackSelection.SelectPlanet(planet)

class FxJokerPackController extends FxPackController[Joker]:
  override protected def renderItem(joker: Joker): Node =
    val iv = imageNode(Images.joker(joker))
    Tooltip.install(iv, new Tooltip(joker.description))
    iv
  override protected def selectMsg(joker: Joker): PackSelection =
    PackSelection.SelectJoker(joker)
