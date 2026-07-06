package scalatro
package model.rng.seed

import model.commons.*
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
