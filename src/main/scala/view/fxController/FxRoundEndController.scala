package scalatro
package view.fxController

import app.Msg.RoundEndAction

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

abstract class FxRoundEndController[A] extends Initializable, Bindable[A]:

  /** Realised by each subclass with its @FXML-injected button. */
  protected def button: Button

  /** The single action this screen emits when the button is pressed. */
  protected def action: A

  private var actionQueue: Option[Queue[IO, A]] = None

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    button.setOnAction(_ => onClick())

  private def onClick(): Unit =
    offer(action)

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundWonController extends FxRoundEndController[RoundEndAction]:
  @FXML private var nextRoundButton: Button = uninitialized
  override protected def button: Button = nextRoundButton
  override protected def action: RoundEndAction = RoundEndAction.NextRound

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundLostController extends FxRoundEndController[RoundEndAction]:
  @FXML private var restartButton: Button = uninitialized
  override protected def button: Button = restartButton
  override protected def action: RoundEndAction = RoundEndAction.Restart
