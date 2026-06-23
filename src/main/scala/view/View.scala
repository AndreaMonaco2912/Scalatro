package scalatro
package view

import model.round.{Round, RoundAction}

import cats.effect.IO
import cats.effect.std.Queue
import model.game.{RoundLostAction, RoundWonAction}

trait View[S]:
  def render(state: S): IO[Unit]

class HeadlessView extends View[Round]:
  override def render(round: Round): IO[Unit] =
    IO.println(s"[Test Render] Score is: ${round.score}, hand is ${round.hand}")

class FxView(
    controller: FxController,
    actionQueue: Queue[IO, RoundAction]
) extends View[Round]:

  controller.setActionQueue(actionQueue)

  override def render(round: Round): IO[Unit] =
    IO(controller.update(round))

class RoundEndView[A](
    controller: FxRoundEndController[A],
    actionQueue: Queue[IO, A]
) extends View[Round]:
  controller.setActionQueue(actionQueue)
  override def render(state: Round): IO[Unit] = IO.unit
