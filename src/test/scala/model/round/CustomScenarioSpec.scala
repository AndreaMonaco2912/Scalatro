package scalatro
package model.round

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{HandTypeLevels, Joker}
import model.commons.CardBuilder.*
import model.round.CustomScenario

import model.commons.JokerType.{CrazyJoker, DeviousJoker}

class CustomScenarioSpec extends AnyFlatSpec with Matchers:

  "The CustomScenario DSL" should "build a complete Round with jokers and levels" in:
    val customLevels = HandTypeLevels.initial
    val scenario = Cards(A | S, K | H, Q | C, J | D, 10 | S) withJokers (
      CrazyJoker,
      DeviousJoker
    ) onLevels customLevels
    val currentRound = scenario.buildRound

    currentRound.hand shouldBe Seq(A | S, K | H, Q | C, J | D, 10 | S)
    currentRound.gameState.jokers shouldBe Seq(CrazyJoker, DeviousJoker)
    currentRound.gameState.levels shouldBe customLevels

  it should "allow omitting jokers or levels" in:
    val simpleRound = Cards(2 | H, 3 | H, 4 | H).buildRound

    simpleRound.hand shouldBe Seq(2 | H, 3 | H, 4 | H)
    simpleRound.gameState.jokers shouldBe Seq.empty
    simpleRound.gameState.levels shouldBe HandTypeLevels.initial

