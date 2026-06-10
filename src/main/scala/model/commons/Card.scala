package scalatro
package model.commons

enum Suit:
  case Spades, Hearts, Clubs, Diamonds

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

case class Card(rank: Rank, suit: Suit):
  def onScored(prevHandScore: HandScore.HandScore): HandScore.HandScore =
    HandScore(this.getBaseChips + prevHandScore.chips, prevHandScore.mult)

  def getBaseChips: Chips.Chips = this.rank match
    case Rank.Jack | Rank.Queen | Rank.King => 10
    case Rank.Ace => 11
    case r => r.value