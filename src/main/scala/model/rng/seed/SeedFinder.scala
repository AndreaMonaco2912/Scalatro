package scalatro
package model.rng.seed

import model.commons.*
import model.commons.Rank.Ace
import model.commons.Suit.{Diamonds, Hearts}
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

object SeedFinder:
  def findSeed(
      constraints: Seq[SeedConstraint],
      handSize: Int = 5,
      maxAttempts: Int = Int.MaxValue
  )(using policies: SelectionPolicies): Seed =
    LazyList
      .continually(Random.nextLong())
      .map(Seed(_))
      .take(maxAttempts)
      .find(seed =>
        satisfiesConstraints(constraints, handSize)(using
          ScalatroRng(seed),
          policies
        )
      )
      .getOrElse(Seed(0L))

@main
def findSeedMain(): Unit =
  given SelectionPolicies(
    cardPolicy = new UniformSelection[Card],
    planetPolicy = new UniformSelection[Planet],
    jokerPolicy = new UniformSelection[Joker]
  )
  val constraints = Seq(
    InitialHandWith(Seq(Card(Ace, Hearts), Card(Ace, Diamonds)), 1),
    JokerPackContains(JokerType.CleverJoker, 1),
    JokerPackContains(JokerType.CraftyJoker, 2),
    InitialHandWith(Seq(Card(Ace, Hearts), Card(Ace, Diamonds)), 2)
  )
  val seed = SeedFinder.findSeed(constraints)
  println(s"Found seed: $seed")
