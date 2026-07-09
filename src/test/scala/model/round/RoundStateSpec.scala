package scalatro
package model.round

import model.commons.Rank.Ace
import model.commons.Score.Score
import model.commons.Suit.Spades
import model.commons.{Card, Deck, Score}
import model.game.{GameState, HandInformation}
import model.rng.ScalatroRng

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[RoundState]] */
class RoundStateSpec extends AnyFlatSpec, Matchers:

  private def initialRound: RoundState = RoundState(
    score = Score.zero,
    hand = Seq(Card(Ace, Spades)),
    deck = Deck(),
    gameState = GameState.initial
  )

  private val testHand = Seq(Card(Ace, Spades))

  "Modifying the score" should "return a new Round with the updated score" in:
    val newScore = Score(20)
    val updatedRound = initialRound.modify(score = newScore)

    updatedRound.score shouldBe newScore
    updatedRound.hand shouldBe initialRound.hand
    updatedRound.deck shouldBe initialRound.deck
    updatedRound.remainingPlays shouldBe initialRound.remainingPlays
    updatedRound.remainingDiscards shouldBe initialRound.remainingDiscards
    updatedRound.gameState shouldBe initialRound.gameState

  "Modifying the hand" should "return a new Round with the updated hand" in:
    val updatedRound = initialRound.modify(hand = testHand)

    updatedRound.hand shouldBe testHand
    updatedRound.score shouldBe initialRound.score
    updatedRound.deck shouldBe initialRound.deck
    updatedRound.remainingPlays shouldBe initialRound.remainingPlays
    updatedRound.remainingDiscards shouldBe initialRound.remainingDiscards
    updatedRound.gameState shouldBe initialRound.gameState

  "Modifying the deck" should "return a new Round with the updated deck" in:
    val newDeck = Deck().shuffle(using ScalatroRng.default)
    val updatedRound = initialRound.modify(deck = newDeck)

    updatedRound.deck shouldBe newDeck
    updatedRound.score shouldBe initialRound.score
    updatedRound.hand shouldBe initialRound.hand
    updatedRound.remainingPlays shouldBe initialRound.remainingPlays
    updatedRound.remainingDiscards shouldBe initialRound.remainingDiscards
    updatedRound.gameState shouldBe initialRound.gameState

  "Modifying the remaining plays" should "return a new Round with the updated remaining plays" in:
    val newRemainingPlays = initialRound.remainingPlays - 1
    val updatedRound = initialRound.modify(remainingPlays = newRemainingPlays)

    updatedRound.remainingPlays shouldBe newRemainingPlays
    updatedRound.score shouldBe initialRound.score
    updatedRound.hand shouldBe initialRound.hand
    updatedRound.deck shouldBe initialRound.deck
    updatedRound.remainingDiscards shouldBe initialRound.remainingDiscards
    updatedRound.gameState shouldBe initialRound.gameState

  "Modifying the remaining discards" should "return a new Round with the updated remaining discards" in:
    val newRemainingDiscards = initialRound.remainingDiscards - 1
    val updatedRound =
      initialRound.modify(remainingDiscards = newRemainingDiscards)

    updatedRound.remainingDiscards shouldBe newRemainingDiscards
    updatedRound.score shouldBe initialRound.score
    updatedRound.hand shouldBe initialRound.hand
    updatedRound.deck shouldBe initialRound.deck
    updatedRound.remainingPlays shouldBe initialRound.remainingPlays
    updatedRound.gameState shouldBe initialRound.gameState

  "A Round created with apply" should "initialize remaining plays to the game state's hand number" in:
    val gameState = GameState.initial.copy(
      handInformation =
        HandInformation(handSize = 5, handNum = 7, discardNum = 2)
    )
    val round = RoundState(Score.zero, testHand, Deck(), gameState)

    round.remainingPlays shouldBe gameState.handInformation.handNum

  it should "initialize remaining discards to the game state's discard number" in:
    val gameState = GameState.initial.copy(
      handInformation =
        HandInformation(handSize = 5, handNum = 7, discardNum = 2)
    )
    val round = RoundState(Score.zero, testHand, Deck(), gameState)

    round.remainingDiscards shouldBe gameState.handInformation.discardNum

  "A Round" should "always know which the best play from the current hand" in:
    initialRound.bestPlay should not be empty

  "A Round" should "be finished when the blind is beaten" in:
    val beatingScore = GameState.initial.blindProgression.targetScore
    val round = initialRound.modify(score = beatingScore)

    round.isFinished shouldBe true

  "bestPlay" should "return an empty sequence when the hand is empty" in:
    val round = initialRound.modify(hand = Seq())
    round.bestPlay shouldBe Seq()

  it should "be finished when there are no remaining plays" in:
    val round = initialRound.modify(remainingPlays = 0)

    round.isFinished shouldBe true

  it should "be finished when there are no remaining cards in hand" in:
    val round = initialRound.modify(hand = Seq())

    round.isFinished shouldBe true

  it should "not be finished when the blind otherwise" in:
    initialRound.isFinished shouldBe false
