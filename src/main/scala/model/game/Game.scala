package scalatro
package model.game

import scala.util.Random
import model.commons.Score.Score
import model.round.Placeholder

import cats.data.State
import cats.syntax.all.*

class Game(val seed: Long):
  private val rng: Random = Random(seed)
  private given Random = rng

  def play(): GameResult =
    gameLoop.runA(GameState.initial).value

  private def gameLoop: State[GameState, GameResult] =
    for
      blind <- State.inspect[GameState, Blind](_.blind)
      _ <- GameState.shuffleDeck
      score <- Placeholder.playRound
      result <-
        if blind.isBeaten(score)
        then GameState.advanceBlind >> gameLoop
        else State.pure[GameState, GameResult](GameResult(blind, score))
    yield result

case class GameResult(blind: Blind, finalScore: Score):
  def isGameLost: Boolean = !blind.isBeaten(finalScore)

object Game:
  def apply(seed: Long = Random.nextLong()): Game = new Game(seed)
