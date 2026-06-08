package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameTest extends AnyFlatSpec, Matchers:
  val seed = 0L

  "A Game" should "store the seed it was created with" in:
    Game(seed).seed shouldBe seed

  it should "pick a random seed when none is given" in:
    Game().seed should not equal Game().seed

  it should "be reproducible: same seed yields the same shuffle" in:
    Game(seed).getShuffledDeck shouldBe Game(seed).getShuffledDeck

  it should "yield different shuffles for different seeds" in:
    Game(seed).getShuffledDeck should not equal Game(seed + 1).getShuffledDeck
