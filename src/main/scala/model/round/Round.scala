package scalatro
package model.round

import model.round.Placeholders.Card
import model.round.Score.*

object Score:
  opaque type Score = Double
  def apply(d: Double): Score =
    require(d >= 0.0, "Score must be positive")
    d
  extension (s: Score)
    def +(other: Score): Score = s + other
    def -(other: Score): Score = s - other
    def >(other: Score): Boolean = s > other
    def <(other: Score): Boolean = s < other
    def >=(other: Score): Boolean = s >= other
    def <=(other: Score): Boolean = s >= other

  val zero: Score = Score(0.0)

object Hand:
  opaque type Hand = Seq[Card]
  def empty: Hand = Seq()
  extension (hand: Hand) def isEmpty: Boolean = hand.isEmpty

object Deck:
  opaque type Deck = Seq[Card]
  def empty: Deck = Seq()
  extension (deck: Deck) def isEmpty: Boolean = deck.isEmpty

case class Round(
    score: Score,
    hand: Seq[Card],
    deck: Seq[Card]
)
