package scalatro
package view

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button
import model.game.{RoundLostAction, RoundWonAction}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

abstract class FxRoundEndController[A] extends Initializable:

  /** Realised by each subclass with its @FXML-injected button. */
  protected def button: Button

  /** The single action this screen emits when the button is pressed. */
  protected def action: A

  private var actionQueue: Option[Queue[IO, A]] = None

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    button.setOnAction(_ => onClick())

  def setActionQueue(queue: Queue[IO, A]): Unit =
    actionQueue = Some(queue)

  private def onClick(): Unit =
    actionQueue.foreach(_.offer(action).unsafeRunAndForget())

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundWonController extends FxRoundEndController[RoundWonAction]:
  @FXML private var nextRoundButton: Button = uninitialized
  override protected def button: Button = nextRoundButton
  override protected def action: RoundWonAction = RoundWonAction.NextRound

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundLostController extends FxRoundEndController[RoundLostAction]:
  @FXML private var restartButton: Button = uninitialized
  override protected def button: Button = restartButton
  override protected def action: RoundLostAction = RoundLostAction.NewRun
