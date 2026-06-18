package scalatro
package controller

import model.commons.{Deck, Score}
import model.commons.Score.Score
import model.game.{Game, GameResult, GameState}
import model.round.{Round, RoundAction}
import view.View

import cats.effect.IO
import cats.effect.std.Queue

import scala.util.Random

trait Controller[S, A]:
  def start(): IO[S]

class SingleRoundController(
    view: View[Round, RoundAction],
    actionQueue: Queue[IO, RoundAction],
    gameState: GameState
) extends Controller[Round, RoundAction]:

  given Random = new Random()
  private val (hand, deck) = gameState.deck.draw(8)

  private val initialRound = Round(
    Score.zero,
    hand,
    deck,
    gameState.blind
  )

  override def start(): IO[Round] =
    for
      roundManager = RoundManager(view.render, actionQueue.take)
      finalRound <- roundManager.startRound(initialRound)
    yield finalRound

case class GameViews(roundView: View[Round, RoundAction])

class GameController(gameViews: GameViews, actionQueue: Queue[IO, RoundAction])
    extends Controller[GameResult, RoundAction]:

  private def playRound(gameState: GameState): IO[Score] =
    for
      singleRoundController = SingleRoundController(
        gameViews.roundView,
        actionQueue,
        gameState
      )
      finalRound <- singleRoundController.start()
    yield finalRound.score

  val game = Game(state => playRound(state))

  override def start(): IO[GameResult] = game.play()
