package scalatro
package model.round

import model.round.Placeholders.Card

import cats.data.State

object RoundActions:
  type StateRound[A] = State[Round, A]

  def discardCardAndReplace(card: Card): State[Round, Unit] =
    def discardCard: State[Round, Unit] =
      State.modify(state => state.copy(hand = state.hand.filterNot(_ == card)))

    for
      _ <- discardCard
      _ <- drawCard
    yield ()

  def playCard(card: Card): State[Round, Unit] =
    def increaseScore: State[Round, Unit] =
      State.modify(state => state.copy(score = state.score + Score(card.value)))
    for
      _ <- increaseScore
      _ <- discardCardAndReplace(card)
    yield ()

  def drawCard: State[Round, Unit] =
    State.modify { state =>
      state.deck.headOption match
        case Some(card) =>
          state.copy(deck = state.deck.drop(1), hand = state.hand :+ card)
        case None => state
    }
