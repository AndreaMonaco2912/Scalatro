package scalatro
package model.round

import model.commons.*
import model.game.{Blind, GameState}
import model.round.RoundAction.{DiscardCards, PlayCards}
import model.round.{Round, RoundAction, RoundManager}

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[RoundManager]] */
class RoundManagerSpec extends AnyFlatSpec with Matchers:

  private val c1 = Card(Rank.Jack, Suit.Clubs)
  private val c2 = Card(Rank.Jack, Suit.Diamonds)
  private val c3 = Card(Rank.Queen, Suit.Clubs)
  private val c4 = Card(Rank.Queen, Suit.Hearts)
  private val c5 = Card(Rank.King, Suit.Clubs)
  private val c6 = Card(Rank.King, Suit.Diamonds)

  private val initialHand = Seq(c1, c2, c3, c4)
  private val initialDeck = Deck(Seq(c5, c6))

  private val roundNum = 1
  private val defaultGameState = GameState.initial

  private def runSequence(
      initialRound: Round,
      actions: Seq[RoundAction]
  ): Round =
    val testProgram = for
      queue <- Queue.unbounded[IO, RoundAction]
      manager = RoundManager(_ => IO.unit, queue.take)
      _ <- actions.traverse_(action => queue.offer(action))
      finalRound <- manager.startRound(initialRound)
    yield finalRound
    testProgram.unsafeRunSync()

  "A RoundManager" should "return immediately if the initial round is already finished" in:
    val gameState = GameState.initial
    val initialRound = Round(Score(300), initialHand, initialDeck, gameState)
    val finalRound = runSequence(initialRound, Seq())
    finalRound shouldBe initialRound

  it should "apply a PlayCards action and finish the round if the score reaches the blind" in:
    val score = Score(15.0)
    val blind = Blind(roundNum, score)
    val initialRound = Round(
      Score.zero,
      initialHand,
      initialDeck,
      defaultGameState.copy(blind = blind)
    )
    val actions = Seq(PlayCards(Seq(c1)))
    val finalRound = runSequence(initialRound, actions)
    finalRound.score shouldBe score
    finalRound.hand should not contain c1
    finalRound.isFinished shouldBe true

  it should "accumulate score over multiple PlayCards actions until blind is beaten" in:
    val score = Score(30.0)
    val blind = Blind(roundNum, score)
    val initialRound = Round(
      Score.zero,
      initialHand,
      initialDeck,
      defaultGameState.copy(blind = blind)
    )
    val actions = Seq(
      PlayCards(Seq(c1)),
      PlayCards(Seq(c2))
    )
    val finalRound = runSequence(initialRound, actions)
    finalRound.score shouldBe score
    finalRound.hand should not contain c1
    finalRound.hand should not contain c2
    finalRound.isFinished shouldBe true

  it should "apply a DiscardCards action, drawing new cards, and then continue until PlayCards beats the blind" in:
    val score = Score(15.0)
    val blind = Blind(roundNum, score)
    val initialRound = Round(
      Score.zero,
      initialHand,
      initialDeck,
      defaultGameState.copy(blind = blind)
    )
    val initialDeckSize = initialRound.deck.size
    val actions = Seq(
      DiscardCards(Seq(c1)),
      PlayCards(Seq(c2))
    )
    val finalRound = runSequence(initialRound, actions)
    finalRound.score shouldBe score
    finalRound.hand should not contain c1
    finalRound.hand should not contain c2
    finalRound.isFinished shouldBe true
