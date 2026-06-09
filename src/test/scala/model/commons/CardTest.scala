package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardTest extends AnyFlatSpec, Matchers:

  "An Ace" should "add 11 chips to score" in:
    val AceOfDiamonds = Card(Suit.Diamonds, Rank.Ace)
    val previousScore = HandScore(0, 0)
    AceOfDiamonds.onScored(previousScore) shouldBe HandScore(11,0)
