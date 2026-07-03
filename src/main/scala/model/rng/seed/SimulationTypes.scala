package scalatro
package model.rng.seed

import model.commons.*
import model.rng.SelectionPolicy
import model.rng.Types.Pool
import model.round.Hand

private[seed] object SimulationTypes:
  trait PoolUpdate[A]:
    def onPicked(item: A, pool: Pool[A]): Pool[A]

  given PoolUpdate[Card] with
    def onPicked(item: Card, pool: Pool[Card]): Pool[Card] = pool

  given PoolUpdate[Joker] with
    def onPicked(item: Joker, pool: Pool[Joker]): Pool[Joker] = pool - item

  given PoolUpdate[Planet] with
    def onPicked(item: Planet, pool: Pool[Planet]): Pool[Planet] = pool

  case class SimState(
      constraints: Seq[SeedConstraint],
      cardPool: Pool[Card],
      jokerPool: Pool[Joker],
      planetPool: Pool[Planet]
  )

  object SimState:
    def initial(constraints: Seq[SeedConstraint])(using
        policies: SelectionPolicies
    ): SimState =
      given SelectionPolicy[Card] = policies.cardPolicy
      given SelectionPolicy[Planet] = policies.planetPolicy
      given SelectionPolicy[Joker] = policies.jokerPolicy
      SimState(
        constraints,
        CardsPack().pool,
        JokerPack().pool,
        PlanetPack().pool
      )

  case class SimRound(
      hand: Hand,
      cardPack: Pack[Card],
      jokerPack: Pack[Joker],
      planetPack: Pack[Planet]
  )

  case class WantedPackItems(
      cards: Seq[Card],
      jokers: Seq[Joker],
      planets: Seq[Planet]
  )
