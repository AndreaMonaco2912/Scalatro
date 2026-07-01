package scalatro
package model.commons

import model.rng.ScalatroRng

import java.util.function.Consumer
import scala.util.Random

opaque type Deck = Seq[Card]

object Deck:
  def apply(): Deck =
    for
      suit <- Suit.values.toSeq
      rank <- Rank.values.toSeq
    yield Card(rank, suit)

  def apply(cards: Seq[Card]): Deck =
    cards

  extension (d: Deck)
    def shuffle(using rng: ScalatroRng): Deck = rng.shuffle(d)
    def sort: Deck = CardOrderer.sortBySuit.order(d)
    def draw(n: Int): (Seq[Card], Deck) =
      require(n >= 0 && n <= d.size, s"cannot draw $n from a deck of ${d.size}")
      d.splitAt(n)
    def size: Int = d.size
    def add(card: Card): Deck = d :+ card
    def foreach[A](function: Card => A): Unit = d.foreach(function)
    def cards: Seq[Card] = d
