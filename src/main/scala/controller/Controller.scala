package scalatro
package controller

import model.commons.{Deck, Score}
import model.commons.Score.Score
import model.game.{Blind, Game, GameResult, GameState, RoundLostAction, RoundWonAction}
import model.round.{Round, RoundAction}
import view.{FxRoundEndController, FxView, RoundEndView, View}

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

  private def showOutcome[A](getController: IO[FxRoundEndController[A]]): IO[Unit] =
    for
      queue <- Queue.unbounded[IO, A]
      ctrl <- getController
      view = RoundEndView(ctrl, queue)
      _ <- queue.take
    yield ()

  private def showRoundWon(blind: Blind): IO[Unit] =
    showOutcome[RoundWonAction](gameViews.roundWon)

  private def showRoundLost(blind: Blind): IO[Unit] =
    showOutcome[RoundLostAction](gameViews.roundLost)

  val game = Game(state => playRound(state), showRoundWon, showRoundLost)

  override def start(): IO[GameResult] = game.play()
