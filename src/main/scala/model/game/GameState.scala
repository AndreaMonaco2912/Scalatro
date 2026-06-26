package scalatro
package model.game

import model.commons.*

import scala.util.Random

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

  def scoreConfig: ScoreConfig =
    ScoreConfig(jokers, levels, BasicHandScoreCalculator)

  def shopInformation: ShopInformation =
    ShopInformation(deck, levels, jokers)

case class HandInformation(handSize: Int, handNum: Int, discardNum: Int)

case class ShopInformation(
    deck: Deck,
    levels: HandTypeLevels,
    jokers: Seq[Joker]
)

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
