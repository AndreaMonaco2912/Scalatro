package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.commons.Deck

import scala.util.Random

class GameStateTest extends AnyFlatSpec, Matchers:

  val start: GameState = GameState.initial

  "initial" should "start at the first blind" in:
    start.blind shouldBe Blind.first

  it should "start with a full deck" in:
    start.deck.cards should contain theSameElementsAs Deck().cards

  "advanceBlind" should "replace the blind with its next" in:
    val result = GameState.advanceBlind.runS(start).value
    result.blind shouldBe start.blind.next

  it should "preserve the multiset of cards" in:
    given Random = new Random(0L)
    val result = GameState.shuffleDeck.runS(start).value
    result.deck.cards should contain theSameElementsAs start.deck.cards