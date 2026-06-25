package scalatro
package model.game

import scala.util.Random
import model.commons.Deck
import model.commons.Joker
import model.commons.HandType
import model.commons.HandTypeLevels
import model.commons.JokerType.*

case class GameState(
    handInformation: HandInformation,
    deck: Deck,
    blind: Blind,
    jokers: Seq[Joker],
    levels: HandTypeLevels
):
  def shuffleDeck(using Random): GameState =
    this.copy(deck = deck.shuffle)

  def advanceBlind: GameState =
    this.copy(blind = blind.next)

case class HandInformation(handSize: Int, handNum: Int, discardNum: Int)

object GameState:
  private val initialHandSize = 5
  private val initialHandNum = 4
  private val initialDiscardNum = 3

  private def initialHand: HandInformation =
    HandInformation(initialHandSize, initialHandNum, initialDiscardNum)

  def initial: GameState =
    GameState(
      initialHand,
      Deck(),
      Blind.first,
      Seq.empty,
      HandTypeLevels.initial
    )
