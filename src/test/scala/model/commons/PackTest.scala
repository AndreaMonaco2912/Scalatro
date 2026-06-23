package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class PackTest extends AnyFlatSpec, Matchers:
  given Random = Random(0L)

  "A pack with 3 cards" should "contain exactly 3 cards" in:
    CardsPack(3).items.length shouldBe 3

  "A pack with 0 cards" should "be empty" in:
    CardsPack(0).items shouldBe empty

  "A pack with negative size" should "throw an IllegalArgumentException" in:
    an[IllegalArgumentException] should be thrownBy CardsPack(-1)

  "A pack with 3 cards" should "contain only cards from the pool" in:
    val pack = CardsPack(3)
    pack.items.foreach(card => CardsPack.pool should contain(card))

  "A pack with an external pool" should "contain only cards from that pool" in:
    val pool = Suit.values.toSeq.map(Card(Rank.Two, _))
    val pack = CardsPack(3, pool)
    pack.items.foreach(card => pool should contain(card))

  "A pack with an empty external pool" should "be empty" in:
    CardsPack(3, Seq.empty).items shouldBe empty

  "A PlanetPack with 2 planets" should "contain exactly 2 planets" in :
    PlanetPack(2).items.length shouldBe 2
  
  "A PlanetPack" should "contain only planets" in :
    val pack = PlanetPack(5)
    pack.items.foreach(planet => PlanetPack.pool should contain(planet))