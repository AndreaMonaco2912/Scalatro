package scalatro
package model.round

import model.commons.{Card, Deck}
import model.commons.Score.Score

object Hand:
  opaque type Hand = Seq[Card]
  def empty: Hand = Seq()
  extension (hand: Hand) def isEmpty: Boolean = hand.isEmpty

case class Round(
    score: Score,
    hand: Seq[Card],
    deck: Deck
)
