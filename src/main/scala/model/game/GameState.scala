package scalatro
package model.game

import model.commons.*
import model.rng.{ScalatroRng, SelectionPolicies, SelectionPolicy}

/** The state of a game, carried across the whole match.
  * @param handInformation
  *   the hand size and the number of plays and discards per round
  * @param deck
  *   the full deck owned by the player
  * @param blindProgression
  *   the progression of blinds
  * @param jokers
  *   the owned jokers, in order
  * @param levels
  *   the levels of the hand types
  * @param selectionPolicies
  *   the selection policies used when generating packs
  */
case class GameState(
    handInformation: HandInformation,
    deck: Deck,
    blindProgression: BlindProgression,
    jokers: Seq[Joker],
    levels: HandTypeLevels,
    selectionPolicies: SelectionPolicies
):
  /** Shuffles the deck.
    *
    * @return
    *   the game state with the shuffled deck
    */
  def shuffleDeck(using ScalatroRng): GameState =
    this.copy(deck = deck.sort.shuffle)

  /** Advances to the next blind.
    *
    * @return
    *   the game state with the advanced blind progression
    */
  def advanceBlind(using ScalatroRng): GameState =
    this.copy(blindProgression = blindProgression.next)

  /** The score configuration for the current blind, jokers and levels.
    *
    * @return
    *   the score configuration
    */
  def scoreConfig: ScoreConfig =
    ScoreConfig(
      jokers,
      levels,
      BasicHandScoreCalculator,
      blindProgression.blind
    )

  /** Adds a card to the deck.
    *
    * @param card
    *   the card to add
    * @return
    *   the game state with the updated Deck
    */
  def addCard(card: Card): GameState = copy(deck = deck.add(card))

  /** Adds a joker after the owned ones.
    *
    * @param joker
    *   the joker to add
    * @return
    *   the game state with updated Jocker List
    */
  def addJoker(joker: Joker): GameState = copy(jokers = jokers :+ joker)

  /** Uses a planet card, leveling up its associated hand type.
    *
    * @param planet
    *   the planet to use
    * @return
    *   the game state with updated hand level
    */
  def usePlanet(planet: Planet): GameState = copy(levels = planet.use(levels))

/** The hand configuration of a run.
  * @param handSize
  *   the number of cards held in hand
  * @param handNum
  *   the number of plays per round
  * @param discardNum
  *   the number of discards per round
  */
case class HandInformation(handSize: Int, handNum: Int, discardNum: Int)

object GameState:
  val initialHandSize = 8
  val initialHandNum = 4
  val initialDiscardNum = 3

  private def initialHand: HandInformation =
    HandInformation(initialHandSize, initialHandNum, initialDiscardNum)

  /** The initial game state of a new run.
    *
    * @return
    *   the initial game state
    */
  def initial: GameState =
    GameState(
      initialHand,
      Deck(),
      BlindProgression.first,
      Seq.empty,
      HandTypeLevels.initial,
      SelectionPolicies.default
    )

type GameStateModification = Modification[GameState]

object GameStateModification:
  case class SetCardPolicy(policy: SelectionPolicy[Card])
      extends GameStateModification:
    override def apply(value: GameState): GameState =
      value.copy(selectionPolicies =
        value.selectionPolicies.copy(cardPolicy = policy)
      )
  case class SetJokerPolicy(policy: SelectionPolicy[Joker])
      extends GameStateModification:
    override def apply(value: GameState): GameState =
      value.copy(selectionPolicies =
        value.selectionPolicies.copy(jokerPolicy = policy)
      )
  case class SetPlanetPolicy(policy: SelectionPolicy[Planet])
      extends GameStateModification:
    override def apply(value: GameState): GameState =
      value.copy(selectionPolicies =
        value.selectionPolicies.copy(planetPolicy = policy)
      )
