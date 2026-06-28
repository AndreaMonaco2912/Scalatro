package scalatro
package view.fxController

import app.Msg.ShopAction

import cats.effect.IO
import cats.effect.std.Queue
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxShopController extends Initializable, Bindable[ShopAction]:
  @FXML private var cardPackButton: Button = uninitialized
  @FXML private var planetPackButton: Button = uninitialized
  @FXML private var jokerPackButton: Button = uninitialized
  @FXML private var skipButton: Button = uninitialized

  private var actionQueue: Option[Queue[IO, ShopAction]] = None

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    cardPackButton.setOnAction(_ => offer(ShopAction.OpenCardPack))
    planetPackButton.setOnAction(_ => offer(ShopAction.OpenPlanetPack))
    jokerPackButton.setOnAction(_ => offer(ShopAction.OpenJokerPack))
    skipButton.setOnAction(_ => offer(ShopAction.SkipShop))
