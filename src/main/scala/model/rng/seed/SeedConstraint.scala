package scalatro
package model.rng.seed

import model.commons.{Card, HandType, Joker, Planet}
import model.rng.seed.SimulationTypes.SimRound

sealed trait SeedConstraint:
  def round: Int
  def isSatisfiedBy(evidence: SimRound): Boolean

final case class InitialHandWithCards(cards: Seq[Card], round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = cards.diff(e.hand).isEmpty

final case class InitialHandWithHandType(handType: HandType, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = HandType.detect(e.hand) == handType

final case class CardPackContains(card: Card, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = e.cardPack.items.contains(card)

final case class JokerPackContains(joker: Joker, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = e.jokerPack.items.contains(joker)

final case class PlanetPackContains(planet: Planet, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = e.planetPack.items.contains(planet)
