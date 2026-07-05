package scalatro
package model.commons

import model.commons.Mult.Mult
import model.rng.Weighable

///** The information modifiable by the effects of a joker
//  */
//trait JokerContext extends Context[Joker]:
//
//  /** @return
//    *   the hand score
//    */
//  def handScore: HandScore
//
//  /** @return
//    *   the cards played
//    */
//  def playedCards: Seq[Card]
//
//  /** @return
//    *   the level of hand types
//    */
//  def levels: HandTypeLevels

case class JokerContext(
    playedCards: Seq[Card],
    levels: HandTypeLevels,
    handScore: HandScore
) extends Context[Joker]

object JokerContext:

  def default: JokerContext =
    JokerContext(Seq.empty, HandTypeLevels.initial, HandScore.zero)

type JokerEffect = Effect[JokerContext]

sealed trait Joker extends Weighable, EffectSource:

  /** @return
    *   The name of the joker
    */
  def name: String

  /** @return
    *   The description of the joker
    */
  def description: String

  /** The effect applied at the end of the hand played, before obtaining the
    * total hand score
    * @return
    *   the new hand score
    */
  def independent: JokerEffect = Effect.identity

  /** The effect applied when a card played is scored
    * @return
    *   the new hand score
    */
  def onCardScored(card: Card): JokerEffect = Effect.identity

  /** The effect applied when a hand is played
    * @return
    *   the new hand score
    */
  def onHandPlayed(cards: Seq[Card]): JokerEffect = Effect.identity

/** Trait which, if the hand played is of the specified type, increases the hand
  * score by the addition of a certain amount
  */
sealed trait FlatHandTypeContained(handType: HandType, increase: HandScore)
    extends Joker:

  override def independent: JokerEffect =
    super.independent.andThen(Effect { context =>
      if HandType.contains(context.playedCards, handType)
      then context.copy(handScore = context.handScore + increase)
      else context
    })

/** Trait which, if the hand played is of the specified type, increases the hand
  * score multiplying the mult component by a certain amount
  */
sealed trait MultiplicativeHandTypeContained(
    handType: HandType,
    multiplier: Mult
) extends Joker:

  override def independent: JokerEffect =
    super.independent.andThen(Effect { context =>
      if HandType.contains(context.playedCards, handType)
      then context.copy(handScore = context.handScore * multiplier)
      else context
    })

/** Trait which increases the score with an addition by a certain amount
  */
sealed trait FlatScoreIncrease(increase: HandScore) extends Joker:

  override def independent: JokerEffect =
    super.independent.andThen(Effect { context =>
      context.copy(handScore = context.handScore + increase)
    })

sealed trait FlatSuitScored(suit: Suit, increase: HandScore) extends Joker:

  override def onCardScored(card: Card): JokerEffect =
    super
      .onCardScored(card)
      .andThen(Effect { context =>
        if card.suit == suit then
          context.copy(handScore = context.handScore + increase)
        else context
      })

enum JokerType(val name: String, val description: String) extends Joker:
  case CleverJoker
      extends JokerType(
        "Clever Joker",
        "+80 Chips if played hand contains a Two Pair"
      )
      with FlatHandTypeContained(
        HandType.TwoPair,
        HandScore(Chips(80))
      )
  case CraftyJoker
      extends JokerType(
        "Crafty Joker",
        "+80 Chips if played hand contains a Flush"
      )
      with FlatHandTypeContained(
        HandType.Flush,
        HandScore(Chips(80))
      )
  case CrazyJoker
      extends JokerType(
        "Crazy Joker",
        "+12 Mult if played hand contains a Straight"
      )
      with FlatHandTypeContained(
        HandType.Straight,
        HandScore(Mult(12))
      )
  case DeviousJoker
      extends JokerType(
        "Devious Joker",
        "+100 Chips if played hand contains a Straight"
      )
      with FlatHandTypeContained(
        HandType.Straight,
        HandScore(Chips(100))
      )
  case TheTribe
      extends JokerType("The Tribe", "X2 Mult if played hand contains a Flush")
      with MultiplicativeHandTypeContained(HandType.Flush, Mult(2))
  case TheOrder
      extends JokerType(
        "The Order",
        "X3 Mult if played hand contains a Straight"
      )
      with MultiplicativeHandTypeContained(HandType.Straight, Mult(3))
  case Arrowhead
      extends JokerType(
        "Arrowhead",
        "Played cards with Spade suit give +50 Chips when scored"
      )
      with FlatSuitScored(Suit.Spades, HandScore(Chips(50)))
  case OnyxAgate
      extends JokerType(
        "Onyx Agate",
        "Played cards with Club suit give +7 Mult when scored"
      )
      with FlatSuitScored(Suit.Clubs, HandScore(Mult(7)))
