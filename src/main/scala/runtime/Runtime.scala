package scalatro
package runtime

import app.Msg.RoundAction
import app.{Cmd, Model, Msg, Update}
import model.game.GameState
import model.rng.ScalatroRng
import model.rng.Types.Seed
import model.rng.seed.SelectionPolicies
import model.round.{RoundManager, RoundState}
import model.shop.Shop
import view.{FxView, GameViews}

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global

class Runtime(screens: GameViews, seed: Seed = Seed.random):
  private given ScalatroRng = ScalatroRng(seed)

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
      case Cmd.NoOp          => IO.unit
      case Cmd.Deal(gs)      => runRound(view, queue, gs)
      case Cmd.BuildShop(gs) =>
        given SelectionPolicies = gs.selectionPolicies
        IO(Shop.default(gs.shopInformation))
          .flatMap(s => queue.offer(Msg.InternalEffect.ShopReady(gs, s)))

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
