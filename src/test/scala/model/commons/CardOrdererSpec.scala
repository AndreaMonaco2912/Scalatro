package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test suite for [[CardOrderer]]
  */
class CardOrdererSpec extends AnyFlatSpec, Matchers:
  "identity" should "keep the same order" in:
    val cards = Seq(
      Card(Rank.Three, Suit.Hearts),
      Card(Rank.Ace, Suit.Spades),
      Card(Rank.Two, Suit.Clubs)
    )

    CardOrderer.identity.order(cards) shouldBe cards

  "sortByRank" should "order cards by rank" in:
    val cards = Seq(
      Card(Rank.Five, Suit.Hearts),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Three, Suit.Diamonds)
    )

    CardOrderer.sortByRank.order(cards) shouldBe Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Three, Suit.Diamonds),
      Card(Rank.Five, Suit.Hearts)
    )

  "sortBySuit" should "follow Suit.values order" in:
    val cards = Seq(
      Card(Rank.Five, Suit.Hearts),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Ace, Suit.Diamonds),
      Card(Rank.Four, Suit.Clubs)
    )

    CardOrderer.sortBySuit.order(cards).map(_.suit) shouldBe Suit.values.toSeq

  it should "sort ranks within each suit" in:
    val cards = Seq(
      Card(Rank.Five, Suit.Spades),
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Three, Suit.Spades)
    )

    CardOrderer.sortBySuit.order(cards) shouldBe Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Three, Suit.Spades),
      Card(Rank.Five, Suit.Spades)
    )

  "swapCards" should "swap two positions" in:
    val cards = Seq(
      Card(Rank.Two, Suit.Spades),
      Card(Rank.Three, Suit.Hearts),
      Card(Rank.Four, Suit.Clubs)
    )

    CardOrderer.swapCards(0, 2).order(cards) shouldBe Seq(
      Card(Rank.Four, Suit.Clubs),
      Card(Rank.Three, Suit.Hearts),
      Card(Rank.Two, Suit.Spades)
    )

  it should "reject a negative first index" in:
    val cards = Seq(Card(Rank.Two, Suit.Spades))

    an[IllegalArgumentException] should be thrownBy
      CardOrderer.swapCards(-1, 0).order(cards)

  it should "reject a second index past the end" in:
    val cards = Seq(Card(Rank.Two, Suit.Spades))

    an[IllegalArgumentException] should be thrownBy
      CardOrderer.swapCards(0, 1).order(cards)
