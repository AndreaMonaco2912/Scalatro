package scalatro
package model.game

import model.commons.Score
import model.commons.Score.Score

import cats.data.State

case class Blind(
    handSize: Int,
    handNum: Int,
    discardNum: Int,
    roundNum: Int,
    targetScore: Score
):
  def isBeaten(achieved: Score): Boolean = achieved >= targetScore

object Blind:
  val increaseAmount = 1.5

  private val initialHandSize = 5
  private val initialHandNum = 4
  private val initialDiscardNum = 3
  private val initialScore = Score(300)
  private val initialRound = 1

  def first: Blind =
    Blind(
      initialHandSize,
      initialHandNum,
      initialDiscardNum,
      initialRound,
      initialScore
    )

  def nextBlind: State[Blind, Unit] =
    State.modify(b =>
      b.copy(
        roundNum = b.roundNum + 1,
        targetScore = b.targetScore * Score(increaseAmount)
      )
    )
