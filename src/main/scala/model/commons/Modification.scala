package scalatro
package model.commons

import model.round.{RoundState, RoundStateModification}

trait Modification[T]:
  def apply(value: T): T

object Modification:
  def when[A](condition: Boolean)(
      modification: Modification[A]
  ): Seq[Modification[A]] =
    when(condition)(Seq(modification))

  def when[A](condition: Boolean)(
      modifications: Seq[Modification[A]]
  ): Seq[Modification[A]] =
    if condition then modifications else Seq.empty

  def run[A, S, I](initial: A, sources: Seq[S], input: I)(
      pf: PartialFunction[S, I => Seq[Modification[A]]]
  ): A =
    sources.collect(pf).flatMap(effect => effect(input)).applyAll(initial)

  extension [A](mods: Seq[Modification[A]])
    def applyAll(initial: A): A =
      mods.foldLeft(initial)((acc, mod) => mod.apply(acc))

trait OnRoundStartEffect:
  def onRoundStart(round: RoundState): Seq[RoundStateModification]

trait OnCardScoredEffect:
  def onCardScored(card: Card): Seq[HandScoreModification]

trait AfterHandPlayedEffect:
  def afterHandPlayed(cards: Seq[Card]): Seq[HandScoreModification]

trait OnHandPlayedEffect:
  def onHandPlayed(cards: Seq[Card]): Seq[HandScoreModification]
