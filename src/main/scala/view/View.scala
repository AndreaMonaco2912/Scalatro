package scalatro
package view

import model.round.{Round, RoundAction}
import view.fxController.FxController

import cats.effect.IO
import cats.effect.std.Queue

/** A trait representing a functional view
  * @tparam S
  *   the type of the state to render
  */
trait View[S]:
  /** Creates a template for rendering a state object in a functional way
    * @param state
    *   the object to render
    * @return
    *   an IO representing the result of the computation
    */
  def render(state: S): IO[Unit]

/** Represents a useless view which just prints the current state */
class HeadlessView extends View[Round]:
  override def render(round: Round): IO[Unit] =
    IO.println(s"[Test Render] Score is: ${round.score}, hand is ${round.hand}")

/** Represents the main view of the game, tied to JavaFx
  * @param controller
  *   the JavaFx controller
  * @param actionQueue
  *   the queue of action coming from the GUI
  */
class FxView(
    controller: FxController,
    actionQueue: Queue[IO, RoundAction]
) extends View[Round]:

  controller.setActionQueue(actionQueue)

  override def render(round: Round): IO[Unit] =
    IO(controller.update(round))
