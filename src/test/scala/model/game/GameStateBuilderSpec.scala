package scalatro
package model.game

import model.commons.{Deck, HandTypeLevels, Joker}
import scalatro.model.extra.GameStateBuilder.DSL.*
import model.game.{Blind, GameState}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalatro.model.extra.GameStateBuilder

class GameStateBuilderSpec extends AnyFlatSpec with Matchers:

  "GameStateBuilder" should "correctly build a state with all custom values" in:
    val customDeck = Deck()
    val customBlind = Blind.first
    val customJokers = Seq.empty[Joker]
    val customLevels = HandTypeLevels.initial

    val state = GameStateBuilder.configure {
      HandSize := 10
      Hands := 5
      Discards := 2
      DeckInGame := customDeck
      BlindInGame := customBlind
      Jokers := customJokers
      Levels := customLevels
    }

    state.handInformation.handSize shouldBe 10
    state.handInformation.handNum shouldBe 5
    state.handInformation.discardNum shouldBe 2
    state.deck shouldBe customDeck
    state.blind shouldBe customBlind
    state.jokers shouldBe customJokers
    state.levels shouldBe customLevels

  it should "retain default values for omitted configurations" in {
    val state = GameStateBuilder.configure {
      HandSize := 9
      Discards := 1
    }

    state.handInformation.handSize shouldBe 9
    state.handInformation.discardNum shouldBe 1

    state.handInformation.handNum shouldBe GameState.initialHandNum
    state.jokers shouldBe Seq.empty
    state.levels shouldBe HandTypeLevels.initial
  }
