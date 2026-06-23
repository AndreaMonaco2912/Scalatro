package scalatro
package model.game

import scala.util.Random
import model.commons.Deck
import model.commons.Joker
import model.commons.HandType

case class GameState(deck: Deck, blind: Blind, jokers : Seq[Joker], levels : Map[HandType,Int]):
  def shuffleDeck(using Random): GameState =
    this.copy(deck = deck.shuffle)

  def advanceBlind: GameState =
    this.copy(blind = blind.next)

object GameState:
  def initial: GameState =
    GameState(Deck(), Blind.first, Seq.empty, HandType.values.map(ht => ht -> 1).toMap)