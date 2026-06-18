package scalatro
package model.game

import scala.util.Random
import model.commons.Score.Score

import cats.effect.IO

class Game(val seed: Long, playRound: GameState => IO[Score]):
  private val rng: Random = Random(seed)
  private given Random = rng

  def play(): IO[GameResult] =
    gameLoop(GameState.initial)

  private def gameLoop(gameState: GameState): IO[GameResult] =
    val blind = gameState.blind
    val deck = gameState.deck
    val shuffledDeck = deck.shuffle

    for {
      result <- playRound(GameState(shuffledDeck, blind))
      outcome <-
        if blind.isBeaten(result) then gameLoop(GameState(deck, blind.next))
        else IO.pure(GameResult(blind, result))
    } yield outcome

case class GameResult(blind: Blind, finalScore: Score)

object Game:
  def apply(
      playRound: GameState => IO[Score],
      seed: Long = Random.nextLong()
  ): Game = new Game(seed, playRound)
