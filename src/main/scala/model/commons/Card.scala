package scalatro
package model.commons

import model.rng.Weighable

enum Suit:
  case Spades, Hearts, Clubs, Diamonds

/** An enum that represents a rank of a card.
  * @param value
  *   the value of the rank (used for ordering and assigning chips)
  */
enum Rank(val value: Int):
  case Two extends Rank(2)
  case Three extends Rank(3)
  case Four extends Rank(4)
  case Five extends Rank(5)
  case Six extends Rank(6)
  case Seven extends Rank(7)
  case Eight extends Rank(8)
  case Nine extends Rank(9)
  case Ten extends Rank(10)
  case Jack extends Rank(11)
  case Queen extends Rank(12)
  case King extends Rank(13)
  case Ace extends Rank(14)

trait Card extends Weighable:
  def rank: Rank
  def suit: Suit
  def onScored: Seq[HandScoreModification]

object Card:
  /** Creates a card.
    * @param rank
    *   the rank of the card
    * @param suit
    *   the suit of the card
    * @return
    *   the card
    */
  def apply(rank: Rank, suit: Suit): Card = CardImpl(rank, suit)

  private case class CardImpl(rank: Rank, suit: Suit) extends Card:
    /** Create the modification that increment the score by the base chip of the
      * card
      * @return
      *   the modification to the hand score
      */
    override def onScored: Seq[HandScoreModification] =
      Seq(HandScoreModification.FlatChips(this.baseChips))

    private def baseChips: Chips.Chips = this.rank match
      case Rank.Jack | Rank.Queen | Rank.King => Chips(10)
      case Rank.Ace                           => Chips(11)
      case r                                  => Chips(r.value)
