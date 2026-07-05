package scalatro
package model.commons

import model.commons.Effect.andThen
import model.commons.HandScore
import model.rng.Weighable

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

case class CardContext(handScore: HandScore) extends Context[Card]

object CardContext:
  def default: CardContext = CardContext(HandScore.zero)

type CardEffect = Effect[CardContext]

trait Card extends Weighable, EffectSource:
  def rank: Rank
  def suit: Suit
  def onScored: CardEffect = Effect.identity

object Card:
  def apply(rank: Rank, suit: Suit): Card = CardImpl(rank, suit)

  private case class CardImpl(rank: Rank, suit: Suit) extends Card:
    override def onScored: CardEffect =
      super.onScored.andThen(Effect { context =>
        context.copy(handScore = context.handScore + this.baseChips)
      })

    private def baseChips: Chips.Chips = this.rank match
      case Rank.Jack | Rank.Queen | Rank.King => Chips(10)
      case Rank.Ace                           => Chips(11)
      case r                                  => Chips(r.value)
