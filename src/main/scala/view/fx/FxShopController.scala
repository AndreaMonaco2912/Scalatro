package scalatro
package view.fx

import app.Msg.{ManagementAction, ShopAction}

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.VBox
import view.{ImageViews, Images}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxShopController extends Initializable, Dispatcher:
  @FXML private var cardPackButton: Button = uninitialized
  @FXML private var planetPackButton: Button = uninitialized
  @FXML private var jokerPackButton: Button = uninitialized
  @FXML private var skipButton: Button = uninitialized
  @FXML private var handLevelsButton: Button = uninitialized
  @FXML private var deckHost: VBox = uninitialized

  private val clickableDeck = ClickableDeck(dispatch)

  protected def imageNode(image: Image): ImageView =
    ImageViews(image, 136, 200)

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    cardPackButton.setGraphic(renderStandardPack)
    planetPackButton.setGraphic(renderPlanetPack)
    jokerPackButton.setGraphic(renderJokerPack)
    cardPackButton.setOnAction(_ => dispatch(ShopAction.OpenCardPack))
    planetPackButton.setOnAction(_ => dispatch(ShopAction.OpenPlanetPack))
    jokerPackButton.setOnAction(_ => dispatch(ShopAction.OpenJokerPack))
    skipButton.setOnAction(_ => dispatch(ShopAction.SkipShop))
    handLevelsButton.setOnAction(_ => dispatch(ManagementAction.ShowLevels))
    clickableDeck.mount(deckHost)

  private def renderStandardPack: ImageView =
    imageNode(Images.pack("Standard", "Normal", 1))

  private def renderPlanetPack: ImageView =
    imageNode(Images.pack("Celestial", "Normal", 1))

  private def renderJokerPack: ImageView =
    imageNode(Images.pack("Buffoon", "Normal", 1))
