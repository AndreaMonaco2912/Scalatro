package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.commons.Score
import model.commons.Score.Score

class BlindTest extends AnyFlatSpec, Matchers:

  val start: Blind = Blind.first

  "nextBlind" should "increment the round number by one" in:
    val result = Blind.nextBlind.runS(start).value
    result.roundNum shouldBe start.roundNum + 1

  it should "scale the score by the increaseAmount" in:
    val result = Blind.nextBlind.runS(start).value
    result.targetScore shouldBe start.targetScore * Score(Blind.increaseAmount)

  "isBeaten" should "be true if the achieved score meets the target" in:
    Blind.first.isBeaten(Score(300)) shouldBe true

  it should "fail when the achieved score is below the target" in:
    Blind.first.isBeaten(Score(299)) shouldBe false