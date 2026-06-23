package scalatro
package controller

import model.commons.{Deck, Score}
import model.commons.Score.Score
import model.game.{Blind, Game, GameResult, GameState, RoundWonAction}
import model.round.{Round, RoundAction}
import view.{FxRoundWonView, FxView, View}

import cats.effect.IO
import cats.effect.std.Queue

trait Controller[S, A]:
  def start(): IO[S]

class SingleRoundController(
    view: View[Round],
    actionQueue: Queue[IO, RoundAction],
    gameState: GameState
) extends Controller[Round, RoundAction]:

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

class GameController(gameViews: GameViews)
    extends Controller[GameResult, RoundAction]:

  private def playRound(gameState: GameState): IO[Score] =
    for
      queue <- Queue.unbounded[IO, RoundAction]
      ctrl <- gameViews.gameplay
      view = FxView(ctrl, queue)
      src = SingleRoundController(view, queue, gameState)
      finalRound <- src.start()
    yield finalRound.score

  private def showRoundWon(blind: Blind): IO[Unit] =
    for
      queue <- Queue.unbounded[IO, RoundWonAction]
      ctrl <- gameViews.roundWon
      view = FxRoundWonView(ctrl, queue)
      action <- queue.take
    yield ()

  val game = Game(state => playRound(state), showRoundWon)

  override def start(): IO[GameResult] = game.play()
