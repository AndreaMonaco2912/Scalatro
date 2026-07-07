package scalatro
package model.rng.seed

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.Types.Seed

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SeedFinderSpec extends AnyFlatSpec, Matchers:

  "SeedFinder.findSeed" should "return the fallback seed when maxAttempts is zero" in:
    val seed = SeedFinder.findSeed(
      constraints = Seq.empty,
      maxAttempts = 0
    )

    seed shouldBe Seed(0L)

  it should "return a non-fallback seed when unconstrained search has one attempt" in:
    val seed = SeedFinder.findSeed(
      constraints = Seq.empty,
      maxAttempts = 1
    )

    seed should not be Seed(0L)

  it should "return the fallback seed when no attempted seed satisfies the constraints" in:
    val impossibleConstraint =
      InitialHandWithCards(Seq(Card(Ace, Spades), Card(Ace, Spades)), 1)

    val seed = SeedFinder.findSeed(
      constraints = Seq(impossibleConstraint),
      maxAttempts = 3
    )

    seed shouldBe Seed(0L)
