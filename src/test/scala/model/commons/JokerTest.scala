package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JokerTest extends AnyFlatSpec, Matchers:

  val defaultHandScore: HandScore = HandScore(50, 20)
  val defaultJokerConfig: JokerConfig = JokerConfig(
    Seq.empty,
    Seq.empty,
    HandTypeLevels.initial
  )

  "Default effect" should "not modify the hand score" in:
    val joker = new Joker
    val c = Card(Rank.Ace, Suit.Clubs)
    joker.independent(defaultHandScore)(using
      defaultJokerConfig
    ) shouldBe defaultHandScore
    joker.onCardHeld(defaultHandScore, c)(using
      defaultJokerConfig
    ) shouldBe defaultHandScore
    joker.onCardScored(defaultHandScore, c)(using
      defaultJokerConfig
    ) shouldBe defaultHandScore
    joker.onHandPlayed(defaultHandScore, Seq(c, c, c))(using
      defaultJokerConfig
    ) shouldBe defaultHandScore

  "Clever Joker" should "increase score by +80 chips if played hand contains Two Pair" in:
    val joker: Joker = Joker(JokerType.CleverJoker)
    val c1 = Card(Rank.Ace, Suit.Clubs)
    val c2 = Card(Rank.Ten, Suit.Hearts)
    val jokerConfig =
      JokerConfig(Seq(c1, c1, c2, c2), Seq.empty, HandTypeLevels.initial)
    joker.independent(defaultHandScore)(using
      jokerConfig
    ) shouldBe defaultHandScore + HandScore(80, 0)

  "Crafty Joker" should "increase score by +80 Chips if played hand contains a Flush" in:
    val joker: Joker = Joker(JokerType.CraftyJoker)
    val suit = Suit.Hearts
    val c1 = Card(Rank.Two, suit)
    val c2 = Card(Rank.Four, suit)
    val c3 = Card(Rank.Six, suit)
    val jokerConfig =
      JokerConfig(Seq(c1, c1, c2, c2, c3), Seq.empty, HandTypeLevels.initial)
    joker.independent(defaultHandScore)(using
      jokerConfig
    ) shouldBe defaultHandScore + HandScore(80, 0)

  "Crazy Joker" should "increase score by +12 Mult if played hand contains a Straight" in:
    val joker: Joker = Joker(JokerType.CrazyJoker)
    val c1 = Card(Rank.Two, Suit.Clubs)
    val c2 = Card(Rank.Three, Suit.Hearts)
    val c3 = Card(Rank.Four, Suit.Clubs)
    val c4 = Card(Rank.Five, Suit.Hearts)
    val c5 = Card(Rank.Six, Suit.Hearts)
    val jokerConfig =
      JokerConfig(Seq(c1, c2, c3, c4, c5), Seq.empty, HandTypeLevels.initial)
    joker.independent(defaultHandScore)(using
      jokerConfig
    ) shouldBe defaultHandScore + HandScore(0, 12)

  "Devious Joker" should "increase score by +100 Chips if played hand contains a Straight" in:
    val joker: Joker = Joker(JokerType.DeviousJoker)
    val c1 = Card(Rank.Two, Suit.Clubs)
    val c2 = Card(Rank.Three, Suit.Hearts)
    val c3 = Card(Rank.Four, Suit.Clubs)
    val c4 = Card(Rank.Five, Suit.Hearts)
    val c5 = Card(Rank.Six, Suit.Hearts)
    val jokerConfig =
      JokerConfig(Seq(c1, c2, c3, c4, c5), Seq.empty, HandTypeLevels.initial)
    joker.independent(defaultHandScore)(using
      jokerConfig
    ) shouldBe defaultHandScore + HandScore(100, 0)
