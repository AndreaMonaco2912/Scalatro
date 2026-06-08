package scalatro
package model.game

import scala.util.Random
import model.commons.Deck

class Game(val seed: Long):
  private val rng: Random = Random(seed)
  given Random = rng
  val deck: Deck = Deck()
  def getShuffledDeck: Deck = deck.shuffle

object Game:
  def apply(seed: Long = Random.nextLong()): Game = new Game(seed)
