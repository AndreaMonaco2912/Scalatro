package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.CardBuilder.*

class CardBuilderSpec extends AnyFlatSpec with Matchers:

  "CardBuilder" should "correctly minimum and maximum numeric cards" in:
    (2 | H) shouldBe Card(Rank.Two, Suit.Hearts)
    (10 | S) shouldBe Card(Rank.Ten, Suit.Spades)

  it should "correctly create middle numeric cards across all suits" in:
    (5 | C) shouldBe Card(Rank.Five, Suit.Clubs)
    (7 | D) shouldBe Card(Rank.Seven, Suit.Diamonds)
    (9 | H) shouldBe Card(Rank.Nine, Suit.Hearts)

  it should "correctly create face cards" in:
    (J | S) shouldBe Card(Rank.Jack, Suit.Spades)
    (Q | D) shouldBe Card(Rank.Queen, Suit.Diamonds)
    (K | H) shouldBe Card(Rank.King, Suit.Hearts)
    (A | C) shouldBe Card(Rank.Ace, Suit.Clubs)

  it should "throw an IllegalArgumentException for numbers below 2" in:
    an[IllegalArgumentException] should be thrownBy
      (1 | H)

    an[IllegalArgumentException] should be thrownBy
      (0 | S)

    an[IllegalArgumentException] should be thrownBy
      (-22 | C)

  it should "throw an IllegalArgumentException for numbers above 10" in:
    an[IllegalArgumentException] should be thrownBy
      (11 | D)

    an[IllegalArgumentException] should be thrownBy
      (42 | H)
