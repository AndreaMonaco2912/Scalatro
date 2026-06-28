package scalatro
package model.game

import model.commons.{Deck, HandTypeLevels}

import scala.annotation.targetName

/** A DSL for building a [[GameState]] with a readable syntax.
 * Example:
 * {{{
 * GameState state = GameStateBuilder.configure{
 *  HandSize := 8
 *  Hands := 4
 *  Discards := 3
 * }
 * }}}
 */
class GameStateBuilder:
  var handSize: Int = 8
  var remainingHands: Int = 4
  var remainingDiscards: Int = 3

  def build: GameState = GameState(
    handInformation = HandInformation(
      this.handSize,
      this.remainingHands,
      this.remainingDiscards
    ),
    deck = Deck(),
    blind = Blind.first,
    jokers = Seq.empty,
    levels = HandTypeLevels.initial
  )

object GameStateBuilder:
  /**
   * It creates a builder, makes it available in the
   * implicit scope of the block, runs the block, and builds the state.
   * **/
  def configure(configuration: GameStateBuilder ?=> Unit): GameState =
    val builder = GameStateBuilder()
    // By calling `configuration(using builder)`, we inject the builder
    // into the context of everything inside the curly braces.
    configuration(using builder)
    builder.build

  object DSL:

    object HandSize:
      @targetName("assign")
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.handSize = value

    object Hands:
      @targetName("assign")
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.remainingHands = value

    object Discards:
      @targetName("assign")
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.remainingDiscards = value
