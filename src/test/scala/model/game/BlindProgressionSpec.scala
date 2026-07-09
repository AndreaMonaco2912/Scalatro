package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{Score, ScoreConfig}
import model.commons.Score.{Score, calculateScore}
import model.game.*
import model.extra.{Cards, CustomScenario}
import model.extra.CardBuilder.*

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import app.Msg.RoundAction
import app.Msg.RoundAction.{DiscardCards, PlayCards}
import model.round.{RoundManager, RoundState}

class BlindProgressionSpec extends AnyFlatSpec, Matchers:

  private given defaultScoreConfig: ScoreConfig = ScoreConfig.default

  private def runSequence(
      initialRoundState: RoundState,
      actions: Seq[RoundAction],
      render: RoundState => IO[Unit] = _ => IO.unit
  ): RoundState =
    val testProgram = for
      queue <- Queue.unbounded[IO, RoundAction]
      _ <- actions.traverse_(action => queue.offer(action))
      manager = RoundManager(render, queue.take)
      finalRound <- manager.startRound(initialRoundState)
      queueFinalSize <- queue.size
    yield
      queueFinalSize shouldBe 0 // all actions must have been consumed
      finalRound

    testProgram.unsafeRunSync()

  val start: BlindProgression = BlindProgression.first

  "next" should "increment the round number by one" in:
    val result = start.next
    result.roundNum shouldBe start.roundNum + 1

  it should "move from SmallBlind to BigBlind within the same ante" in:
    start.blind shouldBe SmallBlind
    val result = start.next
    result.blind shouldBe BigBlind
    result.anteNum shouldBe start.anteNum

  it should "move from BigBlind to a Boss within the same ante" in:
    val result = start.next.next
    result.isBoss shouldBe true
    result.anteNum shouldBe start.anteNum

  it should "fail when the achieved score is below the target" in:
    BlindProgression.first.isBeaten(
      BlindProgression.initialScore - Score(1)
    ) shouldBe false

  "targetScore" should "equal initialScore for the first small blind" in:
    start.targetScore shouldBe BlindProgression.initialScore

  it should "be double the small blind's score for the boss blind" in:
    val small = start.targetScore
    val boss = start.next.next
    boss.targetScore shouldBe small * 2

  it should "be the average of small and boss for the big blind" in:
    val small = start.targetScore
    val big = start.next
    big.targetScore shouldBe (small + small * 2) / 2

  it should "triple the small blind's score on the next ante" in:
    val small = start.targetScore
    val nextAnteSmall = start.next.next.next
    nextAnteSmall.targetScore shouldBe small * 3

  "isBeaten" should "be true if the achieved score meets the target" in:
    BlindProgression.first.isBeaten(BlindProgression.initialScore) shouldBe true

  it should "fail when the achieved score is below the target" in:
    BlindProgression.first.isBeaten(
      BlindProgression.initialScore - Score(1)
    ) shouldBe false

// Tests for every boss blind

  "The Needle" should "permit to play only one hand" in:
    val theNeedleScenario: CustomScenario = Cards(A of S) inBlind TheNeedle
    val currentRound = theNeedleScenario.buildRound
    val finalRound = runSequence(currentRound, Seq(PlayCards(Seq(A of S))))

    finalRound.remainingPlays shouldBe 0
    finalRound.isFinished shouldBe true

  "The Flint" should "half Base Chips and Mult for played poker hands" in:
    val theFlintScenario: CustomScenario =
      Cards(A of S, A of C) inBlind TheFlint
    val currentRound = theFlintScenario.buildRound

    given theFlintScoreConfig: ScoreConfig =
      ScoreConfig.default.copy(blind = TheFlint)

    val actualScore: Score = calculateScore(currentRound.hand)
    /*
     * Played cards: 2 Aces --> Pair
     * Base chips for Pair: 10
     * Base mult for Pair: 2
     * So, after The Flint effect,
     * Actual base chips: 5
     * Actual base mult: 1
     * So the final score is: (5 + 11 + 11) * 1
     * */
    actualScore shouldBe Score(27)

  "The Water" should "set the available discards to 0" in:
    val theWaterScenario: CustomScenario =
      Cards(A of S, A of C) inBlind TheWater withTarget Score.calculateScore(
        Seq(A of S)
      )
    val currentRound = theWaterScenario.buildRound
    val finalRound = runSequence(currentRound, Seq(PlayCards(Seq(A of S))))

    finalRound.remainingDiscards shouldBe 0

  "The Head" should "debuff all Heart cards" in:
    given ScoreConfig = ScoreConfig.default.copy(blind = TheHead)

    val scoreWithDebuffedHeart = calculateScore(Seq(A of H))
    // Scores only High card base chips and mult (5 * 1)
    scoreWithDebuffedHeart shouldBe Score(5)

  "The Club" should "debuff all Club cards" in:
    given ScoreConfig = ScoreConfig.default.copy(blind = TheClub)

    val scoreWithDebuffedHeart = calculateScore(Seq(A of C))
    // Scores only High card base chips and mult (5 * 1)
    scoreWithDebuffedHeart shouldBe Score(5)

  "The Goad" should "debuff all Spade cards" in:
    given ScoreConfig = ScoreConfig.default.copy(blind = TheGoad)

    val scoreWithDebuffedHeart = calculateScore(Seq(A of S))
    // Scores only High card base chips and mult (5 * 1)
    scoreWithDebuffedHeart shouldBe Score(5)

  "The Window" should "debuff all Diamond cards" in :

    given ScoreConfig = ScoreConfig.default.copy(blind = TheWindow)

    val scoreWithDebuffedHeart = calculateScore(Seq(A of D))
    // Scores only High card base chips and mult (5 * 1)
    scoreWithDebuffedHeart shouldBe Score(5)

  "The Plant" should "debuff all face cards (Jacks, Queens and Kings are face cards)" in :

    given ScoreConfig = ScoreConfig.default.copy(blind = ThePlant)

    val scoreWithDebuffedHeart = calculateScore(Seq(J of S, Q of S, K of S))
    // Scores only High card base chips and mult (5 * 1)
    scoreWithDebuffedHeart shouldBe Score(5)