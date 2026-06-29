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
  private var handSize: Int = GameState.initialHandSize
  private var remainingHands: Int = GameState.initialHandNum
  private var remainingDiscards: Int = GameState.initialDiscardNum
  private var deck: Deck = Deck()
  private var blind: Blind = Blind.first
  private var jokers: Seq[Joker] = Seq.empty
  private var levels: HandTypeLevels = HandTypeLevels.initial

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
    * block, runs the block, and builds the GameState.
    */
  def configure(configuration: GameStateBuilder ?=> Unit): GameState =
    val builder = GameStateBuilder()
    configuration(using builder)
    builder.build

  object DSL:

    object HandSize:
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.handSize = value

    object Hands:
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.remainingHands = value

    object Discards:
      infix def :=(value: Int)(using b: GameStateBuilder): Unit =
        b.remainingDiscards = value

    object DeckInGame:
      infix def :=(deck: Deck)(using b: GameStateBuilder): Unit =
        b.deck = deck

    object BlindInGame:
      infix def :=(blind: Blind)(using b: GameStateBuilder): Unit =
        b.blind = blind

    object Jokers:
      infix def :=(jokers: Seq[Joker])(using b: GameStateBuilder): Unit =
        b.jokers = jokers

    object Levels:
      infix def :=(levels: HandTypeLevels)(using b: GameStateBuilder): Unit =
        b.levels = levels
