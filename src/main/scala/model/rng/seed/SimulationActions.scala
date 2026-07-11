package scalatro
package model.rng.seed

import model.commons.*
import model.game.GameState
import model.rng.seed.SimulationTypes.*
import model.rng.{ScalatroRng, SelectionPolicies}
import model.round.Hand
import model.shop.Shop

import cats.Monad
import cats.data.State

/** A collection of actions to run during the seed search simulation */
private[seed] object SimulationActions:
  private type SimStep[A] = State[GameState, A]

  /** Checks whether the given round satisfies the given constraints
    * @param constraints
    *   the constraints to check
    * @param simRound
    *   the round to check
    * @return
    *   `true` if all constraints are satisfied, `false` otherwise
    */
  def checkRound(
      constraints: Seq[SeedConstraint],
      simRound: SimRound
  ): Boolean = constraints.forall(_.isSatisfiedBy(simRound))

  /** Shuffles the deck and draws a hand
    * @return
    *   a [[State]] transition that shuffles the [[GameState]]'s deck and yields
    *   the drawn [[Hand]]
    */
  def shuffleDeckAndDraw(using ScalatroRng): SimStep[Hand] =
    State(s =>
      val shuffled = s.shuffleDeck
      (shuffled, shuffled.deck.draw(s.handInformation.handSize)._1)
    )

  /** Generates a shop
    * @return
    *   a [[State]] transition that generates a shop
    */
  def generateShop(using ScalatroRng): SimStep[Shop] =
    State.inspect { s =>
      given SelectionPolicies = s.selectionPolicies
      Shop.default(s.shopInformation)
    }

  /** Generates a [[WantedPackItems]] from the given constraints
    * @param constraints
    *   the constraints to use
    * @return
    *   the wanted pack
    */
  def wantedPackItems(constraints: Seq[SeedConstraint]): WantedPackItems =
    WantedPackItems(
      cards = constraints.collect { case CardPackContains(c, _) => c },
      jokers = constraints.collect { case JokerPackContains(j, _) => j },
      planets = constraints.collect { case PlanetPackContains(p, _) => p }
    )

  /** Picks cards from the packs according to the given constraints
    * @param constraints
    *   the constraints to use
    * @return
    *   a [[State]] transition that applies pick policies
    */
  def pickFromPacks(constraints: Seq[SeedConstraint]): SimStep[Unit] =
    State.modify { s =>
      val WantedPackItems(cards, jokers, planets) = wantedPackItems(constraints)
      cards.pickedBy(jokers.pickedBy(planets.pickedBy(s)))
    }

  /** Simulates a round
    * @return
    *   a [[State]] transition that simulates and yields a round
    */
  def simulateRound(using ScalatroRng): SimStep[SimRound] =
    for
      hand <- shuffleDeckAndDraw
      shop <- generateShop
      simRound = SimRound(hand, shop.cardPack, shop.jokerPack, shop.planetPack)
    yield simRound

  /** Advances the blind progression
    * @return
    *   a [[State]] transition that advances the blind progression
    */
  def advanceBlind(using ScalatroRng): SimStep[Unit] =
    State.modify(_.advanceBlind)

  /** Checks all rounds against the given constraints
    * @param constraints
    *   the constraints to check against
    * @return
    *   a [[State]] transition that checks all rounds against the given
    *   constraints and yields true if all constraints are satisfied
    */
  def checkAllRounds(
      constraints: Seq[SeedConstraint]
  )(using ScalatroRng): SimStep[Boolean] =
    Monad[SimStep].tailRecM(constraints) { remainingConstraints =>
      if remainingConstraints.isEmpty then State.pure(Right(true))
      else
        for
          currentRound <-
            State.inspect[GameState, Int](_.blindProgression.roundNum)
          simRound <- simulateRound
          (currentRoundConstraints, laterConstraints) =
            remainingConstraints.partition(_.round == currentRound)
          currentRoundOk = checkRound(currentRoundConstraints, simRound)
          result <-
            if !currentRoundOk then
              State.pure[GameState, Either[Seq[SeedConstraint], Boolean]](
                Right(false)
              )
            else
              for
                _ <- pickFromPacks(currentRoundConstraints)
                _ <- advanceBlind
              yield Left(laterConstraints)
        yield result
    }

  /** Runs a game simulation to check if all contraints are satisfied
    * @param constraints
    *   the constraints to check against
    * @return
    *   `true` if all constraints are satisfied, `false` otherwise
    */
  def satisfiesConstraints(
      constraints: Seq[SeedConstraint]
  )(using ScalatroRng): Boolean =
    checkAllRounds(constraints)
      .runA(GameState.initial)
      .value
