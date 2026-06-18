package scalatro
package model.game

import scala.util.Random
import model.commons.Deck

case class GameState(deck: Deck, blind: Blind):
  def shuffleDeck(using Random): GameState =
    this.copy(deck = deck.shuffle)

  def advanceBlind: GameState =
    this.copy(blind = blind.next)

object GameState:
  def initial: GameState =
    GameState(Deck(), Blind.first)
