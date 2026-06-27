package scalatro
package controller

import model.commons.Score.Score
import model.commons.{Deck, Pack, Score}
import model.game.*
import model.round.{Round, RoundAction, RoundManager}
import model.shop.{PackAction, Shop, ShopActions}
import view.{FxView, View}
import view.fxController.{FxPackController, FxRoundEndController}
import view.GameViews

import cats.effect.IO
import cats.effect.std.Queue

/** A trait representing a functional controller
  *
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

  private val (hand, deck) =
    gameState.deck.draw(gameState.handInformation.handSize)

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
      view <- FxView(ctrl, queue)
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
      _ <- IO(ctrl.setActionQueue(queue))
      action <- queue.take
    yield action

    IO.unit

  override def showShop(shop: Shop): IO[Unit] =
    for
      queue <- Queue.unbounded[IO, ShopActions]
      ctrl <- gameViews.shop
      _ <- IO(ctrl.setActionQueue(queue))
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
  ): IO[Option[A]] =
    for
      queue <- Queue.unbounded[IO, PackAction[A]]
      ctrl <- getController
      _ <- IO {
        ctrl.setActionQueue(queue)
        ctrl.showItems(pack.items)
      }
      action <- queue.take
    yield action match
      case PackAction.Select(item) => Some(item)
      case PackAction.Skip         => None

  override def start(): IO[GameResult] = Game(this).play()
