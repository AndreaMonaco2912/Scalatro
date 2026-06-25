package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Inspectors.forAll

import model.commons.Score.Score
import model.commons.Chips.Chips
import model.commons.Mult.Mult

class ScoreTest extends AnyFlatSpec, Matchers:

  private given defaultScoreConfig: ScoreConfig = ScoreConfig.default
  private given calculator: HandScoreCalculator = defaultScoreConfig.calculator

  /** Method for getting the expected score of a hand played
    * @param cards
    *   the cards played
    * @param levels
    *   the levels of the hand types
    * @return
    *   the expected score
    */
  def getExpectedScore(
      cards: Seq[Card],
      levels: HandTypeLevels = HandTypeLevels.initial
  ): HandScore =
    val handType = HandType.detect(cards)
    val baseScore = handType.baseScore
    val scoringCards = HandType.getScoringCards(cards)
    val level = levels.getOrElse(handType, Level.initial)
    val chipsSum = scoringCards.map(_.getBaseChips).sum
    baseScore + Planet.getIncrease(handType) * (level - 1) + HandScore(
      chipsSum,
      0
    )

  def getCardsForHandType(handType: HandType): Seq[Card] =
    val suit1 = Suit.Hearts
    val suit2 = Suit.Clubs
    val c1 = Card(Rank.Ace, suit1)
    val c2 = Card(Rank.Two, suit1)
    val c3 = Card(Rank.Three, suit1)
    val c4 = Card(Rank.Four, suit1)
    val c5 = Card(Rank.Five, suit1)
    val c6 = Card(Rank.Ace, suit2)
    val c7 = Card(Rank.Two, suit2)
    handType match
      case HandType.HighCard      => Seq(c1)
      case HandType.Pair          => Seq(c1, c1)
      case HandType.TwoPair       => Seq(c1, c1, c2, c2)
      case HandType.ThreeOfAKind  => Seq(c1, c1, c1)
      case HandType.Straight      => Seq(c6, c7, c3, c4, c5)
      case HandType.Flush         => Seq(c1, c1, c2, c3, c4)
      case HandType.FullHouse     => Seq(c1, c1, c1, c7, c7)
      case HandType.FourOfAKind   => Seq(c1, c1, c1, c1)
      case HandType.StraightFlush => Seq(c1, c2, c3, c4, c5)
      case HandType.FiveOfAKind   => Seq(c1, c1, c1, c6, c6)
      case HandType.FlushHouse    => Seq(c1, c1, c1, c2, c2)
      case HandType.FlushFive     => Seq(c1, c1, c1, c1, c1)

  "BasicHandScoreCalculator" should "multiply chips and mult together" in:
    val chips: Chips = 50
    val mult: Mult = 20
    Score(HandScore(chips, mult))(using
      BasicHandScoreCalculator
    ) shouldBe Score(
      chips * mult
    )

  "AvgSquaredHandScoreCalculator" should "square the average of chips and mult" in:
    val chips: Chips = 50
    val mult: Mult = 20
    val avg = (chips + mult) / 2
    Score(HandScore(chips, mult))(using
      AvgSquaredHandScoreCalculator
    ) shouldBe Score(avg * avg)

  "Hand type at initial level" should "score base score + rank of the cards scored" in:
    forAll(HandType.values)(handType =>
      val cards = getCardsForHandType(handType)
      val score = Score.calculateHandScore(cards)
      val expectedScore = getExpectedScore(cards, HandTypeLevels.initial)
      score shouldBe expectedScore
    )

  "Hand type at level N" should "score base score + (level-1) * planet score increase + rank of the cards scored" in:
    val level = 5
    forAll(HandType.values)(handType =>
      val scoreConfig = ScoreConfig.default.copy(levels =
        HandTypeLevels.initial.updated(handType, level)
      )
      val cards = getCardsForHandType(handType)
      val score = Score.calculateHandScore(cards)(using scoreConfig)
      val expectedScore = getExpectedScore(cards, scoreConfig.levels)
      score shouldBe expectedScore
    )
