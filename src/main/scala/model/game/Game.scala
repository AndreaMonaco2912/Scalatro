package scalatro
package model.game

import scala.util.Random
import model.commons.Deck
import model.commons.Score.Score
import model.round.Placeholder

import scala.annotation.tailrec

class Game(val seed: Long):
  private val rng: Random = Random(seed)
  given Random = rng
  private val deck: Deck = Deck()

  def play(): GameResult =
    @tailrec
    def loop(blind: Blind): GameResult =
      val achieved: Score =
        Placeholder.playRound.runA((deck.shuffle, blind)).value
      if !blind.isBeaten(achieved) then GameResult(blind, achieved)
      else loop(Blind.nextBlind.runS(blind).value)

    loop(Blind.first)

case class GameResult(blind: Blind, finalScore: Score):
  def isGameLost: Boolean = !blind.isBeaten(finalScore)

object Game:
  def apply(seed: Long = Random.nextLong()): Game = new Game(seed)
