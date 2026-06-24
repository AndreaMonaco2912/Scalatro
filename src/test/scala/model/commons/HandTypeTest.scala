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
    val cards: Seq[Card] = Seq(c1, c2, c3, c4, c5)
    HandType.detect(cards) shouldBe HandType.HighCard

  "Flush Five" should "contain Flush Five, Five of a Kind, Four of a Kind, Flush, Three of a Kind, Pair and High Card" in:
    val c = Card(Rank.Ace, Suit.Clubs)
    val cards = Seq(c, c, c, c, c)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.FlushFive,
      HandType.FiveOfAKind,
      HandType.FourOfAKind,
      HandType.Flush,
      HandType.ThreeOfAKind,
      HandType.Pair,
      HandType.HighCard
    )

  "Flush House" should "contain Flush House, Full House, Flush, Three of a Kind, Two Pair, Pair and High Card" in:
    val suit = Suit.Spades
    val c1 = Card(Rank.Two, suit)
    val c2 = Card(Rank.Three, suit)
    val cards = Seq(c1, c2, c1, c2, c1)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.FlushHouse,
      HandType.FullHouse,
      HandType.Flush,
      HandType.ThreeOfAKind,
      HandType.TwoPair,
      HandType.Pair,
      HandType.HighCard
    )

  "Five of a Kind" should "contain Five of a Kind, Four of a Kind, Three of a Kind, Pair and High Card" in:
    val c1 = Card(Rank.Ace, Suit.Clubs)
    val c2 = Card(Rank.Ace, Suit.Hearts)
    val cards = Seq(c1, c1, c1, c2, c2)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.FiveOfAKind,
      HandType.FourOfAKind,
      HandType.ThreeOfAKind,
      HandType.Pair,
      HandType.HighCard
    )

  "Straight Flush" should "contain Straight Flush, Straight, Flush and HighCard" in :
    val suit = Suit.Diamonds
    val c1 = Card(Rank.Six, suit)
    val c2 = Card(Rank.Seven, suit)
    val c3 = Card(Rank.Eight, suit)
    val c4 = Card(Rank.Nine, suit)
    val c5 = Card(Rank.Ten, suit)
    val cards = Seq(c1, c2, c3, c4, c5)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.StraightFlush,
      HandType.Straight,
      HandType.Flush,
      HandType.HighCard
    )

  "Four of a Kind" should "contain Four of a Kind, Three of a Kind, Pair and High Card" in:
    val c = Card(Rank.Ace, Suit.Clubs)
    val cards = Seq(c, c, c, c)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.FourOfAKind,
      HandType.ThreeOfAKind,
      HandType.Pair,
      HandType.HighCard
    )

  "Full House" should "contain Full House, Three of a Kind, Two Pair, Pair and High Card" in:
    val c1 = Card(Rank.Two, Suit.Hearts)
    val c2 = Card(Rank.Two, Suit.Clubs)
    val c3 = Card(Rank.Seven, Suit.Spades)
    val cards = Seq(c1,c2,c3,c3,c3)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.FullHouse,
      HandType.ThreeOfAKind,
      HandType.TwoPair,
      HandType.Pair,
      HandType.HighCard
    )

  "Flush" should "contain Flush and High Card and occasionally other types" in:
    val suit = Suit.Hearts
    val c1 = Card(Rank.Ace, suit)
    val c2 = Card(Rank.Six, suit)
    val c3 = Card(Rank.King, suit)
    val c4 = Card(Rank.Eight, suit)
    val c5 = Card(Rank.Three, suit)
    val cardsNormalFlush = Seq(c1,c2,c3,c4,c5)
    HandType.values
      .filter(ht => HandType.contains(cardsNormalFlush, ht))
      .toSet shouldBe Set(
      HandType.Flush,
      HandType.HighCard
    )
    val cardsFlushWithPair = Seq(c1,c1,c2,c3,c4)
    HandType.values
      .filter(ht => HandType.contains(cardsFlushWithPair, ht))
      .toSet shouldBe Set(
      HandType.Flush,
      HandType.Pair,
      HandType.HighCard
    )
    val cardsFlushWithTwoPair = Seq(c1,c1,c2,c2,c3)
    HandType.values
      .filter(ht => HandType.contains(cardsFlushWithTwoPair, ht))
      .toSet shouldBe Set(
      HandType.Flush,
      HandType.TwoPair,
      HandType.Pair,
      HandType.HighCard
    )

  "Straight" should "contain Straight and High Card" in:
    val suit1 = Suit.Clubs
    val suit2 = Suit.Hearts
    val c1 = Card(Rank.Ten, suit1)
    val c2 = Card(Rank.Jack, suit1)
    val c3 = Card(Rank.Queen, suit1)
    val c4 = Card(Rank.King, suit2)
    val c5 = Card(Rank.Ace, suit2)
    val cards = Seq(c1,c2,c3,c4,c5)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.Straight,
      HandType.HighCard
    )

  "Three of a Kind" should "contain Three of a Kind, Pair and High Card" in:
    val c = Card(Rank.Ace, Suit.Clubs)
    val cards = Seq(c,c,c)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.ThreeOfAKind,
      HandType.Pair,
      HandType.HighCard
    )

  "Two Pair" should "contain Two Pair, Pair and High Card" in:
    val c1 = Card(Rank.Ace, Suit.Clubs)
    val c2 = Card(Rank.Ten, Suit.Hearts)
    val cards = Seq(c1, c1, c2, c2)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.TwoPair,
      HandType.Pair,
      HandType.HighCard
    )

  "Pair" should "contain Pair and High Card" in:
    val c = Card(Rank.Queen, Suit.Diamonds)
    val cards = Seq(c,c)
    HandType.values
      .filter(ht => HandType.contains(cards, ht))
      .toSet shouldBe Set(
      HandType.Pair,
      HandType.HighCard
    )