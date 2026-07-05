package scalatro
package model.commons

import model.commons.HandScore
import model.game.GameState
import model.round.{RoundState, RoundStateModification}
import model.extra.CardBuilder.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JokerSpec extends AnyFlatSpec, Matchers:

  val defaultHandScore: HandScore = HandScore(Chips(50), Mult(20))

  def getScores(cards: Seq[Card], joker: Joker): (HandScore, HandScore) =
    val scoreConfigWithJoker: ScoreConfig = ScoreConfig(
      Seq(joker),
      HandTypeLevels.initial,
      HandScoreCalculator.default
    )
    val scoreConfigWithoutJoker: ScoreConfig = ScoreConfig(
      Seq.empty,
      HandTypeLevels.initial,
      HandScoreCalculator.default
    )
    val scoreWithJoker =
      Score.calculateHandScore(cards)(using scoreConfigWithJoker)
    val scoreWithoutJoker =
      Score.calculateHandScore(cards)(using scoreConfigWithoutJoker)
    (scoreWithJoker, scoreWithoutJoker)

  "Clever Joker" should "increase score by +80 chips if played hand contains Two Pair" in:
    val joker: Joker = JokerType.CleverJoker
    val c1 = Card(Rank.Ace, Suit.Clubs)
    val c2 = Card(Rank.Ten, Suit.Hearts)
    val cards = Seq(c1, c1, c2, c2)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Chips(80)

  "Crafty Joker" should "increase score by +80 Chips if played hand contains a Flush" in:
    val joker: Joker = JokerType.CraftyJoker
    val suit = Suit.Hearts
    val c1 = Card(Rank.Two, suit)
    val c2 = Card(Rank.Four, suit)
    val c3 = Card(Rank.Six, suit)
    val cards = Seq(c1, c1, c2, c2, c3)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Chips(80)

  "Crazy Joker" should "increase score by +12 Mult if played hand contains a Straight" in:
    val joker: Joker = JokerType.CrazyJoker
    val c1 = Card(Rank.Two, Suit.Clubs)
    val c2 = Card(Rank.Three, Suit.Hearts)
    val c3 = Card(Rank.Four, Suit.Clubs)
    val c4 = Card(Rank.Five, Suit.Hearts)
    val c5 = Card(Rank.Six, Suit.Hearts)
    val cards = Seq(c1, c2, c3, c4, c5)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Mult(12)

  "Devious Joker" should "increase score by +100 Chips if played hand contains a Straight" in:
    val joker: Joker = JokerType.DeviousJoker
    val c1 = Card(Rank.Two, Suit.Clubs)
    val c2 = Card(Rank.Three, Suit.Hearts)
    val c3 = Card(Rank.Four, Suit.Clubs)
    val c4 = Card(Rank.Five, Suit.Hearts)
    val c5 = Card(Rank.Six, Suit.Hearts)
    val cards = Seq(c1, c2, c3, c4, c5)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Chips(100)

  "The Order" should "increase score by X3 Mult if played hand contains a Straight" in:
    val joker: Joker = JokerType.TheOrder
    val c1 = Card(Rank.Two, Suit.Clubs)
    val c2 = Card(Rank.Three, Suit.Hearts)
    val c3 = Card(Rank.Four, Suit.Clubs)
    val c4 = Card(Rank.Five, Suit.Hearts)
    val c5 = Card(Rank.Six, Suit.Hearts)
    val cards = Seq(c1, c2, c3, c4, c5)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker * Mult(3)

  "The Tribe" should "increase score by X2 Mult if played hand contains a Flush" in:
    val joker: Joker = JokerType.TheTribe
    val suit = Suit.Hearts
    val c1 = Card(Rank.Two, suit)
    val c2 = Card(Rank.Four, suit)
    val c3 = Card(Rank.Six, suit)
    val cards = Seq(c1, c1, c2, c2, c3)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker * Mult(2)

  "Onyx Agate" should "increase score by +7 Mult when a Club card is scored" in:
    val joker: Joker = JokerType.OnyxAgate
    val suit = Suit.Clubs
    val suit2 = Suit.Hearts
    val c1 = Card(Rank.Two, suit)
    val c2 = Card(Rank.Four, suit2)
    val cards: Seq[Card] = Seq(c1, c1, c2)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Mult(14)

  "Fibonacci" should "score +8 Mult for each Ace, 2, 3, 5, or 8 scored" in:
    val joker = JokerType.Fibonacci
    val cards = Seq(A of S, 8 of S, 5 of S, 3 of S, 2 of S)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Mult(40)

  "Scholar" should "score +20 Chips and +4 Mult for each Ace scored" in:
    val joker = JokerType.Scholar
    val cards = Seq(A of S, A of S, 3 of S, 3 of S, 3 of S)
    val scores = getScores(cards, joker)
    scores match
      case (scoreWithJoker, scoreWithoutJoker) =>
        scoreWithJoker shouldBe scoreWithoutJoker + Chips(40) + Mult(8)

  "Juggler" should "increase both plays and discards remaining by 1" in:
    val gameState : GameState = GameState.initial
    val round: RoundState = RoundState(gameState)
    val remainingPlays = round.remainingPlays
    val joker = JokerType.Juggler
    val modifications = joker.onRoundStart(round)
    joker.onRoundStart(round) shouldBe Seq(RoundStateModification.IncreaseHandsRemaining(1), RoundStateModification.IncreaseDiscardsRemaining(1))