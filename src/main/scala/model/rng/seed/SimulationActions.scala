package scalatro
package model.rng.seed

import model.commons.*
import model.game.GameState
import model.rng.ScalatroRng
import model.rng.seed.SimulationTypes.*
import model.round.Hand
import model.shop.Shop

import cats.Monad
import cats.data.State

private[seed] object SimulationActions:
  type SimStep[A] = State[GameState, A]

  def checkRound(
      constraints: Seq[SeedConstraint],
      simRound: SimRound
  ): Boolean = constraints.forall(_.isSatisfiedBy(simRound))

  def shuffleDeckAndDraw(using rng: ScalatroRng): SimStep[Hand] = {
    State(s =>
      val shuffled = s.shuffleDeck
      (shuffled, shuffled.deck.draw(s.handInformation.handSize)._1)
    )
  }

  def generateShop(using
      rng: ScalatroRng
  ): SimStep[Shop] =
    State.inspect { s =>
      given SelectionPolicies = s.selectionPolicies
      Shop.default(s.shopInformation)(using rng)
    }

  def wantedPackItems(constraints: Seq[SeedConstraint]): WantedPackItems =
    WantedPackItems(
      cards = constraints.collect { case CardPackContains(c, _) => c },
      jokers = constraints.collect { case JokerPackContains(j, _) => j },
      planets = constraints.collect { case PlanetPackContains(p, _) => p }
    )

  /*def updatePools(constraints: Seq[SeedConstraint]): SimStep[Unit] =
    State.modify { s =>
      val wanted = wantedPackItems(constraints)
      s.copy(
        cardPool = s.cardPool - Pool(wanted.cards),
        jokerPool = s.jokerPool - Pool(wanted.jokers),
        planetPool = s.planetPool - Pool(wanted.planets)
      )
    }*/

  def simulateRound(using ScalatroRng): SimStep[SimRound] =
    for
      hand <- shuffleDeckAndDraw
      shop <- generateShop
      simRound = SimRound(hand, shop.cardPack, shop.jokerPack, shop.planetPack)
    yield simRound

  def advanceBlind: SimStep[Unit] = State.modify(_.advanceBlind)

  def checkAllRounds(
      constraints: Seq[SeedConstraint]
  )(using ScalatroRng): SimStep[Boolean] =
    Monad[SimStep].tailRecM(constraints) { remainingConstraints =>
      if remainingConstraints.isEmpty then State.pure(Right(true))
      else
        for
          currentRound <- State.inspect[GameState, Int](
            _.blindProgression.roundNum
          )
          simRound <- simulateRound
          (currentRoundConstraints, laterConstraints) =
            remainingConstraints.partition(_.round == currentRound)
          currentRoundOk = checkRound(currentRoundConstraints, simRound)
          _ <- advanceBlind
        yield
          if !currentRoundOk then Right(false)
          else Left(laterConstraints)
    }

  def satisfiesConstraints(
      constraints: Seq[SeedConstraint]
  )(using ScalatroRng): Boolean =
    checkAllRounds(constraints)
      .runA(GameState.initial)
      .value
