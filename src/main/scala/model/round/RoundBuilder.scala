package scalatro
package model.round

import model.commons.Score.Score
import model.commons.{Deck, Score}
import model.game.GameState

class RoundBuilder:
  private var score: Score = Score.zero
  private var hand: Hand = Seq.empty
  private var deck: Deck = Deck()
  private var gameState: GameState = GameState.initial

  def build: Round = Round(score, hand, deck, gameState)

object RoundBuilder:
  
  def configure(configuration: RoundBuilder ?=> Unit): Round =
    val builder = RoundBuilder()
    configuration(using builder)
    builder.build

  object DSL:
    object ScoreInRound:
      infix def :=(value: Score)(using b: RoundBuilder): Unit =
        b.score = value

    object HandInRound:
      infix def :=(value: Hand)(using b: RoundBuilder): Unit =
        b.hand = value

    object DeckInRound:
      infix def :=(value: Deck)(using b: RoundBuilder): Unit =
        b.deck = value

    object GameStateInRound:
      infix def :=(value: GameState)(using b: RoundBuilder): Unit =
        b.gameState = value