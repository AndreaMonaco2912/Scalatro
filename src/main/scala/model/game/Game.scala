package scalatro
package model.game

import scala.util.Random
import model.commons.Score.Score

import cats.effect.IO
import model.shop.Shop

trait GameHandler:
  def playRound(state: GameState): IO[Score]
  def onRoundWon(blind: Blind): IO[Unit]
  def onRoundLost(blind: Blind): IO[Unit]
  def showShop(shop: Shop): IO[Unit]

class Game(handler: GameHandler, val seed: Long = Random.nextLong()):
  private val rng: Random = Random(seed)
  private given Random = rng

  def play(): IO[GameResult] = gameLoop(GameState.initial)

  private def gameLoop(gameState: GameState): IO[GameResult] =
    for
      result <- handler.playRound(gameState.shuffleDeck)
      outcome <-
        if gameState.blind.isBeaten(result) then handleWin(gameState)
        else handleLoss(gameState, result)
    yield outcome

  private def handleWin(gameState: GameState): IO[GameResult] =
    for
      _ <- handler.onRoundWon(gameState.blind)
      _ <- handler.showShop(Shop.default)
      result <- gameLoop(gameState.advanceBlind)
    yield result

  private def handleLoss(gameState: GameState, result: Score): IO[GameResult] =
    handler.onRoundLost(gameState.blind) >> IO.pure(
      GameResult(gameState.blind, result)
    )

case class GameResult(blind: Blind, finalScore: Score)
