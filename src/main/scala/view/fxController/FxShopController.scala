package scalatro
package view.fxController

import app.Msg.ShopAction

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button
import javafx.scene.layout.VBox

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxShopController extends Initializable, Dispatcher, ClickableDeck:
  @FXML private var cardPackButton: Button = uninitialized
  @FXML private var planetPackButton: Button = uninitialized
  @FXML private var jokerPackButton: Button = uninitialized
  @FXML private var skipButton: Button = uninitialized
  @FXML private var deckHost: VBox = uninitialized

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    cardPackButton.setOnAction(_ => dispatch(ShopAction.OpenCardPack))
    planetPackButton.setOnAction(_ => dispatch(ShopAction.OpenPlanetPack))
    jokerPackButton.setOnAction(_ => dispatch(ShopAction.OpenJokerPack))
    skipButton.setOnAction(_ => dispatch(ShopAction.SkipShop))
    mountDeck(deckHost)
