package scalatro
package model.rng.seed

import model.commons.*
import model.game.GameState
import model.round.Hand

private[seed] object SimulationTypes:
  trait PickFromPackPolicy[A]:
    def onPicked(item: A, gs: GameState): GameState

  extension [A](items: Seq[A])
    def pickedBy(gs: GameState)(using
        policy: PickFromPackPolicy[A]
    ): GameState =
      items.foldLeft(gs)((current, item) => policy.onPicked(item, current))

  given PickFromPackPolicy[Card] with
    def onPicked(card: Card, gs: GameState): GameState = gs.addCard(card)

  given PickFromPackPolicy[Joker] with
    def onPicked(joker: Joker, gs: GameState): GameState = gs.addJoker(joker)

  given PickFromPackPolicy[Planet] with
    def onPicked(planet: Planet, gs: GameState): GameState =
      gs.usePlanet(planet)

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
