package scalatro
package model.rng

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*

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

object PresetPolicies:
  import SelectionPolicy.*
  def boostAces: SelectionPolicy[Card] =
    new UniformSelection[Card] with BoostRank(Ace):
      override def description: String = "Boosts Aces"
  def boostHearts: SelectionPolicy[Card] =
    new UniformSelection[Card] with BoostSuit(Hearts):
      override def description: String = "Boosts Hearts"
  def noFaces: SelectionPolicy[Card] =
    new UniformSelection[Card] with BoostFaces(Weight(0.0)):
      override def description: String = "Disables face cards"
  def crazyCards: SelectionPolicy[Card] =
    new UniformSelection[Card]
      with BoostRank(Ace)
      with BoostSuit(Hearts, Weight(0.5))
      with BoostCard(Card(Ten, Spades), Weight(0.0)):
      override def description: String =
        "Boosts Aces, nerfs Hearts, disables Ten of Spades"

  def pairBiasedPlanets: SelectionPolicy[Planet] =
    new UniformSelection[Planet]
      with BoostPlanetHandType(HandType.Pair, Weight(100.0)):
      override def description: String = "Boosts planets with Pair hand type"
