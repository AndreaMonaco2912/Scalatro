package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.commons.Score
import model.commons.Score.Score

class BlindTest extends AnyFlatSpec, Matchers:

  "nextBlind" should "increment the round number by one" in:
    val start = Blind.firstBlind
    val (result, _) = Blind.nextBlind.run(start).value
    result.roundNum shouldBe start.roundNum + 1

  it should "scale the score by the increaseAmount" in:
    val start = Blind.firstBlind
    val (result, _) = Blind.nextBlind.run(start).value
    result.score shouldBe start.score * Score(Blind.increaseAmount)
