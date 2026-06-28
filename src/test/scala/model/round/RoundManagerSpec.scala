package scalatro
package model.round

import model.commons.*
import model.game.{Blind, GameState}
import app.Msg.RoundAction
import app.Msg.RoundAction.{DiscardCards, PlayCards}
import model.round.{Round, RoundManager}

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[RoundManager]] */
class RoundManagerSpec extends AnyFlatSpec with Matchers with MockFactory:

  private val c1 = Card(Rank.Jack, Suit.Clubs)
  private val c2 = Card(Rank.Jack, Suit.Diamonds)
  private val c3 = Card(Rank.Queen, Suit.Clubs)
  private val c4 = Card(Rank.Queen, Suit.Hearts)
  private val c5 = Card(Rank.King, Suit.Clubs)
  private val c6 = Card(Rank.King, Suit.Diamonds)
  private val c7 = Card(Rank.Five, Suit.Hearts)

  private val initialHand = Seq(c1, c2, c3, c4)
  private val initialDeck = Deck(Seq(c5, c6, c7))

  private val roundNum = 1
  private val initialGameState = GameState.initial

  private given ScoreConfig = ScoreConfig.default

  private def runSequence(
      initialRound: Round,
      actions: Seq[RoundAction],
      render: Round => IO[Unit] = _ => IO.unit
  ): Round =
    val testProgram = for
      queue <- Queue.unbounded[IO, RoundAction]
      _ <- actions.traverse_(action => queue.offer(action))
      manager = RoundManager(render, queue.take)
      finalRound <- manager.startRound(initialRound)
      queueFinalSize <- queue.size
    yield
      queueFinalSize shouldBe 0 // all actions must have been consumed
      finalRound

    testProgram.unsafeRunSync()

  private def roundWithBlindScore(targetScore: Score.Score): Round =
    Round(
      Score.zero,
      initialHand,
      initialDeck,
      initialGameState.copy(blind = Blind(roundNum, targetScore))
    )

  private def assertRoundFinished(
      round: Round,
      expectedScore: Score.Score,
      consumedCards: Card*
  ): Unit =
    round.score shouldBe expectedScore
    consumedCards.foreach(card => round.hand should not contain card)
    round.isFinished shouldBe true

  /** A render mock function expecting to be called, in order, with exactly
    * `rounds`
    * @param rounds
    *   the rounds, in order, to render
    * @return
    *   the render function
    */
  private def mockRenderSequence(rounds: Round*): Round => IO[Unit] =
    val render = mockFunction[Round, IO[Unit]]
    inSequence:
      rounds.foreach(round => render expects round returning IO.unit)
    render

  "A RoundManager" should "return immediately if the initial round is already finished, rendering only once" in:
    val initialRound = Round(
      initialGameState.blind.targetScore,
      initialHand,
      initialDeck,
      initialGameState
    )
    val render = mockRenderSequence(initialRound)
    runSequence(initialRound, Seq(), render) shouldBe initialRound

  private case class PlayCase(
      description: String,
      targetScore: Score.Score,
      actions: Seq[RoundAction],
      consumedCards: Seq[Card]
  )

  private val playCases = Seq(
    PlayCase(
      description =
        "apply a PlayCards action and finish the round if the score reaches the blind",
      targetScore = Score.calculateScore(Seq(c1)),
      actions = Seq(PlayCards(Seq(c1))),
      consumedCards = Seq(c1)
    ),
    PlayCase(
      description =
        "accumulate score over multiple PlayCards actions until blind is beaten",
      targetScore =
        Score.calculateScore(Seq(c1)) + Score.calculateScore(Seq(c2)),
      actions = Seq(PlayCards(Seq(c1)), PlayCards(Seq(c2))),
      consumedCards = Seq(c1, c2)
    ),
    PlayCase(
      description =
        "apply a DiscardCards action, drawing new cards, and then continue until PlayCards beats the blind",
      targetScore = Score.calculateScore(Seq(c1, c2)),
      actions = Seq(DiscardCards(Seq(c1)), PlayCards(Seq(c1, c2))),
      consumedCards = Seq(c1, c2)
    )
  )

  playCases.foreach { playCase =>
    it should playCase.description in:
      val finalRound =
        runSequence(roundWithBlindScore(playCase.targetScore), playCase.actions)
      assertRoundFinished(
        finalRound,
        playCase.targetScore,
        playCase.consumedCards*
      )
  }

  it should "call render with the initial round, plus once per subsequent round values" in:
    val firstScore = Score.calculateScore(Seq(c1))
    val secondScore = firstScore + Score.calculateScore(Seq(c2))
    val initialRound = roundWithBlindScore(secondScore)

    val (drawnAfterFirst, deckAfterFirst) = initialRound.deck.draw(1)
    val roundAfterFirstPlay = initialRound.modify(
      score = firstScore,
      hand = initialHand.filterNot(_ == c1) :++ drawnAfterFirst,
      remainingPlays = initialRound.remainingPlays - 1,
      deck = deckAfterFirst
    )

    val (drawnAfterSecond, deckAfterSecond) = roundAfterFirstPlay.deck.draw(1)
    val finalRound = roundAfterFirstPlay.modify(
      score = secondScore,
      hand = roundAfterFirstPlay.hand.filterNot(_ == c2) :++ drawnAfterSecond,
      remainingPlays = roundAfterFirstPlay.remainingPlays - 1,
      deck = deckAfterSecond
    )

    val actions = Seq(PlayCards(Seq(c1)), PlayCards(Seq(c2)))
    val render =
      mockRenderSequence(initialRound, roundAfterFirstPlay, finalRound)

    runSequence(initialRound, actions, render) shouldBe finalRound
