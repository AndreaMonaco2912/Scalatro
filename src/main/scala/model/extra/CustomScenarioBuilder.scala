package scalatro
package model.extra

import model.commons.*
import model.extra.GameStateBuilder
import model.round.RoundState
import model.commons.Score.Score
import model.game.{Blind, BlindProgression}

/** The entry point for the [[CustomScenario]] DSL.
  *
  * Example:
  * {{{
  * val customScenario = Cards(A of S, K of H) withJokers Seq(myJoker)
  *     onLevels(HC lv 3, TP lv 2, SF lv 5)
  * }}}
  */
object Cards:
  /** Initializes a new play context with the specified cards.
    *
    * @param cards
    *   a variable number of [[Card]]s representing the hand
    * @return
    *   a new [[CustomScenario]] to continue the configuration chain
    */
  def apply(cards: Card*): CustomScenario = CustomScenario(cards)

object HandLevelBuilder:
  /** Shorthand for [[HandType.HighCard]]. */
  val HC: HandType = HandType.HighCard

  /** Shorthand for [[HandType.Pair]]. */
  val P: HandType = HandType.Pair

  /** Shorthand for [[HandType.TwoPair]]. */
  val TP: HandType = HandType.TwoPair

  /** Shorthand for [[HandType.ThreeOfAKind]]. */
  val TOK: HandType = HandType.ThreeOfAKind

  /** Shorthand for [[HandType.Straight]]. */
  val ST: HandType = HandType.Straight

  /** Shorthand for [[HandType.Flush]]. */
  val FL: HandType = HandType.Flush

  /** Shorthand for [[HandType.FullHouse]]. */
  val FH: HandType = HandType.FullHouse

  /** Shorthand for [[HandType.FourOfAKind]]. */
  val FOK: HandType = HandType.FourOfAKind

  /** Shorthand for [[HandType.StraightFlush]]. */
  val SF: HandType = HandType.StraightFlush

  /** Shorthand for [[HandType.FiveOfAKind]]. */
  val FIOK: HandType = HandType.FiveOfAKind

  /** Shorthand for [[HandType.FlushHouse]]. */
  val FLH: HandType = HandType.FlushHouse

  /** Shorthand for [[HandType.FlushFive]]. */
  val FF: HandType = HandType.FlushFive

  extension (handType: HandType)
    /** Associates a [[HandType]] with a [[Level]].
      *
      * @param level
      *   the level to assign
      * @return
      *   a pair of ([[HandType]], [[Level]])
      */
    infix def lv(level: Level): (HandType, Level) = handType -> level

/** A builder for quickly setting up a [[RoundState]] context.
  *
  * @param cards
  *   the sequence of cards in the player's hand
  * @param jokers
  *   the sequence of jokers (defaults to empty)
  * @param levels
  *   the current levels of poker hand types (defaults to initial levels)
  * @param blindProgression
  *   the current blind progression (default is the first)
  * @param customTargetScore
  *   if present, overrides the blind target score (default is empty)
  */
case class CustomScenario(
    cards: Seq[Card],
    jokers: Seq[Joker] = Seq.empty,
    levels: HandTypeLevels = HandTypeLevels.initial,
    blindProgression: BlindProgression = BlindProgression.first,
    customTargetScore: Option[Score] = Option.empty
):

  /** Adds jokers to the current scenario.
    *
    * @param newJokers
    *   a variable number of [[Joker]]s to include
    * @return
    *   a new [[CustomScenario]] containing the specified jokers
    */
  infix def withJokers(newJokers: Joker*): CustomScenario =
    this.copy(jokers = newJokers)

  /** Sets the hand levels for the current scenario.
    *
    * @param entries
    *   a variable number of ([[HandType]], [[Level]])s pairs
    * @return
    *   a new [[CustomScenario]] containing the specified levels
    */
  infix def onLevels(entries: (HandType, Level)*): CustomScenario =
    this.copy(levels = levels ++ entries.toMap)

  /** Set blind to the current scenario.
    *
    * @param blind
    *   the blind in which the scenario will be created
    * @return
    *   a new [[CustomScenario]] containing the specified blind
    */
  infix def inBlind(blind: Blind): CustomScenario =
    this.copy(blindProgression = blindProgression.copy(blind = blind))

  /** Set blind target score to the current scenario.
    *
    * @param targetScore
    *   the target score to beat in the scenario
    * @return
    *   a new [[CustomScenario]] containing the specified target score
    */
  infix def withTarget(targetScore: Score): CustomScenario =
    this.copy(customTargetScore = Some(targetScore))

  /** Terminal method that compiles the chain into a full [[RoundState]] object.
    *
    * @return
    *   the fully constructed [[RoundState]]
    */
  def buildRound: RoundState =
    /** Take the effective blind progression. If is specified a custom target
      * score for the blind, this blind progression will give it when asked for
      * a target score (doesn't compute the score as usual)
      */
    val effectiveBlindProgression =
      customTargetScore.fold(blindProgression)(blindProgression.withTargetScore)

    val customState = GameStateBuilder.configure {
      import GameStateBuilder.DSL.*
      Jokers := this.jokers
      Levels := this.levels
      BlindInGame := effectiveBlindProgression
    }

    RoundBuilder.configure {
      import RoundBuilder.DSL.*
      HandInRound := this.cards
      DeckInRound := Deck(Deck().cards diff this.cards)
      GameStateInRound := customState
    }
