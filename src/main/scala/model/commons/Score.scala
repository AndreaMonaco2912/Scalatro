package scalatro
package model.commons

import model.commons.Chips.Chips
import model.commons.Mult.Mult
import model.commons.Score.Score

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

/** A trait for representing the component which calculates the score of a hand
  */
trait HandScoreCalculator:
  import Chips.Chips, Mult.Mult
  import Score.Score

  /** The strategy by which the chips and the multiplier are combined in a score
    * @param chips
    *   the chips
    * @param mult
    *   the multiplier
    * @return
    */
  def calculate(chips: Chips, mult: Mult): Score

/** Hand score calculator in which the score is given by multiplying together
  * the chips and the multiplier
  */
object BasicHandScoreCalculator extends HandScoreCalculator:
  override def calculate(chips: Chips, mult: Mult): Score = Score(chips * mult)

/** Hand score calculator in which the score is given by squaring the average of
  * the chips and the multiplier
  */
object AvgSquaredHandScoreCalculator extends HandScoreCalculator:

  override def calculate(chips: Chips, mult: Mult): Score =
    val avg = (chips + mult) / 2
    Score(avg * avg)

/** The score given by playing a hand
  * @param chips
  *   the chips
  * @param mult
  *   the multiplier
  */
case class HandScore(chips: Chips, mult: Mult):
  def apply(chips: Chips, mult: Mult): HandScore =
    HandScore(chips, mult)

object HandScore:
  import Chips.Chips, Mult.Mult

  extension (hs: HandScore)
    def +(other: HandScore): HandScore =
      HandScore(hs.chips + other.chips, hs.mult + hs.mult)
    def *(mult: Mult): HandScore = HandScore(hs.chips * mult, hs.mult * mult)

/** The configuration needed to calculate the score
  * @param jokers
  *   the ordered sequence of jokers available
  * @param levels
  *   the level of hand types
  * @param calculator
  *   the hand score calculator
  */
case class ScoreConfig(
    jokers: Seq[Joker],
    levels: HandTypeLevels,
    calculator: HandScoreCalculator
)

object ScoreConfig:
  def default: ScoreConfig =
    ScoreConfig(Seq.empty, HandTypeLevels.initial, BasicHandScoreCalculator)

object Score:
  opaque type Score = Double

  def apply(d: Double): Score =
    require(d >= 0.0, "Score must be positive")
    d
  def apply(using
      scoreConfig: ScoreConfig
  )(handScore: HandScore): Score =
    scoreConfig.calculator.calculate(handScore.chips, handScore.mult)

  extension (s: Score)
    def +(other: Score): Score = s + other
    def -(other: Score): Score = s - other
    def *(other: Score): Score = s * other
    def >(other: Score): Boolean = s > other
    def <(other: Score): Boolean = s < other
    def >=(other: Score): Boolean = s >= other
    def <=(other: Score): Boolean = s >= other
    def asDouble: Double = s

  /** Get the base score of a hand type based on its level
    * @param levels
    *   the level of the hand types
    * @param handType
    *   the hand type
    * @return
    */
  private def getLevelledHandTypeBaseScore(
      levels: HandTypeLevels,
      handType: HandType
  ): HandScore =
    val handTypeLevel: Level = levels.getOrElse(handType, Level.initial)
    val handTypeIncreaseScore: HandScore = Planet.getIncrease(handType)
    handType.baseScore + handTypeIncreaseScore * handTypeLevel

  def calculateHandScore(cards: Seq[Card])(using
      scoreConfig: ScoreConfig
  ): HandScore =
    val handType: HandType = HandType.detect(cards)
    val scoringCards = HandType.getScoringCards(cards)
    val handTypeBaseScore: HandScore =
      getLevelledHandTypeBaseScore(scoreConfig.levels, handType)
    scoringCards.foldLeft(handType.baseScore)((acc, card) => card.onScored(acc))

  /** A method that calculates the score given by a hand
    * @param cards
    *   the cards played
    * @param scoreConfig
    *   the score configuration
    * @return
    *   the score
    */
  def calculateScore(cards: Seq[Card])(using
      scoreConfig: ScoreConfig
  ): Score =
    Score(calculateHandScore(cards))

  val zero: Score = Score(0.0)
