package scalatro
package model.commons

import scala.util.Random

case class Pack[A](items: Seq[A])

trait PackFactory[A]:
  private val SMALL_PACK_SIZE = 3
  private val BIG_PACK_SIZE = 5
  def pool: Seq[A]
  def apply(n: Int)(using rng: Random): Pack[A] =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(rng.shuffle(pool).take(n))
  def apply(n: Int, blackList: Seq[A])(using rng: Random): Pack[A] =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(rng.shuffle(pool diff blackList).take(n))

  def smallPack(using rng: Random): Pack[A] =
    Pack(rng.shuffle(pool).take(SMALL_PACK_SIZE))
  def smallPack(blackList: Seq[A])(using rng: Random): Pack[A] =
    Pack(rng.shuffle(pool diff blackList).take(SMALL_PACK_SIZE))
  def bigPack(using rng: Random): Pack[A] =
    Pack(rng.shuffle(pool).take(BIG_PACK_SIZE))
  def bigPack(blackList: Seq[A])(using rng: Random): Pack[A] =
    Pack(rng.shuffle(pool diff blackList).take(BIG_PACK_SIZE))

object CardsPack extends PackFactory[Card]:
  val pool: Seq[Card] =
    for
      suit <- Suit.values.toSeq
      rank <- Rank.values.toSeq
    yield Card(rank, suit)

object PlanetPack extends PackFactory[Planet]:
  val pool: Seq[Planet] = Planet.values.toSeq

object JokerPack extends PackFactory[JokerType]:
  val pool: Seq[JokerType] = JokerType.values.toSeq
