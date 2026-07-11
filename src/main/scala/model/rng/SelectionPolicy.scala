package scalatro
package model.rng

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.SelectionPolicy.UniformSelection

/** A trait for types that can be weighed during random selection */
trait Weighable

/** A policy for assigning a weight to elements */
trait SelectionPolicy[T <: Weighable]:
  /** The weight of the element
    * @param elem
    *   the element to weigh
    * @return
    *   the weight
    */
  def weight(elem: T): Weight

  /** A description of the policy
    * @return
    *   the description
    */
  def description: String = "A selection policy"

object SelectionPolicy:
  private val defaultBoostWeight: Weight = Weight(2.0)

  /** A selection policy which assigns a uniform weight to every item
    * @tparam T
    *   the type of the item
    */
  class UniformSelection[T <: Weighable] extends SelectionPolicy[T]:
    def weight(elem: T): Weight = Weight(1.0)

  /** Boost a single rank
    * @param rank
    *   the rank to boost
    * @param bonus
    *   the bonus weight to apply to the rank
    */
  trait BoostRank(rank: Rank, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card.rank match
        case `rank` => super.weight(card) * bonus
        case _      => super.weight(card)

  /** Boost a single suit
    * @param suit
    *   the suit to boost
    * @param bonus
    *   the bonus weight to apply to the suit
    */
  trait BoostSuit(suit: Suit, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card.suit match
        case `suit` => super.weight(card) * bonus
        case _      => super.weight(card)

  /** Boost a specific card
    * @param boostedCard
    *   the card to boost
    * @param bonus
    *   the bonus weight to apply to the card
    */
  trait BoostCard(boostedCard: Card, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card match
        case `boostedCard` => super.weight(card) * bonus
        case _             => super.weight(card)

  /** Boost cards with faces
    * @param bonus
    *   the bonus weight to apply to cards with faces
    */
  trait BoostFaces(bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Card]:
    abstract override def weight(card: Card): Weight =
      card.rank match
        case Rank.King | Rank.Queen | Rank.Jack => super.weight(card) * bonus
        case _                                  => super.weight(card)

  /** Boost a specific joker
    * @param jokerType
    *   the joker to boost
    * @param bonus
    *   the bonus weight to apply to the joker
    */
  trait BoostJoker(jokerType: JokerType, bonus: Weight = defaultBoostWeight)
      extends SelectionPolicy[Joker]:
    abstract override def weight(joker: Joker): Weight =
      joker match
        case `jokerType` => super.weight(joker) * bonus
        case _           => super.weight(joker)

  /** Boost a specific planet hand type
    * @param handType
    *   the handType associated to the planet to boost
    * @param bonus
    *   the bonus weight to apply to the planet
    */
  trait BoostPlanetHandType(
      handType: HandType,
      bonus: Weight = defaultBoostWeight
  ) extends SelectionPolicy[Planet]:
    abstract override def weight(planet: Planet): Weight =
      planet.handType match
        case `handType` => super.weight(planet) * bonus
        case _          => super.weight(planet)

/** A class grouping selection policies for cards, planets and jokers
  * @param cardPolicy
  *   the policy for cards
  * @param planetPolicy
  *   the policy for planets
  * @param jokerPolicy
  *   the policy for jokers
  */
case class SelectionPolicies(
    cardPolicy: SelectionPolicy[Card],
    planetPolicy: SelectionPolicy[Planet],
    jokerPolicy: SelectionPolicy[Joker]
)

object SelectionPolicies:
  /** Uniform selection policies for cards, planets and jokers */
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
