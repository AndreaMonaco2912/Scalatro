package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreTest extends AnyFlatSpec, Matchers:

  given basicHandScoreCalculator: HandScoreCalculator = BasicHandScoreCalculator

  "High card" should "score base score + rank of the card" in:
    val c1: Card = Card(Rank.Jack, Suit.Clubs)
    val cards: Seq[Card] = Seq(c1)
    val handType: HandType = HandType.detect(cards)
    val score: Score.Score = Score.calculateHandScore(cards)
    score shouldBe (Score(handType.baseScore) + Score(c1.rank.value))

  "Two pair" should "score base score + rank of the 4 cards" in:
    val c1: Card = Card(Rank.Jack, Suit.Clubs)
    val c2: Card = Card(Rank.Jack, Suit.Clubs)
    val c3: Card = Card(Rank.Queen, Suit.Clubs)
    val c4: Card = Card(Rank.Queen, Suit.Clubs)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4)
    val chipsSum = cards.map(_.rank.value).sum
    val handType: HandType = HandType.detect(cards)
    val score: Score.Score = Score.calculateHandScore(cards)
    score shouldBe (Score(handType.baseScore.chips) + Score(chipsSum)) * Score(
      handType.baseScore.mult
    )
