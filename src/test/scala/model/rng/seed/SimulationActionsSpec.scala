package scalatro
package model.rng.seed

import model.commons.*
import model.commons.JokerType.*
import model.commons.Planet.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.game.GameState
import model.rng.ScalatroRng
import model.rng.seed.SimulationActions.*
import model.rng.seed.SimulationTypes.SimRound

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[SimulationActions]] */
class SimulationActionsSpec extends AnyFlatSpec, Matchers:

  private given ScalatroRng = ScalatroRng.default

  private val aceOfSpades = Card(Ace, Spades)
  private val kingOfHearts = Card(King, Hearts)

  private val simRound = SimRound(
    hand = Seq(aceOfSpades, kingOfHearts),
    cardPack = Pack(Seq(aceOfSpades)),
    jokerPack = Pack(Seq(CleverJoker)),
    planetPack = Pack(Seq(Earth))
  )

  "checkRound" should "return true when every constraint is satisfied" in:
    val constraints = Seq(
      InitialHandWithCards(Seq(aceOfSpades), 1),
      CardPackContains(aceOfSpades, 1),
      JokerPackContains(CleverJoker, 1),
      PlanetPackContains(Earth, 1)
    )

    checkRound(constraints, simRound) shouldBe true

  it should "return false when at least one constraint is not satisfied" in:
    val constraints = Seq(
      InitialHandWithCards(Seq(aceOfSpades), 1),
      CardPackContains(Card(Two, Clubs), 1)
    )

    checkRound(constraints, simRound) shouldBe false

  "wantedPackItems" should "collect only requested cards from card pack constraints" in:
    val constraints = Seq(
      CardPackContains(aceOfSpades, 1),
      InitialHandWithCards(Seq(kingOfHearts), 1)
    )

    val wanted = wantedPackItems(constraints)
    wanted.cards shouldBe Seq(aceOfSpades)
    wanted.jokers shouldBe empty
    wanted.planets shouldBe empty

  it should "collect only requested jokers from joker pack constraints" in:
    val constraints = Seq(
      JokerPackContains(CleverJoker, 1),
      InitialHandWithCards(Seq(kingOfHearts), 1)
    )

    val wanted = wantedPackItems(constraints)
    wanted.cards shouldBe empty
    wanted.jokers shouldBe Seq(CleverJoker)
    wanted.planets shouldBe empty

  it should "collect only requested planets from planet pack constraints" in:
    val constraints = Seq(
      PlanetPackContains(Earth, 1),
      InitialHandWithCards(Seq(kingOfHearts), 1)
    )

    val wanted = wantedPackItems(constraints)
    wanted.cards shouldBe empty
    wanted.jokers shouldBe empty
    wanted.planets shouldBe Seq(Planet.Earth)

  "shuffleDeckAndDraw" should "draw exactly the configured hand size" in:
    val hand = shuffleDeckAndDraw.runA(GameState.initial).value

    hand.size shouldBe GameState.initial.handInformation.handSize

  "generateShop" should "generate three cards, planets, and jokers" in:
    val shop = generateShop.runA(GameState.initial).value

    shop.cardPack.items.size shouldBe 3
    shop.planetPack.items.size shouldBe 3
    shop.jokerPack.items.size shouldBe 3

  "pickFromPacks" should "add wanted cards to the deck" in:
    val constraints = Seq(CardPackContains(aceOfSpades, 1))

    val updated = pickFromPacks(constraints).runS(GameState.initial).value

    updated.deck.cards should contain(aceOfSpades)

  it should "add wanted jokers to the game state" in:
    val constraints = Seq(JokerPackContains(CleverJoker, 1))

    val updated = pickFromPacks(constraints).runS(GameState.initial).value

    updated.jokers should contain(CleverJoker)

  it should "use wanted planets" in:
    val constraints = Seq(PlanetPackContains(Earth, 1))

    val updated = pickFromPacks(constraints).runS(GameState.initial).value

    updated.levels.getLevel(Earth.handType) shouldBe 2

  "advanceBlind" should "advance the blind progression by one round" in:
    val initial = GameState.initial

    val updated = advanceBlind.runS(initial).value

    updated.blindProgression.roundNum shouldBe initial.blindProgression.roundNum + 1

  "simulateRound" should "produce evidence for one simulated round" in:
    val result = simulateRound.runA(GameState.initial).value

    result.hand.size shouldBe GameState.initial.handInformation.handSize
    result.cardPack.items.size shouldBe 3
    result.planetPack.items.size shouldBe 3
    result.jokerPack.items.size shouldBe 3

  "checkAllRounds" should "return true for no remaining constraints" in:
    val result = checkAllRounds(Seq.empty).runA(GameState.initial).value

    result shouldBe true

  it should "return false when current round constraints fail" in:
    val impossibleConstraint =
      InitialHandWithCards(Seq(aceOfSpades, aceOfSpades), 1)

    val result =
      checkAllRounds(Seq(impossibleConstraint)).runA(GameState.initial).value

    result shouldBe false

  "satisfiesConstraints" should "return true for empty constraints" in:
    satisfiesConstraints(Seq.empty) shouldBe true

  it should "return false for impossible constraints" in:
    val impossibleConstraint =
      InitialHandWithCards(Seq(aceOfSpades, aceOfSpades), 1)

    satisfiesConstraints(Seq(impossibleConstraint)) shouldBe false
