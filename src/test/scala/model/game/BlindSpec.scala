package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.commons.Score
import model.commons.Score.Score

class BlindSpec extends AnyFlatSpec, Matchers:

  val start: Blind = Blind.first

  "next" should "increment the round number by one" in:
    val result = start.next
    result.roundNum shouldBe start.roundNum + 1

  it should "scale the score by the increaseAmount" in:
    val result = start.next
    result.targetScore shouldBe start.targetScore * Score(Blind.increaseAmount)

  "isBeaten" should "be true if the achieved score meets the target" in:
    Blind.first.isBeaten(Blind.initialScore) shouldBe true

  it should "fail when the achieved score is below the target" in:
    Blind.first.isBeaten(Blind.initialScore - Score(1)) shouldBe false