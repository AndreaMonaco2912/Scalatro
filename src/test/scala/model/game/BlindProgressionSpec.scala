package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.Score
import model.commons.Score.Score

import model.game.*

class BlindProgressionSpec extends AnyFlatSpec, Matchers:

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
