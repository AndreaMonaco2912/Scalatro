package scalatro
package model.round

import model.commons.{Card, Deck, Suit}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TurnActionsSpec extends AnyFlatSpec with Matchers:
  import TurnActions.*

  private val c1 = Card(1, Suit.Clubs)
  private val c2 = Card(2, Suit.Hearts)
  private val c3 = Card(3, Suit.Diamonds)
  private val c4 = Card(4, Suit.Spades)
  private val c5 = Card(5, Suit.Clubs)
  private val c6 = Card(6, Suit.Hearts)

  private val initialHand: Seq[Card] = Seq(c1, c2, c3)
  private val initialDeck: Deck = Deck(Seq(c4, c5, c6))
  private val initialRound: Round = Round(Score.zero, initialHand, initialDeck)

  "removeCards" should "remove the given cards from the hand without changing deck" in:
    val expectedHand = Seq(c2)
    val expectedDeck = initialDeck
    val result = removeCards(Seq(c1, c3)).runS(initialRound).value
    result.hand shouldBe expectedHand
    result.deck shouldBe expectedDeck

  "increaseScore" should "increase the round score by the given amount" in:
    val delta = Score(7.0)
    val expectedScore = initialRound.score + delta
    val result = increaseScore(delta).runS(initialRound).value
    result.score shouldBe expectedScore

  "drawCards" should "draw the requested number of cards from the deck into the hand" in:
    val expectedHand = initialHand ++ Seq(c4, c5)
    val expectedDeck = Deck(Seq(c6))
    val result = drawCards(2).runS(initialRound).value
    result.hand shouldBe expectedHand
    result.deck shouldBe expectedDeck

  "discardCards" should "remove the cards from the hand and replace them with cards from the deck" in:
    val expectedHand = Seq(c2, c3, c4)
    val expectedDeck = Deck(Seq(c5, c6))
    val result = discardCards(Seq(c1)).runS(initialRound).value
    result.hand shouldBe expectedHand
    result.deck shouldBe expectedDeck

  "playCards" should "discard the cards, draw replacements, and increase the score" in:
    val expectedHand = Seq(c2, c3, c4)
    val expectedDeck = Deck(Seq(c5, c6))
    val expectedScore = initialRound.score + Score(1.0)
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.hand shouldBe expectedHand
    result.deck shouldBe expectedDeck
    result.score shouldBe expectedScore
