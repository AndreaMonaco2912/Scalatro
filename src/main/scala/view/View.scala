package scalatro
package view

import model.round.{Round, RoundAction}

import cats.effect.IO
import cats.effect.std.Queue

trait View[S, A]:
  def render(state: S): IO[Unit]

class HeadlessView extends View[Round, RoundAction]:
  override def render(round: Round): IO[Unit] =
    IO.println(s"[Test Render] Score is: ${round.score}, hand is ${round.hand}")

class FxView(
              controller: FxController,
              actionQueue: Queue[IO, RoundAction]
) extends View[Round, RoundAction]:

  controller.setActionQueue(actionQueue)

  override def render(round: Round): IO[Unit] =
    IO(controller.update(round))
