package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{
  Deck,
  HandType,
  HandTypeLevels,
  JokerType,
  Level,
  Planet,
  getLevel
}
import model.commons.CardBuilder.*
import model.rng.ScalatroRng

class GameStateSpec extends AnyFlatSpec, Matchers:

  val start: GameState = GameState.initial
  given ScalatroRng = ScalatroRng.default

  "initial" should "start at the first blind" in:
    start.blind shouldBe Blind.first

  it should "start with a full deck" in:
    start.deck.cards should contain theSameElementsAs Deck().cards

  "advanceBlind" should "replace the blind with its next" in:
    val result = start.advanceBlind
    result.blind shouldBe start.blind.next

  "shuffle" should "preserve all the cards" in:
    val result = start.shuffleDeck
    result.deck.cards should contain theSameElementsAs start.deck.cards

  "shuffle" should "change the card order" in:
    val result = start.shuffleDeck
    result.deck should not equal start.deck

  "shopInformation" should "carry over the deck, levels, and jokers" in:
    val jokers = Seq(JokerType.CraftyJoker)
    val levels = HandTypeLevels.initial.updated(HandType.Flush, 2)
    val state = start.copy(jokers = jokers, levels = levels)
    val info = state.shopInformation
    info.deck shouldBe state.deck
    info.levels shouldBe levels
    info.jokers shouldBe jokers

  "addCard" should "add a card to the Deck" in:
    val aceOfSpade = A of S
    val deckOnTwoAceOfSpade = Deck().add(aceOfSpade)
    start.addCard(aceOfSpade).deck shouldBe deckOnTwoAceOfSpade
    val deckOnThreeAceOfSpade = deckOnTwoAceOfSpade.add(aceOfSpade)
    start
      .addCard(aceOfSpade)
      .addCard(aceOfSpade)
      .deck shouldBe deckOnThreeAceOfSpade

  "addJocker" should "add the new Jocker to GameState" in:
    val craftyJocker = Seq(JokerType.CraftyJoker)
    val stateWithCrafty = start.addJoker(JokerType.CraftyJoker)
    stateWithCrafty.jokers shouldBe craftyJocker
    val jockerList = craftyJocker :+ JokerType.CrazyJoker
    val stateWithJokerList = stateWithCrafty.addJoker(JokerType.CrazyJoker)
    stateWithJokerList.jokers shouldBe jockerList

  "usePlanet" should "update corresponding hand level" in:
    val result = start.usePlanet(Planet.Mars)
    result.levels.getLevel(HandType.FourOfAKind) shouldBe Level.initial + 1

  "scoreConfig" should "carry over the current jokers and levels" in:
    val jokers = Seq(JokerType.CraftyJoker)
    val levels = HandTypeLevels.initial.updated(HandType.Flush, 2)
    val state = start.copy(jokers = jokers, levels = levels)
    state.scoreConfig.jokers shouldBe jokers
    state.scoreConfig.levels shouldBe levels
