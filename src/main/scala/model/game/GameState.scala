package scalatro
package model.game

import model.commons.*
import model.rng.ScalatroRng

import scala.util.Random

case class GameState(
    handInformation: HandInformation,
    deck: Deck,
    blind: Blind,
    jokers: Seq[Joker],
    levels: HandTypeLevels
):
  def shuffleDeck(using ScalatroRng): GameState =
    this.copy(deck = deck.shuffle)

  def advanceBlind: GameState =
    this.copy(blind = blind.next)

  def scoreConfig: ScoreConfig =
    ScoreConfig(jokers, levels, BasicHandScoreCalculator)

  def shopInformation: ShopInformation =
    ShopInformation(deck, levels, jokers)

  def addCard(card: Card): GameState = copy(deck = deck.add(card))

  def addJoker(joker: Joker): GameState = copy(jokers = jokers :+ joker)

  def usePlanet(planet: Planet): GameState = copy(levels = planet.use(levels))

case class HandInformation(handSize: Int, handNum: Int, discardNum: Int)

case class ShopInformation(
    deck: Deck,
    levels: HandTypeLevels,
    jokers: Seq[Joker]
)

object GameState:
  val initialHandSize = 8
  val initialHandNum = 4
  val initialDiscardNum = 3

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
