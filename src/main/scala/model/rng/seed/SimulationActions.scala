package scalatro
package model.rng.seed

import model.commons.*
import model.rng.ScalatroRng
import model.rng.Types.Pool
import model.rng.seed.SimulationTypes.{PoolUpdate, SimRound, SimState, WantedPackItems}
import model.round.Hand

import cats.data.State

private[seed] object SimulationActions:
  type SimStep[A] = State[SimState, A]

  def checkRound(
      constraints: Seq[SeedConstraint],
      simRound: SimRound
  ): Boolean = constraints.forall(_.isSatisfiedBy(simRound))

  def shuffleDeckAndDraw(handSize: Int)(using rng: ScalatroRng): SimStep[Hand] =
    State.inspect { s =>
      val ordered = Deck(s.cardPool.values).sort.cards // default deck sorting
      rng.shuffle(ordered).take(handSize)
    }

  def generatePacks(packsSize: Int)(using
      rng: ScalatroRng,
      policies: SelectionPolicies
  ): SimStep[(Pack[Card], Pack[Joker], Pack[Planet])] =
    State.inspect { s =>
      (
        Pack(rng.draw(s.cardPool, packsSize)(using policies.cardPolicy)),
        Pack(rng.draw(s.jokerPool, packsSize)(using policies.jokerPolicy)),
        Pack(rng.draw(s.planetPool, packsSize)(using policies.planetPolicy))
      )
    }

  def updatePools(constraints: Seq[SeedConstraint]): SimStep[Unit] =
    val wanted = wantedPackItems(constraints)
    State.modify { s =>
      s.copy(
        cardPool = wanted.cards.foldLeft(s.cardPool) { (pool, card) =>
          summon[PoolUpdate[Card]].onPicked(card, pool)
        },
        jokerPool = wanted.jokers.foldLeft(s.jokerPool) { (pool, joker) =>
          summon[PoolUpdate[Joker]].onPicked(joker, pool)
        },
        planetPool = wanted.planets.foldLeft(s.planetPool) { (pool, planet) =>
          summon[PoolUpdate[Planet]].onPicked(planet, pool)
        }
      )
    }

  def wantedPackItems(constraints: Seq[SeedConstraint]): WantedPackItems =
    WantedPackItems(
      cards = constraints.collect { case CardPackContains(c, _) => c },
      jokers = constraints.collect { case JokerPackContains(j, _) => j },
      planets = constraints.collect { case PlanetPackContains(p, _) => p }
    )

  /*ef updatePools(constraints: Seq[SeedConstraint]): SimStep[Unit] =
    State.modify { s =>
      val wanted = wantedPackItems(constraints)
      s.copy(
        cardPool = s.cardPool - Pool(wanted.cards),
        jokerPool = s.jokerPool - Pool(wanted.jokers),
        planetPool = s.planetPool - Pool(wanted.planets)
      )
    }*/

  def playRound(handSize: Int, packSize: Int)(using
      ScalatroRng,
      SelectionPolicies
  ): SimStep[SimRound] =
    for
      hand <- shuffleDeckAndDraw(handSize)
      (cardPack, jokerPack, planetPack) <- generatePacks(packSize)
      simRound = SimRound(hand, cardPack, jokerPack, planetPack)
    yield simRound

  def checkAllRounds(
      constraints: Seq[SeedConstraint],
      handSize: Int,
      packSize: Int
  )(using
      ScalatroRng,
      SelectionPolicies
  ): SimStep[Boolean] =
    val lastRound = constraints.map(_.round).maxOption.getOrElse(0)

    val computation = (1 to lastRound).foldLeft(
      State.pure[SimState, (Boolean, Seq[SeedConstraint])]((true, constraints))
    ) { (acc, round) =>
      for
        (okSoFar, constraints) <- acc
        simRound <- playRound(handSize, packSize)
        (actualConstraints, remainingConstraints) =
          constraints.partition(_.round == round)
        roundOk = checkRound(actualConstraints, simRound)
        _ <- updatePools(actualConstraints)
      yield (okSoFar && roundOk, remainingConstraints)
    }

    computation.map(_._1)

  def satisfiesConstraints(
      constraints: Seq[SeedConstraint],
      handSize: Int = 5,
      packSize: Int = 3
  )(using ScalatroRng, SelectionPolicies): Boolean =
    checkAllRounds(constraints, handSize, packSize)
      .runA(SimState.initial(constraints))
      .value
