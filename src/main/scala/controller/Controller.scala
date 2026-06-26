package scalatro
package controller

import model.commons.Score.Score
import model.commons.{Deck, Pack, Score}
import model.game.*
import model.round.{Round, RoundAction, RoundManager}
import model.shop.{Shop, ShopActions}
import view.{
  FxPackController,
  FxRoundEndController,
  FxView,
  PackView,
  RoundEndView,
  ShopView,
  View
}

import cats.effect.IO
import cats.effect.std.Queue

/** A trait representing a functional controller
  * @tparam S
  *   the type of the results of the actions
  */
trait Controller[S]:
  /** Creates a template for running the computation in a functional way
    * @return
    *   an IO representing the computation
    */
  def start(): IO[S]

/** A controller for a single round
  * @param view
  *   the view
  * @param actionQueue
  *   the queue of events coming from the view
  * @param gameState
  *   the initial configuration of the round
  */
class SingleRoundController(
    view: View[Round],
    actionQueue: Queue[IO, RoundAction],
    gameState: GameState
) extends Controller[Round]:

  private val (hand, deck) = gameState.deck.draw(8)

  private val initialRound = Round(
    Score.zero,
    hand,
    deck,
    gameState
  )

  override def start(): IO[Round] =
    for
      roundManager = RoundManager(view.render, actionQueue.take)
      finalRound <- roundManager.startRound(initialRound)
    yield finalRound

class GameController(gameViews: GameViews)
    extends Controller[GameResult],
      GameHandler:

  override def playRound(gameState: GameState): IO[Score] =
    for
      queue <- Queue.unbounded[IO, RoundAction]
      ctrl <- gameViews.gameplay
      view = FxView(ctrl, queue)
      src = SingleRoundController(view, queue, gameState)
      finalRound <- src.start()
    yield finalRound.score

  override def onRoundWon(blind: Blind): IO[Unit] =
    showOutcome[RoundWonAction](gameViews.roundWon)

  override def onRoundLost(blind: Blind): IO[Unit] =
    showOutcome[RoundLostAction](gameViews.roundLost)

  private def showOutcome[A](
      getController: IO[FxRoundEndController[A]]
  ): IO[Unit] =
    for
      queue <- Queue.unbounded[IO, A]
      ctrl <- getController
      view = RoundEndView(ctrl, queue)
      _ <- queue.take
    yield ()

  override def showShop(shop: Shop): IO[Unit] =
    for
      queue <- Queue.unbounded[IO, ShopActions]
      controller <- gameViews.shop
      view = ShopView(controller, queue)
      action <- queue.take
      _ <- action match
        case ShopActions.OpenCardPack =>
          showPack(gameViews.cardPack, shop.cardPack)
        case ShopActions.OpenPlanetPack =>
          showPack(gameViews.planetPack, shop.planetPack)
        case ShopActions.OpenJokerPack =>
          showPack(gameViews.jokerPack, shop.jokerPack)
        case ShopActions.SkipShop => IO.unit
    yield ()

  private def showPack[A](
      getController: IO[FxPackController[A]],
      pack: Pack[A]
  ): IO[Unit] =
    for
      queue <- Queue.unbounded[IO, Unit]
      controller <- getController
      _ = PackView(controller, pack, queue)
      _ <- queue.take
    yield ()

  override def start(): IO[GameResult] = Game(this).play()
