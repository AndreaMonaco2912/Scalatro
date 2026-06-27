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

object FxView:
  /** Binds the controller to its action queue and returns a [[View]] that
    * pushes each new [[Round]] to the JavaFX GUI.
    *
    * The queue binding is a side effect, so it is sequenced inside `IO` and
    * runs exactly once, before the first render.
    *
    * @param controller
    *   the JavaFX controller
    * @param actionQueue
    *   the queue of actions coming from the GUI
    * @return
    *   an IO producing the bound view
    */
  def apply(
      controller: FxController,
      actionQueue: Queue[IO, RoundAction]
  ): IO[View[Round]] =
    IO(controller.setActionQueue(actionQueue)) as
      ((round: Round) => IO(controller.update(round)))
