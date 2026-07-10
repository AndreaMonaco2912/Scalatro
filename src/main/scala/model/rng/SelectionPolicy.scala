package scalatro
package model.rng

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.SelectionPolicy.UniformSelection

trait Weighable

trait SelectionPolicy[T <: Weighable]:
  def weight(elem: T): Weight
  def description: String = "A selection policy"

object SelectionPolicy:
  val defaultBoostWeight: Weight = Weight(2.0)

  /** Base case with standard weight */
  class UniformSelection[T <: Weighable] extends SelectionPolicy[T]:
    def weight(elem: T): Weight = Weight(1.0)

  /** Boosts one rank, e.g., a "hot" rank or the ace. */
  trait BoostRank(rank: Rank, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card.rank match
        case `rank` => super.weight(card) * bonus
        case _      => super.weight(card)

  /** Boosts one suit, e.g., to bias toward a flush. */
  trait BoostSuit(suit: Suit, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card.suit match
        case `suit` => super.weight(card) * bonus
        case _      => super.weight(card)

  trait BoostCard(boostedCard: Card, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card match
        case `boostedCard` => super.weight(card) * bonus
        case _             => super.weight(card)

  trait BoostFaces(bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card.rank match
        case Rank.King | Rank.Queen | Rank.Jack => super.weight(card) * bonus
        case _                                  => super.weight(card)

  trait BoostJoker(jokerType: JokerType, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Joker]:
    abstract override def weight(joker: Joker): Weight =
      joker match
        case `jokerType` => super.weight(joker) * bonus
        case _           => super.weight(joker)

  trait BoostPlanetHandType(
      handType: HandType,
      bonus: Weight = defaultBoostWeight
  ) extends SelectionPolicy[Planet]:
    abstract override def weight(planet: Planet): Weight =
      planet.handType match
        case `handType` => super.weight(planet) * bonus
        case _          => super.weight(planet)

case class SelectionPolicies(
    cardPolicy: SelectionPolicy[Card],
    planetPolicy: SelectionPolicy[Planet],
    jokerPolicy: SelectionPolicy[Joker]
)

object SelectionPolicies:
  val default: SelectionPolicies =
    SelectionPolicies(
      cardPolicy = new UniformSelection[Card],
      planetPolicy = new UniformSelection[Planet],
      jokerPolicy = new UniformSelection[Joker]
    )

object PresetPolicies:
  import SelectionPolicy.*
  val boostAces: SelectionPolicy[Card] =
    new UniformSelection[Card] with BoostRank(Ace):
      override def description: String = "Boosts Aces"
  val boostFaces: SelectionPolicy[Card] =
    new UniformSelection[Card] with BoostFaces():
      override def description: String = "Boosts face cards"
  val boostHearts: SelectionPolicy[Card] =
    new UniformSelection[Card] with BoostSuit(Hearts):
      override def description: String = "Boosts Hearts"
  val scholarPolicy: SelectionPolicy[Card] =
    new UniformSelection[Card]
      with BoostRank(Ace, Weight(2.0))
      with BoostFaces(Weight(0.5)):
      override def description: String =
        "Greatly increase priority of Aces, while nerfing face cards"
  val pairBiasedPlanets: SelectionPolicy[Planet] =
    new UniformSelection[Planet]
      with BoostPlanetHandType(HandType.Pair, Weight(100.0)):
      override def description: String = "Boosts planets with Pair hand type"
