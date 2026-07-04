package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardSpec extends AnyFlatSpec, Matchers:

  "An Ace" should "add 11 chips to score" in:
    val AceOfDiamonds = Card(Rank.Ace, Suit.Diamonds)
    val previousScore = HandScore.zero
    AceOfDiamonds.onScored(previousScore) shouldBe HandScore(Chips(11))

  "A Jack" should "add 10 chips to score" in:
    val AceOfDiamonds = Card(Rank.Jack, Suit.Spades)
    val previousScore = HandScore(Chips(50))
    AceOfDiamonds.onScored(previousScore) shouldBe HandScore(Chips(60))
