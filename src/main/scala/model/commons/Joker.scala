package scalatro
package model.commons

import model.game.{GameState, GameStateModification}
import model.rng.{PresetPolicies, Weighable}
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

/** Trait which, if the hand played is of the specified type, increases the hand
  * score by the addition of a certain amount
  */
sealed trait HandTypeContained(
    handType: HandType,
    modification: HandScoreModification
) extends AfterHandPlayedEffect:

  override def afterHandPlayed(cards: Seq[Card]): Seq[HandScoreModification] =
    Modification.when(
      HandType.detect(cards) == handType
    )(modification)

/** Trait which increases the score with an addition by a certain amount
  */
sealed trait SuitScored(suit: Suit, modification: HandScoreModification)
    extends OnCardScoredEffect:

  override def onCardScored(card: Card): Seq[HandScoreModification] =
    Modification.when(
      card.suit == suit
    )(modification)

sealed trait RanksScored(
    ranks: Seq[Rank],
    modifications: Seq[HandScoreModification]
) extends OnCardScoredEffect:
  override def onCardScored(card: Card): Seq[HandScoreModification] =
    Modification.when(ranks.contains(card.rank))(
      modifications
    )

sealed trait OnRoundStartModifier(modifications: Seq[RoundStateModification])
    extends OnRoundStartEffect:
  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    modifications

sealed trait OnBuyModifier(modifications: Seq[GameStateModification])
    extends OnBuyEffect:
  override def onBuy(gameState: GameState): Seq[GameStateModification] =
    modifications

/** An enum which represents a type of joker card.
  * @param name
  *   the display name of the joker
  * @param description
  *   the description of the joker effects
  */
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
  case JollyJoker
      extends JokerType("Jolly Joker", "+8 Mult if played hand contains a Pair")
      with HandTypeContained(
        HandType.Pair,
        HandScoreModification.FlatMult(Mult(8))
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
  case TheDuo
      extends JokerType("The Duo", "X2 Mult if played hand contains a Pair")
      with HandTypeContained(
        HandType.Pair,
        HandScoreModification.MultiplicativeMult(Mult(2))
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
      with OnBuyModifier(
        Seq(GameStateModification.SetCardPolicy(PresetPolicies.scholarPolicy))
      )
  case Juggler
      extends JokerType("Juggler", "+1 play and discard each round")
      with OnRoundStartModifier(
        Seq(
          RoundStateModification.IncreaseRemainingPlays(1),
          RoundStateModification.IncreaseRemainingDiscards(1)
        )
      )
