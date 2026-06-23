package scalatro
package controller

import model.commons.{Deck, Score}
import model.commons.Score.Score
import model.game.{
  Blind,
  Game,
  GameHandler,
  GameResult,
  GameState,
  RoundLostAction,
  RoundWonAction
}
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
    extends Controller[GameResult, RoundAction]
    with GameHandler:

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

  override def start(): IO[GameResult] = Game(this).play()
