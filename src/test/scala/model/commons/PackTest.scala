package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class PackTest extends AnyFlatSpec, Matchers:
  given Random = Random(0L)
  "A pack with 3 cards" should "contain exactly 3 cards" in:
    val plainCardsPack = CardsPack(3)
    plainCardsPack.cards.length shouldBe 3

  "A pack that take an external pool" should "contain only cards from that pool" in:
    val pool =
      for suit <- Suit.values.toSeq
      yield Card(Rank.Two, suit)
    val partialPoolPack = CardsPack(3, pool)
    pool.contains(partialPoolPack.cards)
