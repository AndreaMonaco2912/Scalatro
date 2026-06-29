package scalatro
package model.round

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.{Deck, Score}
import model.game.GameState
import model.commons.CardBuilder.*
import model.round.RoundBuilder.DSL.*

class RoundBuilderSpec extends AnyFlatSpec with Matchers:

  "RoundBuilder" should "correctly build a round with all custom values" in {
    val customScore = Score(42)
    val customHand  = Seq(A | S, K | H, 10 | D, 5 | C)
    val customDeck  = Deck(Seq(2 | S, A | H, J | D, 6 | C))
    val customState = GameState.initial

    val currentRound = RoundBuilder.configure {
      ScoreInRound     := customScore
      HandInRound      := customHand
      DeckInRound      := customDeck
      GameStateInRound := customState
    }

    currentRound.score shouldBe customScore
    currentRound.hand shouldBe customHand
    currentRound.deck shouldBe customDeck
    currentRound.gameState shouldBe customState
  }

  it should "retain default values for omitted configurations" in {
    val customHand = Seq(2 | H, 3 | H)

    val currentRound = RoundBuilder.configure {
      HandInRound := customHand
    }

    currentRound.hand shouldBe customHand
    currentRound.score shouldBe Score.zero
    currentRound.gameState shouldBe GameState.initial
  }