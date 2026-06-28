package scalatro
package controller

import app.Msg.*
import model.commons.Score.Score
import model.commons.{Deck, Pack, Score}
import model.game.*
import model.round.{Round, RoundManager}
import model.shop.{PackAction, Shop, ShopSelection}
import view.{FxView, View}
import view.fxController.{Bindable, FxPackController, FxRoundEndController}
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

trait GameHandler:
  def playRound(state: GameState): IO[Score]
  def onRoundWon(blind: Blind): IO[Unit]
  def onRoundLost(blind: Blind): IO[Unit]
  def showShop(shop: Shop): IO[Option[ShopSelection]]

class GameController(gameViews: GameViews)
    extends Controller[GameResult],
      GameHandler://TODO create a new separated GameHandler singleton

  override def playRound(gameState: GameState): IO[Score] =
    for
      queue <- Queue.unbounded[IO, RoundAction]
      ctrl <- gameViews.gameplay
      view <- FxView(ctrl, queue)
      src = SingleRoundController(view, queue, gameState)
      finalRound <- src.start()
    yield finalRound.score

  private def awaitActionWith[C, A](
      getController: IO[C],
      bind: (C, Queue[IO, A]) => Unit
  ): IO[A] =
    for
      queue <- Queue.unbounded[IO, A]
      ctrl <- getController
      _ <- IO(bind(ctrl, queue))
      action <- queue.take
    yield action

  private def awaitAction[A](getController: IO[Bindable[A]]): IO[A] =
    awaitActionWith(getController, _.setActionQueue(_))

  override def onRoundWon(blind: Blind): IO[Unit] =
    showOutcome[RoundEndAction](gameViews.roundWon)

  override def onRoundLost(blind: Blind): IO[Unit] =
    showOutcome[RoundEndAction](gameViews.roundLost)

  private def showOutcome[A](
      getController: IO[FxRoundEndController[A]]
  ): IO[Unit] =
    awaitAction[A](getController).void

  override def showShop(shop: Shop): IO[Option[ShopSelection]] =
    awaitAction[ShopAction](gameViews.shop)
      .flatMap { // TODO: improve this method readability
        case ShopAction.OpenCardPack =>
          showPack(gameViews.cardPack, shop.cardPack)
            .map(_.map(ShopSelection.CardSelected(_)))
        case ShopAction.OpenPlanetPack =>
          showPack(gameViews.planetPack, shop.planetPack)
            .map(_.map(ShopSelection.PlanetSelected(_)))
        case ShopAction.OpenJokerPack =>
          showPack(gameViews.jokerPack, shop.jokerPack)
            .map(_.map(ShopSelection.JokerSelected(_)))
        case ShopAction.SkipShop => IO.pure(None)
      }

  private def showPack[A](
      getController: IO[FxPackController[A]],
      pack: Pack[A]
  ): IO[Option[A]] =
    awaitActionWith[FxPackController[A], PackAction[A]](
      getController,
      (ctrl, queue) =>
        ctrl.setActionQueue(queue)
        ctrl.showItems(pack.items)
    ).map {
      case PackAction.Select(item) => Some(item)
      case PackAction.Skip         => None
    }

  override def start(): IO[GameResult] = Game(this).play()
