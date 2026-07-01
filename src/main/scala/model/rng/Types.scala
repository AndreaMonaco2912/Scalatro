package scalatro
package model.rng

import scala.util.Random

object Types:
  opaque type Weight = Double

  object Weight:
    def apply(value: Double): Weight = math.max(0.0, value)
    def zero: Weight = 0.0

    extension (w: Weight)
      def value: Double = w
      def +(other: Weight): Weight = Weight(w + other)
      def *(other: Weight): Weight = Weight(w * other)

  opaque type Seed = Long

  object Seed:
    def apply(value: Long): Seed = value
    def random: Seed = Random().nextLong()

    extension (s: Seed) def value: Long = s

  opaque type Pool[+T] = IndexedSeq[T]

  object Pool:
    def apply[T](values: Iterable[T]): Pool[T] = values.toIndexedSeq
    def empty[T]: Pool[T] = IndexedSeq.empty

    extension [T](pool: Pool[T])
      def values: IndexedSeq[T] = pool
      def size: Int = pool.size
      infix def without(values: Iterable[T]): Pool[T] = pool.filterNot(values.toSet)

export Types.*
