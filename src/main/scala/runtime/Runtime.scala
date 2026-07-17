package scalatro
package runtime

import app.Msg.RoundAction
import app.{Cmd, Model, Msg, Update}
import model.game.GameState
import model.rng.ScalatroRng
import model.rng.Types.Seed
import model.round.{RoundManager, RoundState}
import model.shop.Shop
import view.{FxView, ScreenRouter}

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global

/** The controller of game loop and manager of the application run.
  *
  * Owns the message loop: it takes [[Msg]]s from the queue, computes the next
  * [[Model]] and resulting [[Cmd]] with the [[Update]], it passes the obtained
  * [[Model]] to the view that renders it. In the end it processes the resulting
  * [[Cmd]] of the [[Update]] and eventually enqueue a corresponding [[Msg]].
  *
  * @param screens
  *   the screen loader used by the view
  * @param seed
  *   the seed corresponding to the game's random events
  */
class Runtime(screens: ScreenRouter, seed: Seed = Seed.random):
  private given ScalatroRng = ScalatroRng(seed)

  /** Starts the gameplay loop.
    *
    * @return
    *   an IO that never terminates.
    */
  def run: IO[Unit] =
    for
      queue <- Queue.unbounded[IO, Msg]
      dispatch = (m: Msg) => queue.offer(m).unsafeRunAndForget()
      view = FxView(screens, dispatch)
      (model0, cmd0) = Update.init
      _ <- perform(cmd0, queue, view)
      _ <- loop(model0, queue, view)
    yield ()

  private def loop(
      model: Model,
      queue: Queue[IO, Msg],
      view: FxView
  ): IO[Unit] =
    for
      msg <- queue.take
      (next, cmd) = Update.update(model, msg)
      _ <- view.render(next)
      _ <- perform(cmd, queue, view)
      _ <- loop(next, queue, view)
    yield ()

  private def perform(cmd: Cmd, queue: Queue[IO, Msg], view: FxView): IO[Unit] =
    cmd match
      case Cmd.NoOp           => IO.unit
      case Cmd.DealFirstRound => runFirstRound(view, queue)
      case Cmd.Deal(gs)       => runRound(view, queue, gs.advanceBlind)
      case Cmd.BuildShop(gs)  =>
        IO(Shop.default(gs.shopInformation, gs.selectionPolicies))
          .flatMap(s => queue.offer(Msg.InternalEffect.ShopReady(gs, s)))

  private def runFirstRound(view: FxView, queue: Queue[IO, Msg]): IO[Unit] =
    runRound(view, queue, GameState.initial)

  private def runRound(
      view: FxView,
      queue: Queue[IO, Msg],
      gs: GameState
  ): IO[Unit] =
    for
      ctrl <- view.enterGameplay
      roundQueue <- Queue.unbounded[IO, RoundAction]
      _ <- IO(ctrl.setActionQueue(roundQueue))
      roundManager = RoundManager(r => IO(ctrl.update(r)), roundQueue.take)
      finalRound <- roundManager.startRound(RoundState(gs.shuffleDeck))
      _ <- queue.offer(outcome(finalRound))
    yield ()

  private def outcome(roundState: RoundState): Msg =
    if roundState.gameState.blindProgression.isBeaten(roundState.score)
    then Msg.InternalEffect.RoundWon(roundState)
    else Msg.InternalEffect.RoundLost(roundState)
