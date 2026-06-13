package scalatro
package model.commons

import model.commons.Chips.Chips
import model.commons.Mult.Mult
import model.commons.Score.Score

object Chips:
  type Chips = Double

  object Chips:
    def apply(d: Double): Chips =
      require(d >= 0.0, "Chips must be positive")
      d

object Mult:
  type Mult = Double

  object Mult:
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

object HandScore:
  import Chips.Chips, Mult.Mult

  case class HandScore(chips: Chips, mult: Mult)

  def apply(chips: Chips, mult: Mult): HandScore =
    HandScore(chips, mult)

object Score:
  import HandScore.HandScore
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
    val handScore: HandScore =
      cards.foldLeft(handType.baseScore)((acc, card) => card.onScored(acc))
    Score(handScore)

  val zero: Score = Score(0.0)
