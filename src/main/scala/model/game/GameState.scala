package scalatro
package model.game

import scala.util.Random
import model.commons.Deck

import cats.data.State

case class GameState(deck: Deck, blind: Blind)

object GameState:
  def initial: GameState =
    GameState(Deck(), Blind.first)

  def shuffleDeck(using Random): State[GameState, Unit] =
    State.modify[GameState](s => s.copy(deck = s.deck.shuffle))

  val advanceBlind: State[GameState, Unit] =
    State.modify[GameState](s => s.copy(blind = s.blind.next))
