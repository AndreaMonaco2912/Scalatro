package scalatro
package model.commons

import model.extra.CardBuilder.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardBuilderSpec extends AnyFlatSpec with Matchers:

  "CardBuilder" should "correctly minimum and maximum numeric cards" in:
    (2 of H) shouldBe Card(Rank.Two, Suit.Hearts)
    (10 of S) shouldBe Card(Rank.Ten, Suit.Spades)

  it should "correctly create middle numeric cards across all suits" in:
    (5 of C) shouldBe Card(Rank.Five, Suit.Clubs)
    (7 of D) shouldBe Card(Rank.Seven, Suit.Diamonds)
    (9 of H) shouldBe Card(Rank.Nine, Suit.Hearts)

  it should "correctly create face cards" in:
    (J of S) shouldBe Card(Rank.Jack, Suit.Spades)
    (Q of D) shouldBe Card(Rank.Queen, Suit.Diamonds)
    (K of H) shouldBe Card(Rank.King, Suit.Hearts)
    (A of C) shouldBe Card(Rank.Ace, Suit.Clubs)

  it should "throw an IllegalArgumentException for numbers below 2" in:
    an[IllegalArgumentException] should be thrownBy
      (1 of H)

    an[IllegalArgumentException] should be thrownBy
      (0 of S)

    an[IllegalArgumentException] should be thrownBy
      (-22 of C)

  it should "throw an IllegalArgumentException for numbers above 10" in:
    an[IllegalArgumentException] should be thrownBy
      (11 of D)

    an[IllegalArgumentException] should be thrownBy
      (42 of H)
