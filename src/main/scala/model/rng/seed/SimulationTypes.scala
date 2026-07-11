package scalatro
package model.rng.seed

import model.commons.*
import model.game.GameState
import model.round.Hand

/** A collection of types used during seed search simulation */
private[seed] object SimulationTypes:
  /** A policy for picking items from a pack
    * @tparam A
    *   the type of items to pick from the pack
    */
  trait PickFromPackPolicy[A]:
    /** Modifies the game state according to some criteria
      * @param item
      *   the item to pick from the pack
      * @param gs
      *   the current game state
      * @return
      *   the modified game state
      */
    def onPicked(item: A, gs: GameState): GameState

  extension [A](items: Seq[A])
    /** Applies the given policy to each item in the sequence, simulating the
      * picking of all of them
      * @param gs
      *   the current game state
      * @param policy
      *   the policy to apply to each item
      * @return
      *   the modified game state
      */
    def pickedBy(gs: GameState)(using
        policy: PickFromPackPolicy[A]
    ): GameState =
      items.foldLeft(gs)((current, item) => policy.onPicked(item, current))

  /** The policy for picking cards from a pack */
  given PickFromPackPolicy[Card] with
    def onPicked(card: Card, gs: GameState): GameState = gs.addCard(card)

  /** The policy for picking jokers from a pack */
  given PickFromPackPolicy[Joker] with
    def onPicked(joker: Joker, gs: GameState): GameState = gs.addJoker(joker)

  /** The policy for picking planets from a pack */
  given PickFromPackPolicy[Planet] with
    def onPicked(planet: Planet, gs: GameState): GameState =
      gs.usePlanet(planet)

  /** A class representing the result of a simulated round
    * @param hand
    *   the hand at the start of the round
    * @param cardPack
    *   the pack of cards at the end of the round
    * @param jokerPack
    *   the pack of jokers at the end of the round
    * @param planetPack
    *   the pack of planets at the end of the round
    */
  case class SimRound(
      hand: Hand,
      cardPack: Pack[Card],
      jokerPack: Pack[Joker],
      planetPack: Pack[Planet]
  )

  /** A class representing the items that are wanted from a shop
    * @param cards
    *   the cards
    * @param jokers
    *   the jokers
    * @param planets
    *   the planets
    */
  case class WantedPackItems(
      cards: Seq[Card],
      jokers: Seq[Joker],
      planets: Seq[Planet]
  )
