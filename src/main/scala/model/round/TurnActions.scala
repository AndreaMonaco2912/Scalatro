package scalatro
package model.round

import model.commons.Score.{Score, calculateHandScore}
import model.commons.{Card, CardOrderer, HandScoreCalculator, ScoreConfig}

import cats.data.State

object TurnActions:
  type TurnState[A] = State[Round, A]

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

  def orderCards(using ord: CardOrderer): TurnState[Unit] =
    State.modify(state => state.modify(hand = ord.order(state.hand)))

  def discardCards(cards: Seq[Card]): TurnState[Unit] =
    for
      _ <- removeCards(cards)
      _ <- drawCards(cards.size)
      _ <- decreaseRemainingDiscards
    yield ()

  def playCards(using
      scoreConfig : ScoreConfig
  )(cards: Seq[Card]): TurnState[Unit] =
    for
      score = calculateHandScore(cards)
      _ <- increaseScore(score)
      _ <- removeCards(cards)
      _ <- drawCards(cards.size)
      _ <- decreaseRemainingPlays
    yield ()
