package scalatro
package model.round

import model.commons.{Card, HandTypeLevels, Joker}
import model.game.GameStateBuilder

/** The entry point for the custom scenario DSL.
  *
  * Example:
  * {{{
  * val currentRound = Cards(A | S, K | H) withJokers Seq(myJoker) onLevels HandTypeLevels.initial .buildRound
  * }}}
  */
object Cards:
  /** Initializes a new play context with the specified starting cards.
    *
    * @param cards
    *   a variable number of [[Card]]s representing the hand
    * @return
    *   a new, immutable [[CustomScenario]] to continue the configuration chain
    */
  def apply(cards: Card*): CustomScenario = CustomScenario(cards)

/** An immutable builder for quickly setting up a [[Round]] context.
  *
  * @param cards
  *   the sequence of cards in the player's hand
  * @param jokers
  *   the sequence of active jokers (defaults to empty)
  * @param levels
  *   the current levels of poker hand types (defaults to initial levels)
  */
case class CustomScenario(
    cards: Seq[Card],
    jokers: Seq[Joker] = Seq.empty,
    levels: HandTypeLevels = HandTypeLevels.initial
):

  /** Adds jokers to the current context.
    *
    * @param newJokers
    *   a variable number of [[Card]]s to include
    * @return
    *   a new [[CustomScenario]] containing the specified jokers
    */
  infix def withJokers(newJokers: Joker*): CustomScenario =
    this.copy(jokers = newJokers)

  /** Sets the hand levels for the current context.
    *
    * @param newLevels
    *   the [[HandTypeLevels]] tracking the level of each hand type
    * @return
    *   a new [[CustomScenario]] containing the specified levels
    */
  infix def onLevels(newLevels: HandTypeLevels): CustomScenario =
    this.copy(levels = newLevels)

  /** Terminal method that compiles the fluent chain into a full [[Round]]
    * object.
    *
    * @return
    *   the fully constructed, immutable [[Round]]
    */
  def buildRound: Round =
    val customState = GameStateBuilder.configure {
      import model.game.GameStateBuilder.DSL.*
      Jokers := this.jokers
      Levels := this.levels
    }

    RoundBuilder.configure {
      import model.round.RoundBuilder.DSL.*
      HandInRound := this.cards
      GameStateInRound := customState
    }
