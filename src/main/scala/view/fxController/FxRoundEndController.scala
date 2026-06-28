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

abstract class FxRoundEndController extends Initializable, Dispatcher:

  /** Realised by each subclass with its @FXML-injected button. */
  protected def button: Button
  protected def message: RoundEndAction

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    button.setOnAction(_ => dispatch(message))

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundWonController extends FxRoundEndController:
  @FXML private var nextRoundButton: Button = uninitialized
  override protected def button: Button = nextRoundButton
  override protected def message: RoundEndAction = RoundEndAction.NextRound

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundLostController extends FxRoundEndController:
  @FXML private var restartButton: Button = uninitialized
  override protected def button: Button = restartButton
  override protected def message: RoundEndAction = RoundEndAction.Restart
