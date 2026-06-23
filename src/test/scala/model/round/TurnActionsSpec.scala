package scalatro
package model.round

import model.commons.*
import model.game.Blind

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[TurnActions]] */
class TurnActionsSpec extends AnyFlatSpec with Matchers:
  import TurnActions.*

  private given calculator: HandScoreCalculator = BasicHandScoreCalculator
  private given cardOrderer: CardOrderer = CardOrderer.sortByRank

  private val c1 = Card(Rank.Ace, Suit.Clubs)
  private val c2 = Card(Rank.Two, Suit.Hearts)
  private val c3 = Card(Rank.Three, Suit.Diamonds)
  private val c4 = Card(Rank.Four, Suit.Spades)
  private val c5 = Card(Rank.Five, Suit.Clubs)
  private val c6 = Card(Rank.Six, Suit.Hearts)

  private val initialHand: Seq[Card] = Seq(c1, c2, c3)
  private val initialDeck: Deck = Deck(Seq(c4, c5, c6))
  private val initialRound: Round =
    Round(Score.zero, initialHand, initialDeck, Blind.first)

  "discardCards" should "remove discarded cards and add drawn replacements to hand" in:
    val result = discardCards(Seq(c1)).runS(initialRound).value
    result.hand shouldBe Seq(c2, c3, c4)

  it should "remove the drawn cards from the deck" in:
    val result = discardCards(Seq(c1)).runS(initialRound).value
    result.deck shouldBe Deck(Seq(c5, c6))

  it should "decrease remaining discards by 1" in:
    val result = discardCards(Seq(c1)).runS(initialRound).value
    result.remainingDiscards shouldBe initialRound.remainingDiscards - 1

  "playCards" should "remove played cards and add drawn replacements to hand" in:
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.hand shouldBe Seq(c2, c3, c4)

  it should "remove the drawn cards from the deck" in:
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.deck shouldBe Deck(Seq(c5, c6))

  it should "decrease remaining plays by 1" in:
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.remainingPlays shouldBe initialRound.remainingPlays - 1

  it should "increase the round score based on the played cards" in:
    val cardsToPlay = Seq(c1)
    val result = playCards(cardsToPlay).runS(initialRound).value
    result.score shouldBe initialRound.score
      + Score.calculateHandScore(using BasicHandScoreCalculator)(cardsToPlay)

  "orderCards" should "order the cards in hand using a given CardOrderer" in:
    val result = orderCards.runS(initialRound).value
    result.hand shouldBe cardOrderer.order(initialHand)
