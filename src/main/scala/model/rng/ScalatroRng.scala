package scalatro
package model.rng

import model.commons.{Card, Joker, Planet}
import model.rng.SelectionPolicy.UniformSelection

import scala.util.Random

trait ScalatroRng:
  def draw[T <: Weighable: SelectionPolicy](pool: Pool[T], amount: Int): Seq[T]
  def shuffle[T <: Weighable](elems: Seq[T]): Seq[T] =
    given UniformSelection[T]
    draw(Pool(elems), elems.size)

object ScalatroRng:
  def apply(seed: Seed): ScalatroRng = ScalatroRngImpl(seed)
  def default: ScalatroRng = ScalatroRngImpl(Seed(42))

  private class ScalatroRngImpl(seed: Seed) extends ScalatroRng:
    import RandomMap.*
    private val randomMap: RandomMap = RandomMap(seed)

    def draw[T <: Weighable: SelectionPolicy as policy](
        pool: Pool[T],
        amount: Int
    ): Seq[T] =
      if amount <= 0 || pool.size == 0 then Seq.empty
      else
        // Already checked that amount < pool.size
        @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
        val rng = randomMap(pool.values.head)
        WeightedSampling.selectWithoutReplacement(
          pool.values,
          amount,
          policy.weight,
          rng
        )

  private type RandomMap = Map[Class[?], Random]
  private object RandomMap:
    def apply(seed: Seed): RandomMap =
      val random = Random(seed.value)
      Map(
        classOf[Card] -> Random(random.nextLong()),
        classOf[Planet] -> Random(random.nextLong()),
        classOf[Joker] -> Random(random.nextLong())
      )

    extension (randomMap: RandomMap)
      def apply(value: AnyRef): Random = value match
        case _: Card   => randomMap(classOf[Card])
        case _: Planet => randomMap(classOf[Planet])
        case _: Joker  => randomMap(classOf[Joker])
        case _         =>
          throw new IllegalArgumentException(s"No random generator for $value")

  private object WeightedSampling:
    /** Implementation of Efraimidis–Spirakis A-Res [reservoir
      * sampling](https://en.wikipedia.org/wiki/Reservoir_sampling) algorithm
      */
    def selectWithoutReplacement[T](
        elems: IndexedSeq[T],
        amount: Int,
        weightOf: T => Weight,
        random: Random
    ): Seq[T] =
      val n = amount.max(0).min(elems.size)
      elems.indices
        .map(i => (key(weightOf(elems(i)).value, random), i))
        .sortBy(_._1)
        .take(n)
        .map((_, i) => elems(i))

    private def key(weight: Double, random: Random): Double =
      if weight <= 0.0 then Double.PositiveInfinity
      else -math.log(random.nextDouble()) / weight
