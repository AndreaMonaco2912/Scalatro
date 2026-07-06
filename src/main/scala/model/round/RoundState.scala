package scalatro
package model.round

import model.commons.Score.Score
import model.commons.{Card, Deck, Modification, Score}
import model.game.GameState
//TODO valutare se mettere maxSize = 5 e minSize = 1
/** The game's hand: the collection of cards the player can choose from */
type Hand = Seq[Card]

/** A trait for indicating the current state of the round */
trait RoundState:
  /** The current score */
  def score: Score

  /** The current hand */
  def hand: Hand

  /** The current deck */
  def deck: Deck

  /** The remaining plays */
  def remainingPlays: Int

  /** The remaining discards */
  def remainingDiscards: Int

  /** The game state of this round */
  def gameState: GameState

  /** Modify some parameters of this [[RoundState]], returning a new instance
    * with the updated values
    *
    * @param score
    *   the new score
    * @param hand
    *   the new hand
    * @param deck
    *   the new deck
    * @param remainingPlays
    *   the new remaining plays
    * @param remainingDiscards
    *   the new remaining discards
    * @return
    *   the modified round state
    */
  def modify(
      score: Score = score,
      hand: Hand = hand,
      deck: Deck = deck,
      remainingPlays: Int = remainingPlays,
      remainingDiscards: Int = remainingDiscards
  ): RoundState

  /** Checks if the round is considered finished
    * @return
    *   `true` if the round is finished, `false` otherwise
    */
  def isFinished: Boolean

object RoundState:
  /** Creates a new [[RoundState]]
    *
    * @param score
    *   the score
    * @param hand
    *   the hand
    * @param deck
    *   the deck
    * @param gameState
    *   the game state
    * @return
    *   the round state
    */
  def apply(
      score: Score,
      hand: Hand,
      deck: Deck,
      gameState: GameState
  ): RoundState =
    RoundStateImpl(
      score,
      hand,
      deck,
      gameState.handInformation.handNum,
      gameState.handInformation.discardNum,
      gameState
    )

  /** Creates a new [[RoundState]], initialized with the settings in GameState
    *
    * @param gameState
    *   the game state
    * @return
    *   the round state
    */
  def apply(gameState: GameState): RoundState =
    val (hand, deck) = gameState.deck.draw(gameState.handInformation.handSize)
    RoundStateImpl(
      Score.zero,
      hand,
      deck,
      gameState.handInformation.handNum,
      gameState.handInformation.discardNum,
      gameState
    )

  private case class RoundStateImpl(
      score: Score,
      hand: Hand,
      deck: Deck,
      remainingPlays: Int,
      remainingDiscards: Int,
      gameState: GameState
  ) extends RoundState:
    override def modify(
        score: Score,
        hand: Hand,
        deck: Deck,
        remainingPlays: Int,
        remainingDiscards: Int
    ): RoundState =
      RoundStateImpl(
        score,
        hand,
        deck,
        remainingPlays,
        remainingDiscards,
        gameState
      )

    override def isFinished: Boolean =
      gameState.blind.isBeaten(score) || remainingPlays == 0

trait RoundStateModification extends Modification:
  type T = RoundState

object RoundStateModification:
  case class IncreaseHandsRemaining(n : Int) extends RoundStateModification:
    override def apply(value: RoundState): RoundState = value.modify(remainingPlays = value.remainingPlays + n)
  case class IncreaseDiscardsRemaining(n : Int) extends RoundStateModification:
    override def apply(value: RoundState): RoundState = value.modify(remainingDiscards = value.remainingDiscards + n)