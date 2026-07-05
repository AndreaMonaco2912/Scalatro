package scalatro
package model.round

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{Deck, HandType, HandTypeLevels, Joker}

import scalatro.model.extra.CardBuilder.*
import model.commons.JokerType.{CrazyJoker, DeviousJoker}

import scalatro.model.extra.Cards
import scalatro.model.extra.HandLevelBuilder.*

class CustomScenarioSpec extends AnyFlatSpec with Matchers:

  "The CustomScenario DSL" should "build a complete Round with jokers and levels" in:
    val scenario = Cards(A of S, K of H, Q of C, J of D, 10 of S) withJokers (
      CrazyJoker,
      DeviousJoker
    ) onLevels (HC lv 7, TP lv 7, SF lv 7)
    val currentRound = scenario.buildRound

    currentRound.hand shouldBe Seq(A of S, K of H, Q of C, J of D, 10 of S)
    currentRound.gameState.jokers shouldBe Seq(CrazyJoker, DeviousJoker)
    currentRound.gameState.levels shouldBe
      (HandTypeLevels.initial ++ HandType.values
        .filter(ht => ht == HC | ht == TP | ht == SF)
        .map(ht => ht -> 7)
        .toMap)

  it should "allow omitting jokers or levels" in:
    val simpleRound = Cards(2 of H, 3 of H, 4 of H).buildRound

    simpleRound.hand shouldBe Seq(2 of H, 3 of H, 4 of H)
    simpleRound.gameState.jokers shouldBe Seq.empty
    simpleRound.gameState.levels shouldBe HandTypeLevels.initial

  it should "have a coherent deck with the given hand" in:
    val hand = Cards(A of S, K of H, Q of C, J of D, 10 of S)
    val currentRound = hand.buildRound
    
    currentRound.deck shouldBe (Deck().cards diff hand.cards)