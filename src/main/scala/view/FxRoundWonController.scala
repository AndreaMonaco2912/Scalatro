package scalatro
package view

import cats.effect.std.Queue
import cats.effect.IO
import cats.effect.unsafe.implicits.global

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button

import model.game.RoundWonAction

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundWonController extends Initializable:

  @FXML private var nextRoundButton: Button = uninitialized

  private var actionQueue: Option[Queue[IO, RoundWonAction]] = None

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit =
    nextRoundButton.setOnAction(_ => onNextRound())

  def setActionQueue(queue: Queue[IO, RoundWonAction]): Unit =
    actionQueue = Some(queue)

  private def onNextRound(): Unit =
    actionQueue.foreach(
      _.offer(RoundWonAction.NextRound).unsafeRunAndForget()
    )