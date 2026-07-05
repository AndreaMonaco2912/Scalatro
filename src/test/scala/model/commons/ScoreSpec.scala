package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.Score.{Score, getHandTypeBaseScore}
import model.commons.Chips.Chips
import model.commons.Mult.Mult
import model.extra.CardBuilder.*

class ScoreSpec extends AnyFlatSpec, Matchers:

  private given defaultScoreConfig: ScoreConfig = ScoreConfig.default
  private given calculator: HandScoreCalculator = defaultScoreConfig.calculator

  "BasicHandScoreCalculator" should "multiply chips and mult together" in:
    val chips: Chips = Chips(50)
    val mult: Mult = Mult(20)
    Score(HandScore(chips, mult))(using
      BasicHandScoreCalculator
    ) shouldBe Score(
      1000
    )

  "AvgSquaredHandScoreCalculator" should "square the average of chips and mult" in:
    val chips: Chips = Chips(50)
    val mult: Mult = Mult(20)
    Score(HandScore(chips, mult))(using
      AvgSquaredHandScoreCalculator
    ) shouldBe Score(1225)

  "High Card" should "score base score + rank of the card" in:
    val cards = Seq(
      Card(Rank.Jack, Suit.Clubs)
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(15), Mult(1))
    score shouldBe expectedScore

  "Pair" should "score base score + rank of the 2 cards" in:
    val c = Card(Rank.Jack, Suit.Clubs)
    val cards = Seq(
      c,
      c
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(30), Mult(2))
    score shouldBe expectedScore

  "Two Pair" should "score base score + rank of the 4 cards" in:
    val c1 = Card(Rank.Jack, Suit.Clubs)
    val c2 = Card(Rank.Queen, Suit.Clubs)
    val c3 = Card(Rank.King, Suit.Spades)
    val cards = Seq(
      c1,
      c1,
      c2,
      c2,
      c3
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(60), Mult(2))
    score shouldBe expectedScore

  "Three of a Kind" should "score base + rank of the 3 cards" in:
    val c1 = Card(Rank.Jack, Suit.Clubs)
    val c2 = Card(Rank.Queen, Suit.Hearts)
    val cards = Seq(
      c1,
      c1,
      c1,
      c2
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(60), Mult(3))
    score shouldBe expectedScore

  "Straight" should "score base + rank of all cards" in:
    val suit1 = Suit.Clubs
    val suit2 = Suit.Hearts
    val c1 = Card(Rank.Ace, suit1)
    val c2 = Card(Rank.Two, suit1)
    val c3 = Card(Rank.Three, suit1)
    val c4 = Card(Rank.Four, suit1)
    val c5 = Card(Rank.Five, suit2)
    val cards = Seq(
      c1,
      c2,
      c3,
      c4,
      c5
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(55), Mult(4))
    score shouldBe expectedScore

  "Flush" should "score base + rank of all cards" in:
    val suit = Suit.Spades
    val c1 = Card(Rank.Two, suit)
    val c2 = Card(Rank.Four, suit)
    val c3 = Card(Rank.Six, suit)
    val c4 = Card(Rank.Eight, suit)
    val c5 = Card(Rank.Jack, suit)
    val cards = Seq(
      c1,
      c2,
      c3,
      c4,
      c5
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(65), Mult(4))
    score shouldBe expectedScore

  "Full House" should "score base + rank of all cards" in:
    val c1 = Card(Rank.Two, Suit.Spades)
    val c2 = Card(Rank.Seven, Suit.Clubs)
    val cards = Seq(
      c1,
      c1,
      c1,
      c2,
      c2
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(60), Mult(4))
    score shouldBe expectedScore

  "Four of a Kind" should "score base + rank of the 4 cards" in:
    val c1 = Card(Rank.Two, Suit.Spades)
    val c2 = Card(Rank.Seven, Suit.Clubs)
    val cards = Seq(
      c1,
      c1,
      c1,
      c1,
      c2
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(68), Mult(7))
    score shouldBe expectedScore

  "Straight Flush" should "score base + rank of all cards" in:
    val suit = Suit.Hearts
    val c1 = Card(Rank.Ace, suit)
    val c2 = Card(Rank.Two, suit)
    val c3 = Card(Rank.Three, suit)
    val c4 = Card(Rank.Four, suit)
    val c5 = Card(Rank.Five, suit)
    val cards = Seq(
      c1,
      c2,
      c3,
      c4,
      c5
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(125), Mult(8))
    score shouldBe expectedScore

  "Five of a Kind" should "score base + rank of all cards" in:
    val c1 = Card(Rank.Ace, Suit.Hearts)
    val c2 = Card(Rank.Ace, Suit.Diamonds)
    val cards = Seq(
      c1,
      c1,
      c1,
      c2,
      c2
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(175), Mult(12))
    score shouldBe expectedScore

  "Flush House" should "score base + rank of all cards" in:
    val c1 = Card(Rank.Two, Suit.Spades)
    val c2 = Card(Rank.Seven, Suit.Spades)
    val cards = Seq(
      c1,
      c1,
      c1,
      c2,
      c2
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(160), Mult(14))
    score shouldBe expectedScore

  "Flush Five" should "score base + rank of all cards" in:
    val c = Card(Rank.Two, Suit.Spades)
    val cards = Seq(
      c,
      c,
      c,
      c,
      c
    )
    val score: HandScore = Score.calculateHandScore(cards)
    val expectedScore = HandScore(Chips(170), Mult(16))
    score shouldBe expectedScore


//  "High Card level N" should "score base + N * score increase + chips of the card" in:
//    val level: Level = 5
//    val scoreConfig = ScoreConfig(
//      Seq.empty,
//      HandTypeLevels.initial.updated(HandType.HighCard, level),
//      defaultScoreConfig.calculator
//    )
//    val cards: Seq[Card] = Seq(Card(Rank.Ace, Suit.Clubs))
//    val score: HandScore = Score.calculateHandScore(cards)(using scoreConfig)
//    val expectedScore = getExpectedScore(cards, HandType.HighCard, level)
//    score shouldBe expectedScore
