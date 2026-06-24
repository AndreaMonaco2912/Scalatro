package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JokerTest extends AnyFlatSpec, Matchers:

  val defaultHandScore : HandScore = HandScore(50,20)
  val defaultJokerConfig : JokerConfig = JokerConfig(
    Seq.empty, Seq.empty, HandTypeLevels.initial
  )

  "Default effect" should "not modify the hand score" in:
    val joker : Joker = Joker(JokerType.CrazyJoker)
    joker.onCardHeld(defaultHandScore, Card(Rank.Ace, Suit.Clubs))(using defaultJokerConfig) shouldBe defaultHandScore
    joker.onCardScored(defaultHandScore, Card(Rank.Ace, Suit.Clubs))(using defaultJokerConfig) shouldBe defaultHandScore

  "Clever joker" should "increase score by 80 chips when hand contains Two Pair" in:
    val joker : Joker = Joker(JokerType.CleverJoker)
    val c1 = Card(Rank.Ace, Suit.Clubs)
    val c2 = Card(Rank.Ten, Suit.Hearts)
    val jokerConfig = JokerConfig(Seq(c1,c1,c2,c2),Seq.empty,HandTypeLevels.initial)
    joker.independent(defaultHandScore)(using jokerConfig) shouldBe defaultHandScore+HandScore(80,0)