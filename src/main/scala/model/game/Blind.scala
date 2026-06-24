package scalatro
package model.game

import model.commons.Score
import model.commons.Score.Score

case class Blind(roundNum: Int, targetScore: Score):
  def isBeaten(achieved: Score): Boolean = achieved >= targetScore

  def next: Blind = Blind(roundNum + 1, targetScore * Score(1.5))

object Blind:
  val increaseAmount = 1.5
  val initialScore: Score = Score(300)
  private val initialRound = 1

  def first: Blind = Blind(initialRound, initialScore)
