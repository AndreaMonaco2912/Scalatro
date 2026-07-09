package scalatro
package model.commons

import model.rng.ScalatroRng

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
    def sort: Deck = Orderer.sortBySuit.order(d)
    def draw(n: Int): (Seq[Card], Deck) =
      require(n >= 0, s"cannot draw a negative amount of cards")
      d.splitAt(Math.min(n,d.size))
    def size: Int = d.size
    def add(card: Card): Deck = d :+ card
    def foreach[A](function: Card => A): Unit = d.foreach(function)
    def cards: Seq[Card] = d
