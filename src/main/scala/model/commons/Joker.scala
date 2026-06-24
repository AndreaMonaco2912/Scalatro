package scalatro
package model.commons

import model.round.Hand

/** The information needed to apply the effects of a joker
  * @param playedCards
  *   the cards played
  * @param inHandCards
  *   the cards held in hand
  * @param levels
  *   the level of the hand types
  */
case class JokerConfig(
    playedCards: Seq[Card],
    inHandCards: Seq[Card],
    levels: HandTypeLevels
)

class Joker:

  /** The effect applied at the end of the hand played, before obtaining the
    * total hand score
    * @param handScore
    *   the hand score before the effect is applied
    * @param jokerConfig
    *   the joker effect configuration
    * @return
    *   the hand score after the effect is applied
    */
  def independent(handScore: HandScore)(using
      jokerConfig: JokerConfig
  ): HandScore = handScore

  /** The effect applied when a card played is scored
    * @param handScore
    *   the hand score before the effect is applied
    * @param card
    *   the card scored
    * @param jokerConfig
    *   the joker effects configuration
    * @return
    *   the hand score after the effect is applied
    */
  def onCardScored(handScore: HandScore, card: Card)(using
      jokerConfig: JokerConfig
  ): HandScore = handScore

  /** The effect applied when a hand is played
    * @param handScore
    *   the hand score before the effect is applied
    * @param hand
    *   the hand played
    * @param jokerConfig
    *   the joker effects configuration
    * @return
    *   the hand score after the effect is applied
    */
  def onHandPlayed(handScore: HandScore, hand: Hand)(using
      jokerConfig: JokerConfig
  ): HandScore = handScore

  /** The effect applied on a card held in hand
    * @param handScore
    *   the hand score before the effect is applied
    * @param card
    *   the card held in hand
    * @param jokerConfig
    *   the joker effects configuration
    * @return
    *   the hand score after the effect is applied
    */
  def onCardHeld(handScore: HandScore, card: Card)(using
      jokerConfig: JokerConfig
  ): HandScore = handScore

object Joker:
  def apply(jokerType: JokerType): Joker = jokerType match
    case JokerType.CleverJoker =>
      new Joker with FlatHandTypeContained(HandType.TwoPair, HandScore(80, 0))
    case JokerType.CraftyJoker =>
      new Joker with FlatHandTypeContained(HandType.Flush, HandScore(80, 0))
    case JokerType.CrazyJoker =>
      new Joker with FlatHandTypeContained(HandType.Straight, HandScore(0, 12))
    case JokerType.DeviousJoker =>
      new Joker with FlatHandTypeContained(HandType.Straight, HandScore(100, 0))

/** Trait which, if the hand played is of the specified type, increases the hand
  * score by the addition of a certain amount
  */
trait FlatHandTypeContained(handType: HandType, val increase: HandScore)
    extends Joker:

  override def independent(
      handScore: HandScore
  )(using jokerConfig: JokerConfig): HandScore =
    if HandType.contains(jokerConfig.playedCards, handType) then
      increase + handScore
    else handScore

/** Trait which, if the hand played is of the specified type, increases the hand
  * score multiplying the mult component by a certain amount
  */
trait MultiplicativeHandTypeContained(handType: HandType, multiplier: Double)
    extends Joker:

  override def independent(handScore: HandScore)(using
      jokerConfig: JokerConfig
  ): HandScore =
    (handScore, HandType.contains(jokerConfig.playedCards, handType)) match
      case (HandScore(chips, mult), true) => HandScore(chips, mult * multiplier)
      case (handScore, false)             => handScore

/** Trait which increases the score with an addition by a certain amount
  */
trait FlatScoreIncrease(increase: HandScore) extends Joker:

  override def independent(handScore: HandScore)(using
      jokerConfig: JokerConfig
  ): HandScore = super.independent(handScore) + increase

enum JokerType(val name: String, val description: String):
  case CleverJoker
      extends JokerType(
        "Clever Joker",
        "+80 Chips if played hand contains a Two Pair"
      )
  case CraftyJoker
      extends JokerType(
        "Crafty Joker",
        "+80 Chips if played hand contains a Flush"
      )
  case CrazyJoker
      extends JokerType(
        "Crazy Joker",
        "+12 Mult if played hand contains a Straight"
      )
  case DeviousJoker
      extends JokerType(
        "Devious Joker",
        "+100 Chips if played hand contains a Straight"
      )
