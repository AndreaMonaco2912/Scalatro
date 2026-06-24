package scalatro
package model.commons

import scala.util.Random

case class Pack[A](items: Seq[A])

trait PackFactory[A]:
  def pool: Seq[A]
  def apply(n: Int)(using rng: Random): Pack[A] =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(rng.shuffle(pool).take(n))
  def apply(n: Int, externalPool: Seq[A])(using rng: Random): Pack[A] =//TODO a black list would be better then a white list (pool - externalPool)
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(rng.shuffle(externalPool).take(n))
  //TODO: add a factory for a small pack with 3 items

object CardsPack extends PackFactory[Card]:
  val pool: Seq[Card] =
    for
      suit <- Suit.values.toSeq
      rank <- Rank.values.toSeq
    yield Card(rank, suit)

object PlanetPack extends PackFactory[Planet]:
  val pool: Seq[Planet] = Planet.values.toSeq
