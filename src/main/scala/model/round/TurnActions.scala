package scalatro
package model.round

import model.commons.{Card, HandScoreCalculator}
import model.commons.Score.Score
import model.commons.Score.calculateScore

import cats.data.State

object TurnActions:
  type TurnState[A] = State[Round, A]

//  def calculateScore(cards: Seq[Card]): Score.Score = Score(cards.map(_.rank).sum)

  def removeCards(cards: Seq[Card]): TurnState[Unit] =
    State.modify(state =>
      state.copy(hand = state.hand.filterNot(cards.contains))
    )

  def increaseScore(delta: Score): TurnState[Unit] =
    State.modify(state => state.copy(score = state.score + delta))

  def drawCards(n: Int): TurnState[Unit] =
    State.modify(state =>
      val (drawn, remaining) = state.deck.draw(n)
      state.copy(deck = remaining, hand = state.hand :++ drawn)
    )

  def discardCards(cards: Seq[Card]): TurnState[Unit] =
    for
      _ <- removeCards(cards)
      _ <- drawCards(cards.size)
    yield ()

  def playCards(using calculator : HandScoreCalculator)(cards: Seq[Card]): TurnState[Unit] =
    for
      score = calculateScore(cards)
      _ <- increaseScore(score)
      _ <- discardCards(cards)
    yield ()
