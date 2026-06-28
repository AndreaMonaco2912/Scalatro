package scalatro
package model.game

import model.commons.Score.Score
import model.shop.Shop
import controller.GameHandler

import scala.util.Random
import cats.effect.IO

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
      selection <- handler.showShop(Shop.default(gameState.shopInformation))
      nextState = gameState.selectItem(selection).advanceBlind
      result <- gameLoop(nextState)
    yield result

  private def handleLoss(gameState: GameState, result: Score): IO[GameResult] =
    handler.onRoundLost(gameState.blind) >> IO.pure(
      GameResult(gameState.blind, result)
    )

case class GameResult(blind: Blind, finalScore: Score)
