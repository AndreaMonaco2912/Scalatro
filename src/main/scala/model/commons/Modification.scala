package scalatro
package model.commons

import model.game.{GameState, GameStateModification}
import model.round.{RoundState, RoundStateModification}

/** Trait which represents a modification of a value of type [[T]].
  * @tparam T
  *   the type of the modified value
  */
trait Modification[T]:
  /** Invoke the modification.
    * @param value
    *   the initial value
    * @return
    *   the modified value
    */
  def apply(value: T): T

object Modification:
  /** A method for applying a modification in case a provided condition is true.
    * @param condition
    *   the condition
    * @param modification
    *   the modification to apply
    * @tparam A
    *   the type of the modified value
    * @return
    *   an empty sequence if the condition does not hold, a singleton with the
    *   modification if the condition holds
    */
  def when[A](condition: Boolean)(
      modification: Modification[A]
  ): Seq[Modification[A]] =
    when(condition)(Seq(modification))

  def when[A](condition: Boolean)(
      modifications: Seq[Modification[A]]
  ): Seq[Modification[A]] =
    if condition then modifications else Seq.empty

  /** A method that runs the effects of the [[sources]] obtained by [[pf]],
    * accumulating and applying the modifications starting from [[initial]] and
    * using [[input]] as the effect input.
    * @param initial
    *   the initial value to be modified
    * @param sources
    *   the sources of modifications
    * @param input
    *   the input to effects
    * @param pf
    *   function which obtains the effect method from a source
    * @tparam A
    *   the type of the [[initial]]
    * @tparam S
    *   the type of the [[sources]]
    * @tparam I
    *   the type of the [[input]]
    * @return
    *   the value after applying all the modifications
    */
  def run[A, S, I](initial: A, sources: Seq[S], input: I)(
      pf: PartialFunction[S, I => Seq[Modification[A]]]
  ): A =
    sources.collect(pf).flatMap(effect => effect(input)).applyAll(initial)

  extension [A](mods: Seq[Modification[A]])
    /** A method that aggregates and applies all the modifications in a
      * sequence.
      * @param initial
      *   the initial value
      * @return
      *   the modified value after all the applications
      */
    def applyAll(initial: A): A =
      mods.foldLeft(initial)((acc, mod) => mod.apply(acc))

/** A capability trait for an effect invoked at the start of the round.
  */
trait OnRoundStartEffect:
  def onRoundStart(round: RoundState): Seq[RoundStateModification]

/** A capability trait for an effect invoked when a card has scored.
  */
trait OnCardScoredEffect:
  def onCardScored(card: Card): Seq[HandScoreModification]

/** A capability trait for an effect invoked right when a hand has been played.
  */
trait OnHandPlayedEffect:
  def onHandPlayed(cards: Seq[Card]): Seq[HandScoreModification]

/** A capability trait for an effect invoked after a hand has been played.
  */
trait AfterHandPlayedEffect:
  def afterHandPlayed(cards: Seq[Card]): Seq[HandScoreModification]

/** A capability trait for an effect invoked when an entity is bought.
  */
trait OnBuyEffect:
  def onBuy(gameState: GameState): Seq[GameStateModification]
