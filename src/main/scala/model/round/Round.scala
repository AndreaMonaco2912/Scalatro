package scalatro
package model.round

import model.commons.Score.Score
import model.commons.{Card, CardOrderer, Deck}
import model.game.{Blind, GameState}

/** The game's hand: the collection of cards the player can choose from */
type Hand = Seq[Card]

/** An action the user can perform during the round */
enum RoundAction:
  /** The action of playing a group of cards
    * @param cards
    *   the cards to play
    */
  case PlayCards(cards: Seq[Card])

  /** The action of discarding a group of cards
    * @param cards
    *   the cards to discard
    */
  case DiscardCards(cards: Seq[Card])

  /** The action of ordering cards according to a specific [[CardOrderer]]
    * @param orderer
    *   the card orderer
    */
  case OrderHand(orderer: CardOrderer)

/** A trait for indicating the current state of the round */
trait Round:
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

  /** The blind of this round */
  def blind: Blind

  /** @param score
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
    *   the modified round
    */
  def modify(
      score: Score = score,
      hand: Hand = hand,
      deck: Deck = deck,
      remainingPlays: Int = remainingPlays,
      remainingDiscards: Int = remainingDiscards
  ): Round

  /** Checks if the round is considered finished
    * @return
    *   `true` if the round is finished, `false` otherwise
    */
  def isFinished: Boolean

object Round:
  /** Creates a new [[Round]] using the default implementation
    * @param score
    *   the score
    * @param hand
    *   the hand
    * @param deck
    *   the deck
    * @param blind
    *   the blind
    * @return
    *   the round
    */
  def apply(score: Score, hand: Hand, deck: Deck, gs: GameState): Round =//TODO round should be created taking here only GameState, not in the Controller.scala
    RoundImpl(score, hand, deck, gs.handInformation.handNum, gs.handInformation.discardNum, gs.blind)

  private case class RoundImpl(
      score: Score,
      hand: Hand,
      deck: Deck,
      remainingPlays: Int,
      remainingDiscards: Int,
      blind: Blind
  ) extends Round:
    override def modify(
        score: Score,
        hand: Hand,
        deck: Deck,
        remainingPlays: Int,
        remainingDiscards: Int
    ): Round =
      RoundImpl(score, hand, deck, remainingPlays, remainingDiscards, blind)

    override def isFinished: Boolean =
      blind.isBeaten(score) || remainingPlays == 0
