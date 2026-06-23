package scalatro
package model.commons

/** A trait that defines a strategy for ordering a sequence of cards. Different
  * implementations to define various ways of reordering a sequence of cards.
  */
trait CardOrderer:
  /** Orders the cards according to a rule defined by the implementing
    * CardOrderer.
    *
    * @param cards
    *   the sequence of cards to be ordered
    * @return
    *   the new sequence of cards
    */
  def order(cards: Seq[Card]): Seq[Card]

/** A collection of predefined [[CardOrderer]]s or templates to create them
  */
object CardOrderer:
  /** An [[scala.math.Ordering]] for [[Rank]] in decreasing order by their
    * numeric value
    */
  given rankOrdering: Ordering[Rank] = (r1, r2) => r2.value compare r1.value

  /** An [[scala.math.Ordering]] for [[Suit]], based on their ordinal position
    * Suit
    */
  given suitOrdering: Ordering[Suit] = _.ordinal compare _.ordinal

  /** Maintains the original card order
    * @return
    *   the card orderer
    */
  val identity: CardOrderer = cards => cards

  /** Sorts cards by increasing [[Rank]], then sorts each rank group by [[Suit]]
    * @return
    *   the card orderer
    */
  val sortByRank: CardOrderer = _.sortBy(c => (c.rank, c.suit))

  /** Sorts cards by [[Suit]] according to the suit ordering, then sorts each
    * suit group by rank.
    * @return
    *   the card orderer
    */
  val sortBySuit: CardOrderer = _.sortBy(c => (c.suit, c.rank))

  /** Swaps two cards at the given positions. Both indexes must be within the
    * sequence bounds.
    *
    * @param i
    *   the index of the first card to swap
    * @param j
    *   the index of the second card to swap
    * @return
    *   the card orderer
    */
  def swapCards(i: Int, j: Int): CardOrderer = cards =>
    require(i >= 0 && i < cards.size, s"i must be in [0, ${cards.size})")
    require(j >= 0 && j < cards.size, s"j must be in [0, ${cards.size})")
    cards.updated(i, cards(j)).updated(j, cards(i))

  /** Moves a card from one position to another, shifting other cards to fill
    * the gap. Both indexes must be within the sequence bounds.
    *
    * @param from
    *   the initial index of the card
    * @param to
    *   the target index of the card
    * @return
    *   the card orderer
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
