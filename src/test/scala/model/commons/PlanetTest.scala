package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Inspectors.forAll

class PlanetTest extends AnyFlatSpec, Matchers:

  val initialLevels: HandTypeLevels = HandTypeLevels.initial

  "Initial hand type level" should "be 1" in:
    Level.initial shouldBe 1

  "Initial hand type levels" should "all be the initial value" in:
    forAll(initialLevels)((_, level) => level shouldBe Level.initial)

  "Using a planet card 1 time" should "increase the level of the corresponding hand of 1" in:
    forAll(Planet.values)(p =>
      p.use(initialLevels)
        .getOrElse(p.handType, Level.initial) shouldBe Level.initial + 1
    )

  "Using a planet card n times" should "increase the level of the corresponding poker hand of n" in:
    val n = 5
    forAll(Planet.values)(p =>
      p.use(initialLevels, n)
        .getOrElse(p.handType, Level.initial) shouldBe Level.initial + n
    )
