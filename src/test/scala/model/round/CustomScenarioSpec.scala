package scalatro
package model.round

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{HandTypeLevels, Joker}
import model.commons.CardBuilder.*

import model.commons.JokerType.{CrazyJoker, DeviousJoker}

class CustomScenarioSpec extends AnyFlatSpec with Matchers:

  "The CustomScenario DSL" should "build a complete Round with jokers and levels" in:
    val customLevels = HandTypeLevels.initial
    val scenario = Cards(A of S, K of H, Q of C, J of D, 10 of S) withJokers (
      CrazyJoker,
      DeviousJoker
    ) onLevels customLevels
    val currentRound = scenario.buildRound

    currentRound.hand shouldBe Seq(A of S, K of H, Q of C, J of D, 10 of S)
    currentRound.gameState.jokers shouldBe Seq(CrazyJoker, DeviousJoker)
    currentRound.gameState.levels shouldBe customLevels

  it should "allow omitting jokers or levels" in:
    val simpleRound = Cards(2 of H, 3 of H, 4 of H).buildRound

    simpleRound.hand shouldBe Seq(2 of H, 3 of H, 4 of H)
    simpleRound.gameState.jokers shouldBe Seq.empty
    simpleRound.gameState.levels shouldBe HandTypeLevels.initial
