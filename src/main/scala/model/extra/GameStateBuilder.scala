package scalatro
package model.extra

import model.commons.{Deck, HandTypeLevels, Joker}
import model.game.{BlindProgression, GameState, HandInformation}
import model.rng.SelectionPolicies

/** A DSL for building a [[GameState]] with a readable syntax. Example:
  * {{{
  * val state: GameState = GameStateBuilder.configure {
  * HandSize    := 8
  * Hands       := 4
  * Discards    := 3
  * DeckInGame  := Deck()
  * BlindInGame := Blind(initialRound, initialScore)
  * Jokers      := Seq.empty
  * Levels      := HandTypeLevels.initial
  * }
  * }}}
  */
class GameStateBuilder:
  private var handSize: Int = GameState.initialHandSize
  private var remainingHands: Int = GameState.initialHandNum
  private var remainingDiscards: Int = GameState.initialDiscardNum
  private var deck: Deck = Deck()
  private var blindProgression: BlindProgression = BlindProgression.first
  private var jokers: Seq[Joker] = Seq.empty
  private var levels: HandTypeLevels = HandTypeLevels.initial

  /** Builds the final [[GameState]] using the configured parameters. * @return
    * the constructed [[GameState]]
    */
  def build: GameState = GameState(
    handInformation = HandInformation(
      this.handSize,
      this.remainingHands,
      this.remainingDiscards
    ),
    deck = this.deck,
    blindProgression = this.blindProgression,
    jokers = this.jokers,
    levels = this.levels,
    selectionPolicies = SelectionPolicies.default
  )

object GameStateBuilder:

  /** Creates a builder, makes it available in the implicit scope of the block,
    * runs the block to apply configurations, and builds the [[GameState]].
    * @param configuration
    *   the context function containing DSL assignments
    * @return
    *   the fully constructed [[GameState]]
    */
  def configure(configuration: GameStateBuilder ?=> Unit): GameState =
    val builder = GameStateBuilder()
    configuration(using builder)
    builder.build

  object DSL:
    object HandSize:
      /** @param value the number of cards the player can hold */
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.handSize = value

    object Hands:
      /** @param value the number of playable hands */
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.remainingHands = value

    object Discards:
      /** @param value the number of allowed discards */
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.remainingDiscards = value

    object DeckInGame:
      /** @param deck the [[Deck]] of cards to be used */
      infix def :=(deck: Deck)(using b: GameStateBuilder): Unit =
        b.deck = deck

    object BlindInGame:
      /** @param blindProgression
        *   the target [[BlindProgression]] for the round
        */
      infix def :=(blindProgression: BlindProgression)(using
          b: GameStateBuilder
      ): Unit =
        b.blindProgression = blindProgression

    object Jokers:
      /** @param jokers the sequence of [[Joker]]s currently held */
      infix def :=(jokers: Seq[Joker])(using b: GameStateBuilder): Unit =
        b.jokers = jokers

    object Levels:
      /** @param levels
        *   the [[HandTypeLevels]] tracking the level of each hand type
        */
      infix def :=(levels: HandTypeLevels)(using b: GameStateBuilder): Unit =
        b.levels = levels
