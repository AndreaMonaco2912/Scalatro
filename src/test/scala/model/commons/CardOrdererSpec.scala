package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[CardOrderer]] */
class CardOrdererSpec extends AnyFlatSpec, Matchers:
  private val testCards = Seq(
    Card(Rank.Eight, Suit.Spades),
    Card(Rank.Three, Suit.Hearts),
    Card(Rank.Four, Suit.Clubs)
  )

  "identity" should "keep the same order" in:
    CardOrderer.identity.order(testCards) shouldBe testCards

  "sortByRank" should "order cards by rank" in:
    CardOrderer.sortByRank.order(testCards) shouldBe Seq(
      Card(Rank.Eight, Suit.Spades),
      Card(Rank.Four, Suit.Clubs),
      Card(Rank.Three, Suit.Hearts)
    )

  it should "sort suits within each rank" in:
    val cards = Seq(
      Card(Rank.Five, Suit.Hearts),
      Card(Rank.Five, Suit.Spades),
      Card(Rank.Five, Suit.Diamonds),
      Card(Rank.Five, Suit.Clubs)
    )

    CardOrderer.sortByRank.order(cards).map(_.suit) shouldBe Suit.values.toSeq

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
      Card(Rank.Five, Suit.Spades),
      Card(Rank.Three, Suit.Spades),
      Card(Rank.Two, Suit.Spades)
    )

  "swapCards" should "swap two positions" in:
    CardOrderer.swapCards(0, 2).order(testCards) shouldBe Seq(
      Card(Rank.Four, Suit.Clubs),
      Card(Rank.Three, Suit.Hearts),
      Card(Rank.Eight, Suit.Spades)
    )

  it should "reject a negative first index" in:
    an[IllegalArgumentException] should be thrownBy
      CardOrderer.swapCards(-1, 0).order(testCards)

  it should "reject a second index past the end" in:
    an[IllegalArgumentException] should be thrownBy
      CardOrderer.swapCards(0, testCards.size).order(testCards)

  "moveCard" should "move a card from start to end" in:
    CardOrderer.moveCard(0, 2).order(testCards) shouldBe Seq(
      Card(Rank.Three, Suit.Hearts),
      Card(Rank.Four, Suit.Clubs),
      Card(Rank.Eight, Suit.Spades)
    )

  it should "move a card from end to start" in:
    CardOrderer.moveCard(2, 0).order(testCards) shouldBe Seq(
      Card(Rank.Four, Suit.Clubs),
      Card(Rank.Eight, Suit.Spades),
      Card(Rank.Three, Suit.Hearts)
    )

  it should "keep the same order if moving to the same position" in:
    CardOrderer.moveCard(1, 1).order(testCards) shouldBe testCards

  it should "reject a negative start index" in:
    an[IllegalArgumentException] should be thrownBy
      CardOrderer.moveCard(-1, 0).order(testCards)

  it should "reject an end index past the end" in:
    an[IllegalArgumentException] should be thrownBy
      CardOrderer.moveCard(0, testCards.size).order(testCards)
