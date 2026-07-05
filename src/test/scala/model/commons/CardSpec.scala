package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardSpec extends AnyFlatSpec, Matchers:

  "An Ace" should "add 11 chips to score" in:
    val AceOfDiamonds = Card(Rank.Ace, Suit.Diamonds)
    AceOfDiamonds.onScored shouldBe Seq(HandScoreModification.FlatChips(Chips(11)))

  "A Jack" should "add 10 chips to score" in:
    val JackOfSpades = Card(Rank.Jack, Suit.Spades)
    JackOfSpades.onScored shouldBe Seq(HandScoreModification.FlatChips(Chips(10)))
