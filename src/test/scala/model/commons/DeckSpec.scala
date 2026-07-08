package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.rng.ScalatroRng
import model.rng.Types.Seed

class DeckSpec extends AnyFlatSpec, Matchers:
  import Deck.*
  import model.extra.CardBuilder.*
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

  it should "change the card order" in:
    val d = Deck()
    d.shuffle.cards should not equal d.cards

  it should "preserve every card when shuffled" in:
    val d = Deck()
    d.shuffle.cards.toSet shouldBe d.cards.toSet

  "draw" should "return the requested number of cards and the rest" in:
    val drawSize = 5
    val (hand, rest) = Deck().draw(drawSize)
    hand.size shouldBe drawSize
    rest.size shouldBe pokerDeckFullSize - drawSize
    (hand ++ rest.cards) should contain theSameElementsAs Deck().cards

  it should "not draw more cards than available" in:
    Deck().draw(53) shouldBe Deck().draw(52)

  it should "reject a negative count" in:
    an[IllegalArgumentException] should be thrownBy Deck().draw(-1)

  "Deck(cards)" should "create a deck containing only the given cards in the given order" in:
    val ordered = Seq(A of S, 2 of H, K of C)
    val deck = Deck(ordered)
    deck.cards shouldBe ordered

  "add" should "append the given card to the deck" in:
    val base = Deck(Seq(2 of H, 5 of C))
    val result = base.add(A of S)
    result.cards should contain theSameElementsAs base.cards :+ (A of S)

  "sort" should "order by suit, then by descending rank within each suit" in:
    val deck = Deck(Seq(2 of H, A of S, K of S, 5 of C))
    deck.sort.cards shouldBe Seq(A of S, K of S, 2 of H, 5 of C)
