package scalatro
package model.commons

import model.rng.ScalatroRng

/** An ordered collection of cards. */
opaque type Deck = Seq[Card]

object Deck:
  def apply(): Deck =
    /** Creates a poker 52 card deck.
      * @return
      *   the deck
      */
    for
      suit <- Suit.values.toSeq
      rank <- Rank.values.toSeq
    yield Card(rank, suit)

  /** Creates a deck from the given cards.
    *
    * @param cards
    *   the cards of the deck
    * @return
    *   the deck
    */
  def apply(cards: Seq[Card]): Deck =
    cards

  extension (d: Deck)
    /** Shuffles the deck.
      * @param rng
      *   the scalatro game random
      * @return
      *   the shuffled deck
      */
    def shuffle(using rng: ScalatroRng): Deck = rng.shuffle(d)

    /** Sorts the deck by suit, then by rank.
      *
      * @return
      *   the sorted deck
      */
    def sort: Deck = Orderer.sortBySuit.order(d)

    /** Draws the first `n` cards from the top of the deck. If the deck holds
      * fewer than `n` cards, all of them are drawn.
      *
      * @param n
      *   the number of cards to draw
      * @return
      *   the drawn cards and the remaining deck
      */
    def draw(n: Int): (Seq[Card], Deck) =
      require(n >= 0, s"cannot draw a negative amount of cards")
      d.splitAt(Math.min(n, d.size))

    /** The number of cards in the deck.
      *
      * @return
      *   the size
      */
    def size: Int = d.size

    /** Adds a card to the deck.
      *
      * @param card
      *   the card to add
      * @return
      *   the deck with the added card
      */
    def add(card: Card): Deck = d :+ card

    /** Applies `f` to every card in the deck.
      *
      * @param f
      *   the function to apply
      */
    def foreach[A](f: Card => A): Unit = d.foreach(f)
    def cards: Seq[Card] = d
