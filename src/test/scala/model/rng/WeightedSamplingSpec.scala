package scalatro
package model.rng

import model.rng.ScalatroRng.WeightedSampling.*

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

/** A test spec for [[WeightedSampling]] */
class WeightedSamplingSpec extends AnyFlatSpec, Matchers, MockFactory:
  "selectWithoutReplacement" should "select elements according to mocked random and weights" in:
    val elems = IndexedSeq("a", "b", "c")
    val weightOf: String => Weight = {
      case "a" => Weight(1.0)
      case "b" => Weight(2.0)
      case "c" => Weight(0.0)
    }

    val rnd = mock[Random]
    (rnd.nextDouble _).expects().returning(0.9).repeat(elems.length)

    val selected = selectWithoutReplacement(elems, 3, weightOf, rnd)
    selected should contain theSameElementsInOrderAs Seq("b", "a", "c")

  it should "select elements with different nextDouble results when all have same weight" in:
    val elems = IndexedSeq("x", "y", "z")
    val rnd = mock[Random]
    (rnd.nextDouble _).expects().returning(0.3).once()
    (rnd.nextDouble _).expects().returning(0.7).once()
    (rnd.nextDouble _).expects().returning(0.5).once()
    val selected = selectWithoutReplacement(elems, 3, _ => Weight(1.0), rnd)
    selected.size shouldBe 3
    selected should contain theSameElementsInOrderAs Seq("y", "z", "x")

  it should "return empty when amount <= 0" in:
    val elems = IndexedSeq("x", "y")
    val rnd = mock[Random]
    (rnd.nextDouble _).expects().returning(0.9).repeat(elems.length)
    val selected = selectWithoutReplacement(elems, 0, _ => Weight(1.0), rnd)
    selected shouldBe empty

  it should "clamp amount to size of elements" in:
    val elems = IndexedSeq("p", "q")
    val rnd = mock[Random]
    (rnd.nextDouble _).expects().returning(0.5).repeat(elems.length)
    val selected = selectWithoutReplacement(elems, 5, _ => Weight(1.0), rnd)
    selected.size shouldBe 2
    selected.toSet shouldBe elems.toSet
