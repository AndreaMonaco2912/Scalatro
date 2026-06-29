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
      HandScore(hs.chips + other.chips, hs.mult + other.mult)
    def *(mult: Double): HandScore = HandScore(hs.chips * mult, hs.mult * mult)

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
  def apply(handScore: HandScore)(using
      calculator: HandScoreCalculator
  ): Score =
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
    val jokerConfig: JokerConfig = JokerConfig(cards, scoreConfig.levels)
    val handType: HandType = HandType.detect(cards)
    val scoringCards = HandType.getScoringCards(cards)
    val initialScore: HandScore =
      getHandTypeBaseScore(handType, levels)
    val afterHandPlayed: HandScore = jokers.foldLeft(initialScore)(
      (acc, joker) => joker.onHandPlayed(cards).apply(acc)(using jokerConfig)
    )
    val afterCards: HandScore =
      scoringCards.foldLeft(afterHandPlayed)((acc, card) =>
        val afterCard: HandScore = card.onScored(acc)
        jokers.foldLeft(afterCard)((acc, joker) =>
          joker.onCardScored(card).apply(acc)(using jokerConfig)
        )
      )
    jokers.foldLeft(afterCards)((acc, joker) =>
      joker.independent(acc)(using jokerConfig)
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
