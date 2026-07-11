package scalatro
package model.rng.seed

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.Types.Seed
import model.rng.seed.ScalatroSeedSearch.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[ScalatroSeedSearch]] */
class ScalatroSeedSearchSpec extends AnyFlatSpec, Matchers:

  "findSeed" should "return the fallback seed when maxAttempts is zero" in:
    val seed = findSeed(
      constraints = Seq.empty,
      maxAttempts = 0
    )

    seed shouldBe Seed(0L)

  it should "return a non-fallback seed when unconstrained search has one attempt" in:
    val seed = findSeed(
      constraints = Seq.empty,
      maxAttempts = 1
    )

    seed should not be Seed(0L)

  it should "return the fallback seed when no attempted seed satisfies the constraints" in:
    val impossibleConstraint =
      InitialHandWithCards(Seq(Card(Ace, Spades), Card(Ace, Spades)), 1)

    val seed = findSeed(
      constraints = Seq(impossibleConstraint),
      maxAttempts = 3
    )

    seed shouldBe Seed(0L)

  "main" should "run a search without constraints" in:
    noException should be thrownBy main(Array.empty)

  "parseArgs" should "parse multiple constraints" in:
    parseArgs(
      Array(
        "-c",
        "initial-hand-cards:1:Ace-Spades,King-Hearts",
        "-c",
        "planet-pack-contains:2:Earth"
      )
    ) shouldBe Right(
      Seq(
        InitialHandWithCards(Seq(Card(Ace, Spades), Card(King, Hearts)), 1),
        PlanetPackContains(Planet.Earth, 2)
      )
    )

  it should "accept no arguments" in:
    parseArgs(Array.empty) shouldBe Right(Seq.empty)

  it should "return a Left for a missing constraint" in:
    parseArgs(Array("-c")) should matchPattern:
      case Left(_) =>

  it should "return a Left for unknown arguments" in:
    parseArgs(Array("--unknown")) should matchPattern:
      case Left(_) =>

  it should "return a Left for an invalid constraint" in:
    parseArgs(Array("-c", "invalid")) should matchPattern:
      case Left(_) =>

  "parseConstraint" should "parse every supported constraint" in:
    parseConstraint(
      "initial-hand-cards:1:Ace-Spades,King-Hearts"
    ) shouldBe Right(
      InitialHandWithCards(
        Seq(Card(Ace, Spades), Card(King, Hearts)),
        1
      )
    )

    parseConstraint("initial-hand-type:2:FullHouse") shouldBe Right(
      InitialHandWithHandType(HandType.FullHouse, 2)
    )

    parseConstraint("card-pack-contains:3:King-Clubs") shouldBe Right(
      CardPackContains(Card(King, Clubs), 3)
    )

    parseConstraint("joker-pack-contains:4:CleverJoker") shouldBe Right(
      JokerPackContains(JokerType.CleverJoker, 4)
    )

    parseConstraint("planet-pack-contains:5:Earth") shouldBe Right(
      PlanetPackContains(Planet.Earth, 5)
    )

  it should "return a Left for a malformed constraint" in:
    parseConstraint("initial-hand-type:1") should matchPattern:
      case Left(_) =>

  "parseRound" should "parse positive rounds" in:
    parseRound("1") shouldBe Right(1)

  it should "return a Left for non-positive and non-numeric rounds" in:
    parseRound("0") should matchPattern:
      case Left(_) =>
    parseRound("one") should matchPattern:
      case Left(_) =>

  "parseCards" should "parse comma-separated cards" in:
    parseCards("Ace-Spades,King-Hearts") shouldBe
      Right(Seq(Card(Ace, Spades), Card(King, Hearts)))

  it should "return a Left when a card is invalid" in:
    parseCards("Ace-Spades,invalid") should matchPattern:
      case Left(_) =>

  "parseCard" should "parse a rank and suit" in:
    parseCard("Ace-Spades") shouldBe Right(Card(Ace, Spades))

  it should "return a Left for a malformed card" in:
    parseCard("Ace") should matchPattern:
      case Left(_) =>

  "parseRank" should "parse known ranks" in:
    parseRank("Ace") shouldBe Right(Ace)

  it should "return a Left for an unknown rank" in:
    parseRank("One") should matchPattern:
      case Left(_) =>

  "parseSuit" should "parse known suits" in:
    parseSuit("Spades") shouldBe Right(Spades)

  it should "return a Left for an unknown suit" in:
    parseSuit("Stars") should matchPattern:
      case Left(_) =>

  "parseHandType" should "parse known hand types" in:
    parseHandType("FullHouse") shouldBe Right(HandType.FullHouse)

  it should "return a Left for an unknown hand type" in:
    parseHandType("RoyalFlush") should matchPattern:
      case Left(_) =>

  "parseJoker" should "parse known jokers" in:
    parseJoker("CleverJoker") shouldBe Right(JokerType.CleverJoker)

  it should "return a Left for an unknown joker" in:
    parseJoker("UnknownJoker") should matchPattern:
      case Left(_) =>

  "parsePlanet" should "parse known planets" in:
    parsePlanet("Earth") shouldBe Right(Planet.Earth)

  it should "return a Left for an unknown planet" in:
    parsePlanet("UnknownPlanet") should matchPattern:
      case Left(_) =>
