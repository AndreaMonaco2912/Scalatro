package scalatro
package model.game

import model.commons.*
import model.shop.ShopSelection

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

  def selectItem(selection: ShopSelection): GameState = selection match
    case ShopSelection.CardSelected(card)     => copy(deck = deck.add(card))
    case ShopSelection.PlanetSelected(planet) =>
      copy(levels = planet.use(levels))
    case ShopSelection.JokerSelected(joker) => copy(jokers = jokers :+ joker)

case class HandInformation(handSize: Int, handNum: Int, discardNum: Int)

case class ShopInformation(
    deck: Deck,
    levels: HandTypeLevels,
    jokers: Seq[Joker]
)

object GameState:
  private val initialHandSize = 8
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
