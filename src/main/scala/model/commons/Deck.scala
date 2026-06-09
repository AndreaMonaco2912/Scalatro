package scalatro
package model.commons

import scala.util.Random

//enum Suit:
//  case Spades, Hearts, Clubs, Diamonds
//
//case class Card(rank: Int, suit: Suit)

def score(card: Card): Int = card.rank match
  case Rank.Jack | Rank.Queen | Rank.King => 10
  case Rank.Ace            => 11
  case n            => n.ordinal

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
    def shuffle(using rng: Random): Deck = rng.shuffle(d)
    def draw(n: Int): (Seq[Card], Deck) =
      require(n >= 0 && n <= d.size, s"cannot draw $n from a deck of ${d.size}")
      d.splitAt(n)
    def size: Int = d.size
    def cards: Seq[Card] = d
