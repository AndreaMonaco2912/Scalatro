package scalatro
package model.rng.seed

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.seed.SimulationTypes.SimRound

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SeedConstraintSpec extends AnyFlatSpec, Matchers:

  private val aceOfSpades = Card(Ace, Spades)
  private val aceOfHearts = Card(Ace, Hearts)
  private val kingOfClubs = Card(King, Clubs)

  private val round = SimRound(
    hand = Seq(aceOfSpades, aceOfHearts, kingOfClubs),
    cardPack = Pack(Seq(aceOfSpades)),
    jokerPack = Pack(Seq(JokerType.CleverJoker)),
    planetPack = Pack(Seq(Planet.Earth))
  )

  "InitialHandWithCards" should "be satisfied when every requested card is in the hand" in:
    val constraint = InitialHandWithCards(Seq(aceOfSpades, aceOfHearts), 1)

    constraint.isSatisfiedBy(round) shouldBe true

  it should "not be satisfied when a requested card is missing from the hand" in:
    val constraint = InitialHandWithCards(Seq(Card(Two, Diamonds)), 1)

    constraint.isSatisfiedBy(round) shouldBe false

  "InitialHandWithHandType" should "be satisfied when the hand has the requested type" in:
    val pairRound = round.copy(
      hand = Seq(
        Card(Ace, Spades),
        Card(Ace, Hearts),
        Card(King, Clubs),
        Card(Queen, Diamonds),
        Card(Two, Spades)
      )
    )

    val constraint = InitialHandWithHandType(HandType.Pair, 1)

    constraint.isSatisfiedBy(pairRound) shouldBe true

  it should "not be satisfied when the hand has another type" in:
    val constraint = InitialHandWithHandType(HandType.Flush, 1)

    constraint.isSatisfiedBy(round) shouldBe false

  "CardPackContains" should "be satisfied when the card pack contains the requested card" in:
    val constraint = CardPackContains(aceOfSpades, 1)

    constraint.isSatisfiedBy(round) shouldBe true

  it should "not be satisfied when the card pack does not contain the requested card" in:
    val constraint = CardPackContains(Card(Two, Diamonds), 1)

    constraint.isSatisfiedBy(round) shouldBe false

  "JokerPackContains" should "be satisfied when the joker pack contains the requested joker" in:
    val constraint = JokerPackContains(JokerType.CleverJoker, 1)

    constraint.isSatisfiedBy(round) shouldBe true

  it should "not be satisfied when the joker pack does not contain the requested joker" in:
    val constraint = JokerPackContains(JokerType.CraftyJoker, 1)

    constraint.isSatisfiedBy(round) shouldBe false

  "PlanetPackContains" should "be satisfied when the planet pack contains the requested planet" in:
    val constraint = PlanetPackContains(Planet.Earth, 1)

    constraint.isSatisfiedBy(round) shouldBe true

  it should "not be satisfied when the planet pack does not contain the requested planet" in:
    val constraint = PlanetPackContains(Planet.Mars, 1)

    constraint.isSatisfiedBy(round) shouldBe false
