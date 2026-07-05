package scalatro
package model.commons

import model.commons.Chips.Chips
import model.commons.Mult.Mult
import model.commons.Score.Score

import scala.annotation.targetName

object Chips:
  opaque type Chips = Double

  def apply(d: Double): Chips =
    require(d >= 0.0, "Chips must be positive")
    d

  extension (c: Chips)
    def +(other: Chips): Chips = Chips(c + other)
    def asDouble: Double = c

  val zero: Chips = Chips(0.0)

object Mult:
  opaque type Mult = Double

  def apply(d: Double): Mult =
    require(d >= 0.0, "Mult must be positive")
    d

  extension (m: Mult)
    def +(other: Mult): Mult = Mult(m + other)
    def *(other: Mult): Mult = Mult(m * other)
    def asDouble: Double = m

  val zero: Mult = Mult(0.0)

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

object HandScoreCalculator:

  def default: HandScoreCalculator = BasicHandScoreCalculator

/** Hand score calculator in which the score is given by multiplying together
  * the chips and the multiplier
  */
object BasicHandScoreCalculator extends HandScoreCalculator:
  override def calculate(chips: Chips, mult: Mult): Score = Score(
    chips.asDouble * mult.asDouble
  )

/** Hand score calculator in which the score is given by squaring the average of
  * the chips and the multiplier
  */
object AvgSquaredHandScoreCalculator extends HandScoreCalculator:

  override def calculate(chips: Chips, mult: Mult): Score =
    val avg = (chips.asDouble + mult.asDouble) / 2
    Score(avg * avg)

/** The score given by playing a hand
  * @param chips
  *   the chips
  * @param mult
  *   the multiplier
  */
case class HandScore(chips: Chips, mult: Mult) extends Effectible:
  def apply(chips: Chips, mult: Mult): HandScore =
    HandScore(chips, mult)

object HandScore:
  import Chips.Chips, Mult.Mult

  def apply(chips: Chips): HandScore = HandScore(chips, Mult.zero)

  @targetName("fromMult")
  def apply(mult: Mult): HandScore = HandScore(Chips.zero, mult)

  extension (hs: HandScore)
    def +(other: HandScore): HandScore =
      HandScore(hs.chips + other.chips, hs.mult + other.mult)
    @targetName("addChips")
    def +(chips: Chips): HandScore = HandScore(hs.chips + chips, hs.mult)
    @targetName("addMult")
    def +(mult: Mult): HandScore = HandScore(hs.chips, hs.mult + mult)
    def *(mult: Mult): HandScore = HandScore(hs.chips, hs.mult * mult)
    def *(level: Level): HandScore = HandScore(
      Chips(hs.chips.asDouble * level),
      Mult(hs.mult.asDouble * level)
    )

  val zero: HandScore = HandScore(Chips.zero, Mult.zero)

trait HandScoreModification extends Modification[HandScore]

object HandScoreModification:
  case class FlatChips(chips: Chips) extends HandScoreModification:
    override def apply(value: HandScore): HandScore = value + chips
  case class FlatMult(mult: Mult) extends HandScoreModification:
    override def apply(value: HandScore): HandScore = value + mult
  case class MultiplicativeMult(mult: Mult) extends HandScoreModification:
    override def apply(value: HandScore): HandScore = value * mult

trait ScoreModification extends Modification[Score]

object ScoreModification:
  case class MultiplicativeIncrease(factor: Double) extends ScoreModification:
    override def apply(value: Score): Score = value * factor
  case class MultiplicativeDecrease(factor: Double) extends ScoreModification:
    require(factor != 0.0, "Can't divide score by zero")
    override def apply(value: Score): Score = value / factor

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

object Score extends Effectible:
  opaque type Score = Double

  def apply(d: Double): Score =
    require(d >= 0.0, "Score must be positive")
    d
  def apply(handScore: HandScore)(using
      calculator: HandScoreCalculator
  ): Score =
    calculator.calculate(handScore.chips, handScore.mult)

  extension (s: Score)
    def +(other: Score): Score = s + other
    def -(other: Score): Score = s - other
    def *(factor: Double): Score = s * factor
    def /(factor: Double): Score = s / factor
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
  def getHandTypeBaseScore(
      handType: HandType,
      levels: HandTypeLevels
  ): HandScore =
    val handTypeLevel: Level = levels.getLevel(handType)
    val handTypeIncreaseScore: HandScore = Planet.getIncrease(handType)
    handType.baseScore + (handTypeIncreaseScore * (handTypeLevel - 1))

  def calculateHandScore(cards: Seq[Card])(using
      scoreConfig: ScoreConfig
  ): HandScore =
    val jokers = scoreConfig.jokers
    val levels = scoreConfig.levels
    val handType: HandType = HandType.detect(cards)
    val initialScore: HandScore = getHandTypeBaseScore(handType, levels)
    val initialModifications: Seq[Modification[?]] = Seq.empty
    val scoringCards = HandType.getScoringCards(cards)
    val onHandPlayed = jokers.foldLeft(initialModifications) { (acc, joker) =>
      acc ++ joker.onHandPlayed(scoringCards).apply(scoringCards)
    }
    val onCardScored = scoringCards.foldLeft(onHandPlayed) { (acc, card) =>
      val afterCardSelf = acc ++ card.onScored
      jokers.foldLeft(afterCardSelf) { (acc2, joker) =>
        acc2 ++ joker.onCardScored(card).apply(card)
      }
    }
    val allModifications = jokers.foldLeft(onCardScored) { (acc, joker) =>
      acc ++ joker.afterHandPlayed(scoringCards).apply(scoringCards)
    }
    val scoreModifications: Seq[HandScoreModification] =
      allModifications.collect { case s: HandScoreModification => s }
    scoreModifications.foldLeft(initialScore)((acc, modification) =>
      modification(acc)
    )

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
    Score(calculateHandScore(cards))(using scoreConfig.calculator)

  val zero: Score = Score(0.0)
