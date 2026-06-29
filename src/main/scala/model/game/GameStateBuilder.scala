package scalatro
package model.game

import model.commons.{Deck, HandTypeLevels, Joker}

import scala.annotation.targetName

/** A DSL for building a [[GameState]] with a readable syntax. Example:
  * {{{
  * GameState state = GameStateBuilder.configure{
  *  HandSize := 8
  *     Hands := 4
  *     Discards := 3
  *     DeckInGame := Deck()
  *     BlindInGame := Blind(initialRound, initialScore)
  *     Jokers := Seq.empty
  *     Levels := HandType.values.map(ht => ht -> 1).toMap
  * }
  * }}}
  */
class GameStateBuilder:
  var handSize: Int = 8
  var remainingHands: Int = 4
  var remainingDiscards: Int = 3
  var deck: Deck = Deck()
  var blind: Blind = Blind.first
  var jokers: Seq[Joker] = Seq.empty
  var levels: HandTypeLevels = HandTypeLevels.initial

  def build: GameState = GameState(
    handInformation = HandInformation(
      this.handSize,
      this.remainingHands,
      this.remainingDiscards
    ),
    deck = this.deck,
    blind = this.blind,
    jokers = this.jokers,
    levels = this.levels
  )

object GameStateBuilder:
  /** It creates a builder, makes it available in the implicit scope of the
    * block, runs the block, and builds the state. *
    */
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

    object DeckInGame:
      @targetName("assign")
      infix def :=(deck: Deck)(using b: GameStateBuilder): Unit =
        b.deck = deck

    object BlindInGame:
      @targetName("assign")
      infix def :=(blind: Blind)(using b: GameStateBuilder): Unit =
        b.blind = blind

    object Jokers:
      @targetName("assign")
      infix def :=(jokers: Seq[Joker])(using b: GameStateBuilder): Unit =
        b.jokers = jokers

    object Levels:
      @targetName("assign")
      infix def :=(levels: HandTypeLevels)(using b: GameStateBuilder): Unit =
        b.levels = levels
