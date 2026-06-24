package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlanetTest extends AnyFlatSpec, Matchers:

  val initialLevels: HandTypeLevels = HandTypeLevels.initial
  
  "Initial hand type level" should "be 1" in:
    Level.initial shouldBe 1

  "Initial hand type levels" should "all be the initial value" in:
    initialLevels.forall((_,level) => level == Level.initial)

  "Using a planet card 1 time" should "increase the level of the corresponding hand of 1" in:
    Planet.values.forall(p => p.use(initialLevels).forall((_,level) => level == 2))

  "Using a planet card n times" should "increase the level of the corresponding poker hand of n" in:
    val n = 5
    Planet.values.forall(p => (1 to n).foldLeft(initialLevels)((acc,_) => p.use(acc)).getOrElse(p.handType, Level.initial) == Level.initial+n)