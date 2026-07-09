package scalatro
package model.round

import model.commons.Score.{Score, calculateScore}
import model.commons.{Card, Orderer, ScoreConfig}

import cats.data.State

/** A collection of methods for [[RoundState]] state changes */
object TurnActions:
  /** A convenience type alias to represent a [[State]] of [[RoundState]]
    *
    * @tparam A
    *   the type of the value returned after the state change
    */
  type TurnState[A] = State[RoundState, A]

  /** Updates the [[RoundState]] state after playing the given cards.
    *
    * The played cards are scored, removed from the hand, replaced by drawing
    * the same number of cards from the deck, and the number of remaining plays
    * is reduced by one.
    *
    * @param cards
    *   the cards to play
    * @param scoreConfig
    *   the configuration needed to compute the score of the hand
    * @return
    *   a [[State]] that applies the changes to the current [[RoundState]]
    */
  def playCards(
      cards: Seq[Card]
  )(using scoreConfig: ScoreConfig): TurnState[Unit] =
    for
      score = calculateScore(cards)
      _ <- increaseScore(score)
      _ <- removeCards(cards)
      _ <- drawCards(cards.size)
      _ <- decreaseRemainingPlays
    yield ()

  /** Updates the [[RoundState]] state after discarding the given cards.
    *
    * The discarded cards are removed from the hand, replaced by drawing the
    * same number of cards from the deck, and the number of remaining discards
    * is reduced by one.
    *
    * @param cards
    *   the cards to discard
    * @return
    *   a [[State]] that applies the changes to the current [[RoundState]]
    */
  def discardCards(cards: Seq[Card]): TurnState[Unit] =
    for
      _ <- removeCards(cards)
      _ <- drawCards(cards.size)
      _ <- decreaseRemainingDiscards
    yield ()

  /** Updates the [[RoundState]] state after ordering the cards in the hand.
    *
    * The new order of the cards is computed by a specified card orderer.
    *
    * @param ord
    *   the card orderer
    * @return
    *   a [[State]] that applies the changes to the current [[RoundState]]
    */
  def orderCards(using ord: Orderer[Card]): TurnState[Unit] =
    State.modify(state => state.modify(hand = ord.order(state.hand)))

  private def removeCards(cards: Seq[Card]): TurnState[Unit] =
    State.modify(state =>
      state.modify(hand = state.hand.filterNot(cards.contains))
    )

  private def increaseScore(delta: Score): TurnState[Unit] =
    State.modify(state => state.modify(score = state.score + delta))

  private def drawCards(n: Int): TurnState[Unit] =
    State.modify(state =>
      val (drawn, remaining) = state.deck.draw(n)
      state.modify(deck = remaining, hand = state.hand :++ drawn)
    )

  private def decreaseRemainingPlays: TurnState[Unit] =
    State.modify(state =>
      state.modify(remainingPlays = state.remainingPlays - 1)
    )

  private def decreaseRemainingDiscards: TurnState[Unit] =
    State.modify(state =>
      state.modify(remainingDiscards = state.remainingDiscards - 1)
    )
