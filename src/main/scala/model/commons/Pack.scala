package scalatro
package model.commons

import model.rng.Types.Pool
import model.rng.{ScalatroRng, SelectionPolicy, Weighable}

case class Pack[A](items: Seq[A])

trait PackFactory[A <: Weighable](using SelectionPolicy[A]):
  private val smallPackSize = 3
  def pool: Pool[A]
  def apply(n: Int)(using rng: ScalatroRng): Pack[A] =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(rng.draw(pool, n))
  def apply(n: Int, blackList: Seq[A])(using rng: ScalatroRng): Pack[A] =
    require(n >= 0, s"cannot present a pack with a negative amount of cards")
    Pack(rng.draw(pool - Pool(blackList), n))

  def smallPack(using rng: ScalatroRng): Pack[A] =
    Pack(rng.draw(pool, smallPackSize))
  def smallPack(blackList: Seq[A])(using rng: ScalatroRng): Pack[A] =
    Pack(rng.draw(pool - Pool(blackList), smallPackSize))
  def bigPack(using rng: ScalatroRng): Pack[A] =
    Pack(rng.draw(pool, smallPackSize))
  def bigPack(blackList: Seq[A])(using rng: ScalatroRng): Pack[A] =
    Pack(rng.draw(pool - Pool(blackList), smallPackSize))

class CardsPack(using SelectionPolicy[Card]) extends PackFactory[Card]:
  val pool: Pool[Card] =
    Pool(for
      suit <- Suit.values.toSeq
      rank <- Rank.values.toSeq
    yield Card(rank, suit))

class PlanetPack(using SelectionPolicy[Planet]) extends PackFactory[Planet]:
  val pool: Pool[Planet] = Pool(Planet.values)

class JokerPack(using SelectionPolicy[Joker]) extends PackFactory[Joker]:
  val pool: Pool[Joker] = Pool(JokerType.values.toSeq)
