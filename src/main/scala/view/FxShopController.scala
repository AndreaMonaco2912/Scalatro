package scalatro
package view

import model.shop.ShopActions

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxShopController extends Initializable:
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

  def setActionQueue(queue: Queue[IO, ShopActions]): Unit =
    actionQueue = Some(queue)

  private def offer(action: ShopActions): Unit =
    actionQueue.foreach(_.offer(action).unsafeRunAndForget())
