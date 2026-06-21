package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HandTypeTest extends AnyFlatSpec, Matchers:

  "Flush five" should "be detected with 5 cards of the same rank and suit" in:
    val c1: Card = Card(Rank.Six, Suit.Clubs)
    val c2: Card = Card(Rank.Six, Suit.Clubs)
    val c3: Card = Card(Rank.Six, Suit.Clubs)
    val c4: Card = Card(Rank.Six, Suit.Clubs)
    val c5: Card = Card(Rank.Six, Suit.Clubs)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.FlushFive

  "Flush house" should "be detected with both a full house and a flush" in:
    val c1: Card = Card(Rank.Six, Suit.Clubs)
    val c2: Card = Card(Rank.Six, Suit.Clubs)
    val c3: Card = Card(Rank.Six, Suit.Clubs)
    val c4: Card = Card(Rank.Seven, Suit.Clubs)
    val c5: Card = Card(Rank.Seven, Suit.Clubs)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.FlushHouse

  "Five of a kind" should "be detected with 5 cards of the same rank but not of the same suit" in:
    val c1: Card = Card(Rank.Six, Suit.Clubs)
    val c2: Card = Card(Rank.Six, Suit.Clubs)
    val c3: Card = Card(Rank.Six, Suit.Clubs)
    val c4: Card = Card(Rank.Six, Suit.Clubs)
    val c5: Card = Card(Rank.Six, Suit.Diamonds)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.FiveOfAKind

  "Straight flush" should "be detected with 5 cards in a straight of the same suit" in:
    val c1: Card = Card(Rank.Nine, Suit.Hearts)
    val c2: Card = Card(Rank.King, Suit.Hearts)
    val c3: Card = Card(Rank.Queen, Suit.Hearts)
    val c4: Card = Card(Rank.Jack, Suit.Hearts)
    val c5: Card = Card(Rank.Ten, Suit.Hearts)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.StraightFlush

  "Four of a kind" should "be detected with 4 cards of the same rank" in:
    val c1: Card = Card(Rank.Six, Suit.Clubs)
    val c2: Card = Card(Rank.Six, Suit.Clubs)
    val c3: Card = Card(Rank.Six, Suit.Clubs)
    val c4: Card = Card(Rank.Six, Suit.Clubs)
    val c5: Card = Card(Rank.Seven, Suit.Diamonds)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.FourOfAKind

  "Full house" should "be detected with 3 cards of a certain rank and 2 cards of another rank" in:
    val c1: Card = Card(Rank.Six, Suit.Clubs)
    val c2: Card = Card(Rank.Six, Suit.Spades)
    val c3: Card = Card(Rank.Six, Suit.Clubs)
    val c4: Card = Card(Rank.Seven, Suit.Hearts)
    val c5: Card = Card(Rank.Seven, Suit.Diamonds)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.FullHouse

  "Flush" should "be detected with 5 cards of the same suit" in:
    val c1: Card = Card(Rank.Ace, Suit.Spades)
    val c2: Card = Card(Rank.Three, Suit.Spades)
    val c3: Card = Card(Rank.Five, Suit.Spades)
    val c4: Card = Card(Rank.Nine, Suit.Spades)
    val c5: Card = Card(Rank.Queen, Suit.Spades)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.Flush

  "Straight" should "be detected with 5 consecutive cards" in:
    val c1: Card = Card(Rank.Ace, Suit.Spades)
    val c2: Card = Card(Rank.Two, Suit.Hearts)
    val c3: Card = Card(Rank.Three, Suit.Spades)
    val c4: Card = Card(Rank.Four, Suit.Diamonds)
    val c5: Card = Card(Rank.Five, Suit.Spades)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.Straight

  "Three of a kind" should "be detected with 3 cards of the same rank" in:
    val c1: Card = Card(Rank.Ace, Suit.Spades)
    val c2: Card = Card(Rank.Ace, Suit.Hearts)
    val c3: Card = Card(Rank.Ace, Suit.Spades)
    val cards: Seq[Card] = Seq(c1, c2, c3)
    HandType.detect(cards) shouldBe HandType.ThreeOfAKind

  "Two pair" should "be detected with 2 cards a certain rank and 2 cards of another rank" in:
    val c1: Card = Card(Rank.Ace, Suit.Spades)
    val c2: Card = Card(Rank.Ace, Suit.Hearts)
    val c3: Card = Card(Rank.Two, Suit.Spades)
    val c4: Card = Card(Rank.Two, Suit.Spades)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4)
    HandType.detect(cards) shouldBe HandType.TwoPair

  "Pair" should "be detected with 2 cards of the same rank" in:
    val c1: Card = Card(Rank.Ace, Suit.Spades)
    val c2: Card = Card(Rank.Ace, Suit.Hearts)
    val c3: Card = Card(Rank.Two, Suit.Spades)
    val c4: Card = Card(Rank.Three, Suit.Spades)
    val cards: Seq[Card] = Seq(c1, c2, c3, c4)
    HandType.detect(cards) shouldBe HandType.Pair

  "High card" should "be detected when no other combination is possible" in:
    val c1: Card = Card(Rank.Six, Suit.Clubs)
    val c2: Card = Card(Rank.Seven, Suit.Clubs)
    val c3: Card = Card(Rank.Eight, Suit.Clubs)
    val c4: Card = Card(Rank.Ace, Suit.Clubs)
    val c5: Card = Card(Rank.King, Suit.Diamonds)
