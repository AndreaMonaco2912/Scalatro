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
  /** Maintains the same card order
    * @return
    *   the CardOrder
    */
  val identity: CardOrderer = cards => cards

  /** Sort cards by increasing rank
    * @return
    *   the CardOrder
    */
  val sortByRank: CardOrderer = _.sortBy(_.rank.value)

  /** Sort cards by suit, following [[Suit.values]] order, then sort each suite
    * group by rank.
    * @return
    *   the CardOrder
    */
  val sortBySuit: CardOrderer = cards =>
    val bySuit = cards.groupBy(_.suit)
    for
      suit <- Suit.values.toSeq
      card <- bySuit.getOrElse(suit, Nil).sortBy(_.rank.value)
    yield card

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
