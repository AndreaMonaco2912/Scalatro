package scalatro
package model.rng.seed

import model.commons.{Card, HandType, Joker, Planet}
import model.extra.Hint
import model.rng.seed.SimulationTypes.SimRound

/** A trait representing a constraint on the seed */
sealed trait SeedConstraint:
  /** The round for which this constraint applies */
  def round: Int

  /** Checks whether the given round satisfies this constraint
    * @param round
    *   the round to check
    * @return
    *   true if the round satisfies this constraint, false otherwise
    */
  def isSatisfiedBy(round: SimRound): Boolean

/** A constraint that requires the initial hand of the round to contain the
  * given cards
  * @param cards
  *   the cards that must be in the hand
  * @param round
  *   the round for which this constraint applies
  */
final case class InitialHandWithCards(cards: Seq[Card], round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(round: SimRound): Boolean = cards.diff(round.hand).isEmpty

/** A constraint that requires the initial hand of the round to have the given
  * hand type
  * @param handType
  *   the hand type that the initial hand of the round must have
  * @param round
  *   the round for which this constraint applies
  */
final case class InitialHandWithHandType(handType: HandType, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(round: SimRound): Boolean =
    Hint
      .allPlayableHands(round.hand)
      .filter(_.sizeIs == 5)
      .exists(HandType.contains(_, handType))

/** A constraint that requires the card pack of the shop of the round to contain
  * the given card
  * @param card
  *   the card that must be in the pack
  * @param round
  *   the round for which this constraint applies
  */
final case class CardPackContains(card: Card, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(round: SimRound): Boolean =
    round.cardPack.items.contains(card)

/** A constraint that requires the joker pack of the shop of the round to
  * contain the given joker
  * @param joker
  *   the joker that must be in the pack
  * @param round
  *   the round for which this constraint applies
  */
final case class JokerPackContains(joker: Joker, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(round: SimRound): Boolean =
    round.jokerPack.items.contains(joker)

/** A constraint that requires the planet pack of the shop of the round to
  * contain the given planet
  * @param planet
  *   the planet that must be in the pack
  * @param round
  *   the round for which this constraint applies
  */
final case class PlanetPackContains(planet: Planet, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(round: SimRound): Boolean =
    round.planetPack.items.contains(planet)
