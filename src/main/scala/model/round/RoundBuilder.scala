package scalatro
package model.round

import model.commons.Score.Score
import model.commons.{Deck, Score}
import model.game.GameState

/** A DSL for building a [[Round]] with a readable and declarative syntax.
  *
  * Example:
  * {{{
  * val currentRound = RoundBuilder.configure {
  * ScoreInRound     := Score.zero
  * HandInRound      := Seq.empty
  * DeckInRound      := Deck()
  * GameStateInRound := GameState.initial
  * }
  * }}}
  */
class RoundBuilder:
  private var score: Score = Score.zero
  private var hand: Hand = Seq.empty
  private var deck: Deck = Deck()
  private var gameState: GameState = GameState.initial

  /** Builds the final immutable [[Round]] using the configured parameters.
    *
    * @return
    *   the constructed [[Round]]
    */
  def build: Round = Round(score, hand, deck, gameState)

object RoundBuilder:

  /** Creates a builder, makes it available in the implicit scope of the block,
    * runs the block to apply configurations, and builds the [[Round]].
    *
    * @param configuration
    *   the context function containing DSL assignments
    * @return
    *   the fully constructed [[Round]]
    */
  def configure(configuration: RoundBuilder ?=> Unit): Round =
    val builder = RoundBuilder()
    configuration(using builder)
    builder.build

  object DSL:

    object ScoreInRound:
      /** @param value the [[Score]] to set */
      infix def :=(value: Score)(using b: RoundBuilder): Unit =
        b.score = value

    object HandInRound:
      /** @param value the sequence of cards representing the [[Hand]] */
      infix def :=(value: Hand)(using b: RoundBuilder): Unit =
        b.hand = value

    object DeckInRound:
      /** @param value the [[Deck]] to use for drawing cards */
      infix def :=(value: Deck)(using b: RoundBuilder): Unit =
        b.deck = value

    object GameStateInRound:
      /** @param value
        *   the parent [[GameState]] tracking overarching game rules and levels
        */
      infix def :=(value: GameState)(using b: RoundBuilder): Unit =
        b.gameState = value
