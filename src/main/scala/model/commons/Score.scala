package scalatro
package model.commons

import model.commons.Chips.Chips
import model.commons.Mult.Mult
import model.commons.Score.Score
import model.game.GameState

object Chips:
  type Chips = Double

  def apply(d: Double): Chips =
    require(d >= 0.0, "Chips must be positive")
    d

object Mult:
  type Mult = Double

  def apply(d: Double): Mult =
    require(d >= 0.0, "Mult must be positive")
    d

trait HandScoreCalculator:
  import Chips.Chips, Mult.Mult
  import Score.Score
  def calculate(chips: Chips, mult: Mult): Score

object BasicHandScoreCalculator extends HandScoreCalculator:
  override def calculate(chips: Chips, mult: Mult): Score = Score(chips * mult)

object AvgSquaredHandScoreCalculator extends HandScoreCalculator:
  override def calculate(chips: Chips, mult: Mult): Score =
    val avg = (chips + mult) / 2
    Score(avg * avg)

case class HandScore(chips: Chips, mult: Mult):
  def apply(chips: Chips, mult: Mult): HandScore =
    HandScore(chips, mult)

object HandScore:
  import Chips.Chips, Mult.Mult

  extension (hs: HandScore)
    def +(other: HandScore): HandScore =
      HandScore(hs.chips + other.chips, hs.mult + hs.mult)
    def *(mult: Int): HandScore = HandScore(hs.chips * mult, hs.mult * mult)

object Score:
  opaque type Score = Double

  def apply(d: Double): Score =
    require(d >= 0.0, "Score must be positive")
    d
  def apply(using
      calculator: HandScoreCalculator
  )(handScore: HandScore): Score =
    calculator.calculate(handScore.chips, handScore.mult)

  extension (s: Score)
    def +(other: Score): Score = s + other
    def -(other: Score): Score = s - other
    def *(other: Score): Score = s * other
    def >(other: Score): Boolean = s > other
    def <(other: Score): Boolean = s < other
    def >=(other: Score): Boolean = s >= other
    def <=(other: Score): Boolean = s >= other
    def asDouble: Double = s

  def calculateHandScore(using
      calculator: HandScoreCalculator
  )(cards: Seq[Card]): Score =
    val handType: HandType = HandType.detect(cards)
    val scoringCards = HandType.getScoringCards(cards)
//    val handTypeLevel = gameState.levels.getOrElse(handType, 1)
//    val handTypeIncreaseScore: HandScore = Planet.getIncrease(handType)
//    val handTypeBaseScore: HandScore =
//      handType.baseScore + handTypeIncreaseScore * handTypeLevel
    val handScore: HandScore =
      scoringCards.foldLeft(handType.baseScore)((acc, card) =>
        card.onScored(acc)
      )
    Score(handScore)

  val zero: Score = Score(0.0)
