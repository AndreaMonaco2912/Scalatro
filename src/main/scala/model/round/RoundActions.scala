package scalatro
package model.round

import model.commons.Card

import cats.data.State

object RoundActions:
  type StateRound[A] = State[Round, A]

  def discardCardAndReplace(card: Card): StateRound[Unit] =
    def discardCard: State[Round, Unit] =
      State.modify(state => state.copy(hand = state.hand.filterNot(_ == card)))

    for
      _ <- discardCard
      _ <- drawCard
    yield ()

  def playCard(card: Card): StateRound[Unit] =
    def increaseScore: State[Round, Unit] =
      State.modify(state => state.copy(score = state.score + Score(card.rank)))
    for
      _ <- increaseScore
      _ <- discardCardAndReplace(card)
    yield ()

  def drawCard: StateRound[Unit] =
    State.modify { state =>
      val (drawn, remaining) = state.deck.draw(1)
      drawn.headOption match
        case Some(card) =>
          state.copy(deck = remaining, hand = state.hand :+ card)
        case None => state
    }
