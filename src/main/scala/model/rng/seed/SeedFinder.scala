package scalatro
package model.rng.seed

import model.commons.*
import model.commons.Rank.Ace
import model.commons.Suit.{Clubs, Diamonds, Hearts, Spades}
import model.rng.SelectionPolicy.UniformSelection
import model.rng.Types.Seed
import model.rng.seed.SimulationActions.satisfiesConstraints
import model.rng.{ScalatroRng, SelectionPolicy}

import scala.util.Random

case class SelectionPolicies(
    cardPolicy: SelectionPolicy[Card],
    planetPolicy: SelectionPolicy[Planet],
    jokerPolicy: SelectionPolicy[Joker]
)

object SelectionPolicies:
  def default: SelectionPolicies =
    SelectionPolicies(
      cardPolicy = new UniformSelection[Card],
      planetPolicy = new UniformSelection[Planet],
      jokerPolicy = new UniformSelection[Joker]
    )

object SeedFinder:
  def findSeed(
      constraints: Seq[SeedConstraint],
      maxAttempts: Int = Int.MaxValue
  ): Seed =
    LazyList
      .continually(Random.nextLong())
      .map(Seed(_))
      .take(maxAttempts)
      .find(seed => satisfiesConstraints(constraints)(using ScalatroRng(seed)))
      .getOrElse(Seed(0L))

@main
def findSeedMain(): Unit =
  val constraints = Seq(
    InitialHandWith(Seq(Card(Ace, Hearts), Card(Ace, Diamonds)), 1),
    JokerPackContains(JokerType.CleverJoker, 1),
    InitialHandWith(Seq(Card(Ace, Spades), Card(Ace, Clubs)), 2),
    JokerPackContains(JokerType.CraftyJoker, 2)
  )
  val seed = SeedFinder.findSeed(constraints)
  println(s"Found seed: $seed")
