package scalatro
package model.commons

import model.rng.Weighable
import model.round.{RoundState, RoundStateModification}

sealed trait Joker extends Weighable:

  /** @return
    *   The name of the joker
    */
  def name: String

  /** @return
    *   The description of the joker
    */
  def description: String

  /** The effect applied at the start of a round
    * @param round
    *   the round
    * @return
    *   the modifications
    */
  def onRoundStart(round: RoundState): Seq[Modification] = Seq.empty

  /** The effect applied when a card played is scored
    * @return
    *   the modifications
    */
  def onCardScored(card: Card): Seq[Modification] = Seq.empty

  /** The effect applied when a hand is played
    * @return
    *   the modifications
    */
  def onHandPlayed(cards: Seq[Card]): Seq[Modification] = Seq.empty

  /** The effect applied after a hand is played
    * @param cards
    *   the cards played
    * @return
    *   the modifications
    */
  def afterHandPlayed(cards: Seq[Card]): Seq[Modification] = Seq.empty

/** Trait which, if the hand played is of the specified type, increases the hand
  * score by the addition of a certain amount
  */
sealed trait HandTypeContained(
    handType: HandType,
    modification: HandScoreModification
) extends Joker:

  override def afterHandPlayed(cards: Seq[Card]): Seq[Modification] =
    super.onHandPlayed(cards) ++ Modification.when(
      HandType.detect(cards) == handType
    )(modification)

/** Trait which increases the score with an addition by a certain amount
  */
sealed trait SuitScored[A](suit: Suit, modification: Modification)
    extends Joker:

  override def onCardScored(card: Card): Seq[Modification] =
    super.onCardScored(card) ++ Modification.when(
      card.suit == suit
    )(modification)

sealed trait RanksScored[A](
    ranks: Seq[Rank],
    modifications: Seq[Modification]
) extends Joker:
  override def onCardScored(card: Card): Seq[Modification] =
    super.onCardScored(card) ++ Modification.when(ranks.contains(card.rank))(
      modifications
    )

sealed trait HandInformationModifier(modifications: Seq[Modification])
    extends Joker:
  override def onRoundStart(round: RoundState): Seq[Modification] =
    modifications

enum JokerType(val name: String, val description: String) extends Joker:
  case CleverJoker
      extends JokerType(
        "Clever Joker",
        "+80 Chips if played hand contains a Two Pair"
      )
      with HandTypeContained(
        HandType.TwoPair,
        HandScoreModification.FlatChips(Chips(80))
      )
  case CraftyJoker
      extends JokerType(
        "Crafty Joker",
        "+80 Chips if played hand contains a Flush"
      )
      with HandTypeContained(
        HandType.Flush,
        HandScoreModification.FlatChips(Chips(80))
      )
  case CrazyJoker
      extends JokerType(
        "Crazy Joker",
        "+12 Mult if played hand contains a Straight"
      )
      with HandTypeContained(
        HandType.Straight,
        HandScoreModification.FlatMult(Mult(12))
      )
  case DeviousJoker
      extends JokerType(
        "Devious Joker",
        "+100 Chips if played hand contains a Straight"
      )
      with HandTypeContained(
        HandType.Straight,
        HandScoreModification.FlatChips(Chips(100))
      )
  case TheTribe
      extends JokerType("The Tribe", "X2 Mult if played hand contains a Flush")
      with HandTypeContained(
        HandType.Flush,
        HandScoreModification.MultiplicativeMult(Mult(2))
      )
  case TheOrder
      extends JokerType(
        "The Order",
        "X3 Mult if played hand contains a Straight"
      )
      with HandTypeContained(
        HandType.Straight,
        HandScoreModification.MultiplicativeMult(Mult(3))
      )
  case Arrowhead
      extends JokerType(
        "Arrowhead",
        "Played cards with Spade suit give +50 Chips when scored"
      )
      with SuitScored(Suit.Spades, HandScoreModification.FlatChips(Chips(50)))
  case OnyxAgate
      extends JokerType(
        "Onyx Agate",
        "Played cards with Club suit give +7 Mult when scored"
      )
      with SuitScored(Suit.Clubs, HandScoreModification.FlatMult(Mult(7)))
  case Fibonacci
      extends JokerType(
        "Fibonacci",
        "Each played Ace, 2, 3, 5, or 8 gives +8 Mult when scored"
      )
      with RanksScored(
        Seq(Rank.Ace, Rank.Eight, Rank.Five, Rank.Three, Rank.Two),
        Seq(HandScoreModification.FlatMult(Mult(8)))
      )
  case Scholar
      extends JokerType(
        "Scholar",
        "Played Aces give +20 Chips and +4 Mult when scored"
      )
      with RanksScored(
        Seq(Rank.Ace),
        Seq(
          HandScoreModification.FlatChips(Chips(20)),
          HandScoreModification.FlatMult(Mult(4))
        )
      )
  case Juggler
      extends JokerType("Juggler", "+1 play and discard each round")
      with HandInformationModifier(
        Seq(
          RoundStateModification.IncreaseHandsRemaining(1),
          RoundStateModification.IncreaseDiscardsRemaining(1)
        )
      )
