package scalatro
package model.commons

import scala.util.Random
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.rng.ScalatroRng
import model.rng.Types.Seed

class DeckTest extends AnyFlatSpec, Matchers:
  import Deck.*
  import Rank.*
  given ScalatroRng = ScalatroRng(Seed(42))
  val pokerDeckFullSize = 52

  "A Deck" should "contain 52 cards" in:
    Deck().size shouldBe pokerDeckFullSize

  it should "have 13 ranks in each of the 4 suits" in:
    Deck().cards
      .groupBy(_.suit)
      .view
      .mapValues(_.size)
      .values
      .toSet shouldBe Set(13)

  it should "preserve every card when shuffled" in:
    val d = Deck()
    d.shuffle.cards.toSet shouldBe d.cards.toSet

  "draw" should "return the requested number of cards and the rest" in:
    val drawSize = 5
    val (hand, rest) = Deck().draw(drawSize)
    hand.size shouldBe drawSize
    rest.size shouldBe pokerDeckFullSize - drawSize

  it should "reject drawing more than the deck holds" in:
    an[IllegalArgumentException] should be thrownBy Deck().draw(53)

  it should "reject a negative count" in:
    an[IllegalArgumentException] should be thrownBy Deck().draw(-1)

//  "score" should "count face cards as 10" in:
//    score(Card(Jack, Suit.Spades)) shouldBe Score(10)
//    score(Card(King, Suit.Hearts)) shouldBe Score(10)
//
//  it should "count an ace as 11" in:
//    score(Card(Ace, Suit.Clubs)) shouldBe Score(11)
//
//  it should "count number cards at face value" in:
//    score(Card(Seven, Suit.Diamonds)) shouldBe Score(7)
