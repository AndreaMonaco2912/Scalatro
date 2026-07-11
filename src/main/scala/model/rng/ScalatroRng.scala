package scalatro
package model.rng

import model.commons.{Card, Joker, Planet}
import model.game.BossBlind
import model.rng.SelectionPolicy.UniformSelection

import scala.util.Random

/** A trait for game actions influenced by randomicity */
trait ScalatroRng:
  /** Draws a specific amount of elements from a pool, given a specific
    * selection policy
    * @param pool
    *   the pool to draw from
    * @param amount
    *   the amount of elements to draw
    * @tparam T
    *   the type of elements in the pool
    * @return
    *   the drawn elements
    */
  def draw[T <: Weighable: SelectionPolicy](pool: Pool[T], amount: Int): Seq[T]

  /** Shuffles a sequence of elements uniformly
    * @param elems
    *   the sequence of elements to shuffle
    * @tparam T
    *   the type of elements in the sequence
    * @return
    *   the shuffled sequence
    */
  def shuffle[T <: Weighable](elems: Seq[T]): Seq[T] =
    draw(Pool(elems), elems.size)(using UniformSelection[T]())

object ScalatroRng:
  /** Creates a new rng instance with the given seed
    * @param seed
    *   the seed to use
    * @return
    *   the rng instance
    */
  def apply(seed: Seed): ScalatroRng = ScalatroRngImpl(seed)

  /** The default rng instance, using a default seed
    * @return
    *   the rng instance
    */
  def default: ScalatroRng = ScalatroRngImpl(Seed(42))

  private class ScalatroRngImpl(seed: Seed) extends ScalatroRng:
    import RandomMap.*
    private val randomMap: RandomMap = RandomMap(seed)

    def draw[T <: Weighable: SelectionPolicy as policy](
        pool: Pool[T],
        amount: Int
    ): Seq[T] =
      pool.values match
        case IndexedSeq()     => Seq.empty
        case _ if amount <= 0 => Seq.empty
        case values           =>
          val rng = randomMap(values(0))
          WeightedSampling.selectWithoutReplacement(
            values,
            amount,
            policy.weight,
            rng
          )

  private type RandomMap = Map[String, Random]
  private object RandomMap:
    def apply(seed: Seed): RandomMap =
      val random = Random(seed.value)
      Map(
        "Card" -> Random(random.nextLong()),
        "Planet" -> Random(random.nextLong()),
        "Joker" -> Random(random.nextLong()),
        "BossBlind" -> Random(random.nextLong())
      )

    extension (randomMap: RandomMap)
      def apply(value: AnyRef): Random = value match
        case _: Card      => randomMap("Card")
        case _: Planet    => randomMap("Planet")
        case _: Joker     => randomMap("Joker")
        case _: BossBlind => randomMap("BossBlind")
        case _            =>
          throw new IllegalArgumentException(s"No random generator for $value")

  private[rng] object WeightedSampling:
    /** Implementation of Efraimidis–Spirakis A-Res [reservoir
      * sampling](https://en.wikipedia.org/wiki/Reservoir_sampling) algorithm
      * @param elems
      *   the collection of elements to select from
      * @param amount
      *   the number of elements to select
      * @param weightOf
      *   the weight function to use for each element
      * @param random
      *   the random number generator to use
      * @tparam T
      *   the type of elements in the collection
      * @return
      *   the selected elements
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
      val u = random.nextDouble()
      if weight <= 0.0 then Double.PositiveInfinity
      else -math.log(u) / weight
