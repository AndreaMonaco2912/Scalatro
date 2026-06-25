package scalatro
package model.round

import model.commons.*
import model.game.GameState

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[TurnActions]] */
class TurnActionsSpec extends AnyFlatSpec with Matchers with MockFactory:
  import TurnActions.*

  private val c1 = Card(Rank.Ace, Suit.Clubs)
  private val c2 = Card(Rank.Two, Suit.Hearts)
  private val c3 = Card(Rank.Three, Suit.Diamonds)
  private val c4 = Card(Rank.Four, Suit.Spades)
  private val c5 = Card(Rank.Five, Suit.Clubs)
  private val c6 = Card(Rank.Six, Suit.Hearts)

  private val initialHand: Seq[Card] = Seq(c1, c2, c3)
  private val initialDeck: Deck = Deck(Seq(c4, c5, c6))
  private val initialRound: Round =
    Round(Score.zero, initialHand, initialDeck, GameState.initial)
  private val simulatedScore = Score(123456)

  /** Creates a [[ScoreConfig]] that has mocked [[HandScoreCalculator]].
    *
    * The calculator must calculate score exactly `usages` times
    * @param usages
    *   the number of times to calculate the score
    * @return
    *   the score config
    */
  private def nUsagesScoreConfig(usages: Int): ScoreConfig =
    val simulatedScore = Score(123456)
    val scoreCalculator = mock[HandScoreCalculator]
    scoreCalculator.calculate
      .expects(*, *)
      .returning(simulatedScore)
      .repeated(usages)
    ScoreConfig.default.copy(calculator = scoreCalculator)

  /** Creates a mocked [[CardOrderer]] with a simulated behavior (it reverses
    * the order)
    *
    * The orderer must order the cards exactly `usages` time and expects
    * `expectedHand` to be ordered
    * @param expectedHand
    *   the hand to order
    * @param usages
    *   the number of times to order cards
    * @return
    *   the card orderer
    */
  private def nUsagesCardOrderer(expectedHand: Hand, usages: Int): CardOrderer =
    val cardOrderer = mock[CardOrderer]
    cardOrderer.order
      .expects(expectedHand)
      .returning(expectedHand.reverse)
      .repeated(usages)
    cardOrderer

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
    given ScoreConfig = nUsagesScoreConfig(1)
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.hand shouldBe Seq(c2, c3, c4)

  it should "remove the drawn cards from the deck" in:
    given ScoreConfig = nUsagesScoreConfig(1)
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.deck shouldBe Deck(Seq(c5, c6))

  it should "decrease remaining plays by 1" in:
    given ScoreConfig = nUsagesScoreConfig(1)
    val result = playCards(Seq(c1)).runS(initialRound).value
    result.remainingPlays shouldBe initialRound.remainingPlays - 1

  it should "increase the round score based on the played cards" in:
    given ScoreConfig = nUsagesScoreConfig(2)
    val cardsToPlay = Seq(c1)
    val result = playCards(cardsToPlay).runS(initialRound).value
    result.score shouldBe initialRound.score
      + Score.calculateScore(cardsToPlay)

  "orderCards" should "order the cards in hand using a the provided CardOrderer" in:
    given cardOrderer: CardOrderer = nUsagesCardOrderer(initialHand, 2)
    val result = orderCards.runS(initialRound).value
    result.hand shouldBe cardOrderer.order(initialHand)
