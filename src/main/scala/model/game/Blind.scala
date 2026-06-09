package scalatro
package model.game

import model.round.Score
import model.round.Score.Score

import cats.data.State

case class Blind(roundNum: Int, score: Score)

object Blind:
  val handSize = 5
  val handNum = 4
  val discardNum = 3
  val increaseAmount = 1.5

  private val initialScore = 300
  private val initialRound = 1

  def firstBlind: Blind = Blind(initialRound, Score(initialScore))

  def nextBlind: State[Blind, Unit] =
    def increaseRound: State[Blind, Unit] =
      State.modify(b => b.copy(roundNum = b.roundNum + 1))

    def increaseScore: State[Blind, Unit] =
      State.modify(b => b.copy(score = b.score * Score(increaseAmount)))

    for
      _ <- increaseRound
      _ <- increaseScore
    yield ()
