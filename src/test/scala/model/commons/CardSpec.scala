package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardSpec extends AnyFlatSpec, Matchers:

  "An Ace" should "add 11 chips to score" in:
    val AceOfDiamonds = Card(Rank.Ace, Suit.Diamonds)
    val previousScore = HandScore(0, 0)
    AceOfDiamonds.onScored(previousScore) shouldBe HandScore(11, 0)

  "A Jack" should "add 10 chips to score" in:
    val AceOfDiamonds = Card(Rank.Jack, Suit.Spades)
    val previousScore = HandScore(50, 0)
    AceOfDiamonds.onScored(previousScore) shouldBe HandScore(60, 0)