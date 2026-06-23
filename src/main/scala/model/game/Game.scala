package scalatro
package model.game

import scala.util.Random
import model.commons.Score.Score

import cats.effect.IO

class Game(
    val seed: Long,
    playRound: GameState => IO[Score],
    onRoundWon: Blind => IO[Unit] = _ => IO.unit
):
  private val rng: Random = Random(seed)
  private given Random = rng

  def play(): IO[GameResult] =
    gameLoop(GameState.initial)

  private def gameLoop(gameState: GameState): IO[GameResult] =
    val blind = gameState.blind
    val deck = gameState.deck
    val shuffledDeck = deck.shuffle
    val jokers = gameState.jokers
    val levels = gameState.levels

    for
      result <- playRound(GameState(shuffledDeck, blind, jokers, levels))
      outcome <-
        if blind.isBeaten(result) then
          onRoundWon(blind) *> gameLoop(
            GameState(deck, blind.next, jokers, levels)
          )
        else IO.pure(GameResult(blind, result))
    yield outcome

case class GameResult(blind: Blind, finalScore: Score)

object Game:
  def apply(
      playRound: GameState => IO[Score],
      onRoundWon: Blind => IO[Unit] = _ => IO.unit,
      seed: Long = Random.nextLong()
  ): Game = new Game(seed, playRound, onRoundWon)
