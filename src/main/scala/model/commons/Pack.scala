package scalatro
package model.commons

import scala.util.Random

case class Pack(cards: Seq[Card])

trait PackFactory[A]:
  def pool: Seq[A]
  def apply(n: Int): Pack
  def apply(n: Int, externalPool: Seq[A]): Pack

object CardsPack extends PackFactory[Card]:
  val pool: Seq[Card] =
    for
      suit <- Suit.values.toSeq
      rank <- Rank.values.toSeq
    yield Card(rank, suit)

  def apply(n: Int): Pack =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(Random.shuffle(pool).take(n))
  def apply(n: Int, externalPool: Seq[Card]): Pack =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(Random.shuffle(externalPool).take(n))
