package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{Deck, HandType, HandTypeLevels, JokerType}

import model.game.GameStateBuilder.DSL.{Discards, Hands, HandSize}

import scala.util.Random

class GameStateTest extends AnyFlatSpec, Matchers:

//  val start: GameState = GameState.initial
  val start: GameState = GameStateBuilder.configure{
    HandSize := GameState.initial.handInformation.handSize
    Hands := GameState.initial.handInformation.handNum
    Discards := GameState.initial.handInformation.discardNum
  }
  given Random = new Random(0L)

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