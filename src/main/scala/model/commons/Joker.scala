package scalatro
package model.commons

import model.round.Hand
import model.game.{GameState, HandInformation}

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

trait Reversible:
  def reverse[A]: A

trait Effect[A]:
  def apply(value: A)(using JokerConfig): A

object Effect:

  def identity[A]: Effect[A] =
    new Effect[A]:
      def apply(value: A)(using JokerConfig): A = value

  def apply[A](f: (A, JokerConfig) => A): Effect[A] =
    new Effect[A]:
      def apply(value: A)(using jokerConfig: JokerConfig): A =
        f(value, jokerConfig)

  extension [A](self: Effect[A])
    def andThen(next: Effect[A]): Effect[A] =
      new Effect[A]:
        def apply(value: A)(using jokerConfig: JokerConfig): A =
          next(self(value))

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
    * @return
    *   the new hand score
    */
  def independent: Effect[HandScore] = Effect.identity

  /** The effect applied when a card played is scored
    * @return
    *   the new hand score
    */
  def onCardScored(card: Card): Effect[HandScore] = Effect.identity

  /** The effect applied when a hand is played
    * @return
    *   the new hand score
    */
  def onHandPlayed(cards: Seq[Card]): Effect[HandScore] = Effect.identity

/** Trait which, if the hand played is of the specified type, increases the hand
  * score by the addition of a certain amount
  */
sealed trait FlatHandTypeContained(handType: HandType, increase: HandScore)
    extends Joker:

  override def independent: Effect[HandScore] =
    super.independent.andThen(Effect { (handScore, jokerConfig) =>
      if HandType.contains(jokerConfig.playedCards, handType)
      then handScore + increase
      else handScore
    })

/** Trait which, if the hand played is of the specified type, increases the hand
  * score multiplying the mult component by a certain amount
  */
sealed trait MultiplicativeHandTypeContained(
    handType: HandType,
    multiplier: Double
) extends Joker:

  override def independent: Effect[HandScore] =
    super.independent.andThen(Effect { (handScore, jokerConfig) =>
      (handScore, HandType.contains(jokerConfig.playedCards, handType)) match
        case (HandScore(chips, mult), true) =>
          HandScore(chips, mult * multiplier)
        case (handScore, _) => handScore
    })

/** Trait which increases the score with an addition by a certain amount
  */
sealed trait FlatScoreIncrease(increase: HandScore) extends Joker:

  override def independent: Effect[HandScore] =
    super.independent.andThen(Effect { (handScore, _) => handScore + increase })

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
  case TheTribe
      extends JokerType("The Tribe", "X2 Mult if played hand contains a Flush")
      with MultiplicativeHandTypeContained(HandType.Flush, 2)
  case TheOrder
      extends JokerType(
        "The Order",
        "X3 Mult if played hand contains a Straight"
      )
      with MultiplicativeHandTypeContained(HandType.Straight, 3)
