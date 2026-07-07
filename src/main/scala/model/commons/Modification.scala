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

trait OnRoundStartEffect:
  def onRoundStart(round: RoundState): Seq[RoundStateModification]

trait OnCardScoredEffect:
  def onCardScored(card: Card): Seq[HandScoreModification]

trait AfterHandPlayedEffect:
  def afterHandPlayed(cards: Seq[Card]): Seq[HandScoreModification]

trait OnHandPlayedEffect:
  def onHandPlayed(cards: Seq[Card]): Seq[HandScoreModification]
