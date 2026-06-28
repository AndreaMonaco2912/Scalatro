package scalatro
package controller

import app.Msg.*
import model.commons.Score.Score
import model.commons.{Deck, Pack, Score}
import model.game.*
import model.round.{Round, RoundManager}
import model.shop.{Shop, ShopSelection}
import view.{FxView, View}
import view.fxController.{Bindable, FxPackController, FxRoundEndController}
import view.GameViews

import cats.effect.IO
import cats.effect.std.Queue
import app.Msg.PackSelection.{SelectCard, SelectJoker, SelectPlanet, SkipPack}
import model.shop.ShopSelection.*

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
  * @param render
  *   the function of rendering
  * @param actionQueue
  *   the queue of events coming from the view
  * @param gameState
  *   the initial configuration of the round
  */
class SingleRoundController(
    render: Round => IO[Unit],
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
      roundManager = RoundManager(render, actionQueue.take)
      finalRound <- roundManager.startRound(initialRound)
    yield finalRound
