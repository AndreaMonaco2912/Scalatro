package scalatro
package model.rng.seed

import model.commons.{Card, Joker, Planet}
import model.rng.seed.SimulationTypes.SimRound

// Constraints for the seed finder

// Cards
// Round First Hand custom
// Round First Hand contains specific cards
// Round First Hand contains hand type
// Card X at pack Y

// Jokers
// Joker X at pack Y (with dim)

// Planets
// Planet X at pack Y (with dim)

sealed trait SeedConstraint:
  def round: Int
  def isSatisfiedBy(evidence: SimRound): Boolean

final case class InitialHandWith(cards: Seq[Card], round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = cards.diff(e.hand).isEmpty

final case class CardPackContains(card: Card, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = e.cardPack.items.contains(card)

final case class JokerPackContains(joker: Joker, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = e.jokerPack.items.contains(joker)

final case class PlanetPackContains(planet: Planet, round: Int)
    extends SeedConstraint:
  def isSatisfiedBy(e: SimRound): Boolean = e.planetPack.items.contains(planet)
