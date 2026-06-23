package scalatro
package model.commons

/** A trait that defines a strategy for ordering a sequence of cards. Different
  * implementations to define various ways of reordering a sequence of cards.
  */
trait CardOrderer:
  /** Orders the cards according to some rule defined by the implementing
    * CardOrderer.
    *
    * @param cards
    *   the sequence of cards to be ordered
    * @return
    *   the new sequence of cards
    */
  def order(cards: Seq[Card]): Seq[Card]

object CardOrderer:
  private given Ordering[Rank] = (r1, r2) => r2.value compare r1.value
  private given Ordering[Suit] = _.ordinal compare _.ordinal

  /** Maintains the same card order
    * @return
    *   the CardOrder
    */
  val identity: CardOrderer = cards => cards

  /** Sort cards by increasing rank
    * @return
    *   the CardOrder
    */
  val sortByRank: CardOrderer = _.sortBy(c => (c.rank, c.suit))

  /** Sort cards by suit, following [[Suit.values]] order, then sort each suite
    * group by rank.
    * @return
    *   the CardOrder
    */
  val sortBySuit: CardOrderer = _.sortBy(c => (c.suit, c.rank))

  /** Swap 2 cards. Indexes must be positive numbers and inside the Seq size.
    *
    * @param i
    *   the index of the first card to swap
    * @param j
    *   the index of the second card to swap
    * @return
    *   the CardOrder
    */
  def swapCards(i: Int, j: Int): CardOrderer = cards =>
    require(i >= 0 && i < cards.size, s"i must be in [0, ${cards.size})")
    require(j >= 0 && j < cards.size, s"j must be in [0, ${cards.size})")
    cards.updated(i, cards(j)).updated(j, cards(i))

  /** Moves a card from one position to another, shifting the remaining cards.
    * Indexes must be positive numbers and inside the Seq size.
    *
    * @param from
    *   the initial index of the card
    * @param to
    *   the target index of the card
    * @return
    *   the CardOrder
    */
  def moveCard(from: Int, to: Int): CardOrderer = cards =>
    require(
      from >= 0 && from < cards.size,
      s"from must be in [0, ${cards.size})"
    )
    require(to >= 0 && to < cards.size, s"to must be in [0, ${cards.size})")
    val card = cards(from)
    val withoutCard = cards.take(from) ++ cards.drop(from + 1)
    withoutCard.take(to) ++ Seq(card) ++ withoutCard.drop(to)
