package scalatro
package model.round

import model.commons.{Card, Deck, Suit}
import model.round.Builder.{testDeck, testHand}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, Suite}

trait Builder extends BeforeAndAfterEach:
  this: Suite =>
  val round: Round = Round(Score.zero, testHand, testDeck)

object Builder:
  val testHand: Seq[Card] = Seq(
    Card(1, Suit.Clubs),
    Card(2, Suit.Hearts),
    Card(3, Suit.Diamonds)
  )
  val testDeck: Deck = Deck(
    Seq(
      Card(4, Suit.Spades),
      Card(5, Suit.Clubs),
      Card(6, Suit.Hearts)
    )
  )

class RoundActionsSpec extends AnyFlatSpec with Matchers with Builder:
  import RoundActions.*

  "Drawing a card from non empty deck" should "draw it" in:
    val newRound = drawCard.runS(round).value
    newRound.hand.size should be(round.hand.size + 1)
    newRound.deck.size should be(round.deck.size - 1)

  "Discarding a card" should "remove it from hand" in:
    val card = Card(1, Suit.Clubs)
    round.hand should contain(card)
    val newRound = discardCardAndReplace(card).runS(round).value
    newRound.hand should not contain card

  "Discarding a card" should "replace it from deck" in:
    val card1 = Card(1, Suit.Clubs)
    val card2 = Card(4, Suit.Spades)
    round.hand should not contain card2
    val newRound = discardCardAndReplace(card1).runS(round).value
    newRound.hand should contain(card2)

  "Playing a card" should "discard and replace it" in:
    val card = Card(1, Suit.Clubs)
    val round1 = discardCardAndReplace(card).runS(round).value
    val round2 = playCard(card).runS(round).value
    round1.hand should be(round2.hand)
    round1.deck should be(round2.deck)

  "Playing a card" should "increase the score" in:
    val card = Card(1, Suit.Clubs)
    playCard(card).runS(round).value.score should be(
      round.score + Score(card.rank)
    )
