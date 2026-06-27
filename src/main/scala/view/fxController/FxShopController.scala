package scalatro
package view.fxController

import model.shop.ShopActions

import cats.effect.IO
import cats.effect.std.Queue
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxShopController extends Initializable, Bindable[ShopActions]:
  @FXML private var cardPackButton: Button = uninitialized
  @FXML private var planetPackButton: Button = uninitialized
  @FXML private var jokerPackButton: Button = uninitialized
  @FXML private var skipButton: Button = uninitialized

  private var actionQueue: Option[Queue[IO, ShopActions]] = None

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    cardPackButton.setOnAction(_ => offer(ShopActions.OpenCardPack))
    planetPackButton.setOnAction(_ => offer(ShopActions.OpenPlanetPack))
    jokerPackButton.setOnAction(_ => offer(ShopActions.OpenJokerPack))
    skipButton.setOnAction(_ => offer(ShopActions.SkipShop))
