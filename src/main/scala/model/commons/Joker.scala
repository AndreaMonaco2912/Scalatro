package scalatro
package model.commons

import model.round.Hand

/** The information needed to apply the effects of a joker
  * @param playedCards
  *   the cards played
  * @param levels
  *   the level of the hand types
  */
case class JokerConfig(
    playedCards: Seq[Card],
    levels: HandTypeLevels
)

object JokerConfig:

  def default: JokerConfig = JokerConfig(Seq.empty, HandTypeLevels.initial)

sealed trait Joker:

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

/** Trait which, if the hand played is of the specified type, increases the hand
  * score by the addition of a certain amount
  */
sealed trait FlatHandTypeContained(handType: HandType, increase: HandScore)
    extends Joker:

  override def independent(
      handScore: HandScore
  )(using jokerConfig: JokerConfig): HandScore =
    if HandType.contains(jokerConfig.playedCards, handType) then
      super.independent(handScore) + increase
    else super.independent(handScore)

/** Trait which, if the hand played is of the specified type, increases the hand
  * score multiplying the mult component by a certain amount
  */
sealed trait MultiplicativeHandTypeContained(
    handType: HandType,
    multiplier: Double
) extends Joker:

  override def independent(handScore: HandScore)(using
      jokerConfig: JokerConfig
  ): HandScore =
    (
      super.independent(handScore),
      HandType.contains(jokerConfig.playedCards, handType)
    ) match
      case (HandScore(chips, mult), true) => HandScore(chips, mult * multiplier)
      case (handScore, _)                 => handScore

/** Trait which increases the score with an addition by a certain amount
  */
sealed trait FlatScoreIncrease(increase: HandScore) extends Joker:

  override def independent(handScore: HandScore)(using
      jokerConfig: JokerConfig
  ): HandScore = super.independent(handScore + increase)

// TODO: .values funziona solo se nessun joker ha uno stato interno (es. case MyJoker(var bonus : HandScore))
// in questo caso si dovrebbe dichiarare .values nel companion object
enum JokerType(val name: String, val description: String) extends Joker:
  case CleverJoker
      extends JokerType(
        "Clever Joker",
        "+80 Chips if played hand contains a Two Pair"
      )
      with FlatHandTypeContained(HandType.TwoPair, HandScore(80, 0))
  case CraftyJoker
      extends JokerType(
        "Crafty Joker",
        "+80 Chips if played hand contains a Flush"
      )
      with FlatHandTypeContained(HandType.Flush, HandScore(80, 0))
  case CrazyJoker
      extends JokerType(
        "Crazy Joker",
        "+12 Mult if played hand contains a Straight"
      )
      with FlatHandTypeContained(HandType.Straight, HandScore(0, 12))
  case DeviousJoker
      extends JokerType(
        "Devious Joker",
        "+100 Chips if played hand contains a Straight"
      )
      with FlatHandTypeContained(HandType.Straight, HandScore(100, 0))
