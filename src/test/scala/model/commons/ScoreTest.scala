package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreTest extends AnyFlatSpec, Matchers:

  given basicHandScoreCalculator: HandScoreCalculator = BasicHandScoreCalculator

  def getExpectedScore(cards : Seq[Card], handType : HandType) : Score.Score =
    val baseScore = handType.baseScore
    val scoringCards = HandType.getScoringCards(cards)
    val chipsSum = scoringCards.map(_.getBaseChips).sum
    Score(HandScore(baseScore.chips+chipsSum,baseScore.mult))

  "High card" should "score base score + rank of the card" in:
    val cards = Seq(
      Card(Rank.Jack, Suit.Clubs),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Five, Suit.Hearts),
      Card(Rank.Three, Suit.Spades),
      Card(Rank.Nine, Suit.Spades)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.HighCard)
    score shouldBe expectedScore

  "Pair" should "score base score + rank of the 2 cards" in :
    val cards = Seq(
      Card(Rank.Jack, Suit.Clubs),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Five, Suit.Hearts),
      Card(Rank.Jack, Suit.Spades),
      Card(Rank.Nine, Suit.Spades)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.Pair)
    score shouldBe expectedScore

  "Two pair" should "score base score + rank of the 4 cards" in:
    val cards = Seq(
      Card(Rank.Jack, Suit.Clubs),
      Card(Rank.Jack, Suit.Clubs),
      Card(Rank.Queen, Suit.Clubs),
      Card(Rank.Queen, Suit.Clubs),
      Card(Rank.King, Suit.Spades)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.TwoPair)
    score shouldBe expectedScore

  "Three of a kind" should "score base + rank of the 3 cards" in:
    val cards = Seq(
      Card(Rank.Jack, Suit.Clubs),
      Card(Rank.Jack, Suit.Clubs),
      Card(Rank.Jack, Suit.Hearts),
      Card(Rank.Queen, Suit.Hearts)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.ThreeOfAKind)
    score shouldBe expectedScore

  "Straight" should "score base + rank of all cards" in:
    val cards = Seq(
      Card(Rank.Ace, Suit.Clubs),
      Card(Rank.Two, Suit.Hearts),
      Card(Rank.Three, Suit.Clubs),
      Card(Rank.Four, Suit.Spades),
      Card(Rank.Five, Suit.Clubs)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.Straight)
    score shouldBe expectedScore

  "Flush" should "score base + rank of all cards" in:
    val cards = Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Four, Suit.Spades),
      Card(Rank.Six, Suit.Spades),
      Card(Rank.Seven, Suit.Spades),
      Card(Rank.Jack, Suit.Spades),
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.Flush)
    score shouldBe expectedScore

  "Full house" should "score base + rank of all cards" in :
    val cards = Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Hearts),
      Card(Rank.Seven, Suit.Spades),
      Card(Rank.Seven, Suit.Clubs),
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.FullHouse)
    score shouldBe expectedScore

  "Full house" should "score base + rank of the 4 cards" in :
    val cards = Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Hearts),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Seven, Suit.Clubs),
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.FourOfAKind)
    score shouldBe expectedScore

  "Straight flush" should "score base + rank of all cards" in :
    val cards = Seq(
      Card(Rank.Ace, Suit.Hearts),
      Card(Rank.Two, Suit.Hearts),
      Card(Rank.Three, Suit.Hearts),
      Card(Rank.Four, Suit.Hearts),
      Card(Rank.Five, Suit.Hearts)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.StraightFlush)
    score shouldBe expectedScore

  "Five of a kind" should "score base + rank of all cards" in :
    val cards = Seq(
      Card(Rank.Ace, Suit.Hearts),
      Card(Rank.Ace, Suit.Diamonds),
      Card(Rank.Ace, Suit.Hearts),
      Card(Rank.Ace, Suit.Clubs),
      Card(Rank.Ace, Suit.Spades)
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.FiveOfAKind)
    score shouldBe expectedScore

  "Flush house" should "score base + rank of all cards" in :
    val cards = Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Seven, Suit.Spades),
      Card(Rank.Seven, Suit.Spades),
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.FlushHouse)
    score shouldBe expectedScore

  "Flush five" should "score base + rank of all cards" in :
    val cards = Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
    )
    val score: Score.Score = Score.calculateHandScore(cards)
    val expectedScore = getExpectedScore(cards, HandType.FlushFive)
    score shouldBe expectedScore