package scalatro
package model.rng

import scala.util.Random

/** A collection of types used for random number generation */
object Types:
  /** A weight used for random number generation */
  opaque type Weight = Double

  object Weight:
    /** Creates a new weight with the specified value
      * @param value
      *   the weight value
      * @return
      *   the weight
      */
    def apply(value: Double): Weight = math.max(0.0, value)

    /** A zero weight
      * @return
      *   the weight
      */
    def zero: Weight = 0.0

    extension (w: Weight)
      /** The values associated with this weight
        * @return
        *   the value
        */
      def value: Double = w

      /** Adds two weights together
        * @param other
        *   the other weight
        * @return
        *   the sum of the two weights
        */
      def +(other: Weight): Weight = Weight(w + other)

      /** Multiplies two weights together
        * @param other
        *   the other weight
        * @return
        *   the product of the two weights
        */
      def *(other: Weight): Weight = Weight(w * other)

  /** A seed used for determining random events in the game. */
  opaque type Seed = Long

  object Seed:
    /** Creates a new seed with the specified value
      * @param value
      *   the seed value
      * @return
      *   the seed
      */
    def apply(value: Long): Seed = value

    /** Creates a new random seed
      * @return
      *   the seed
      */
    def random: Seed = Random().nextLong()

    extension (s: Seed)
      /** The value associated with this seed
        * @return
        *   the value
        */
      def value: Long = s

  /** A pool of items used for random selection. */
  opaque type Pool[+T] = IndexedSeq[T]

  object Pool:
    /** Creates a new pool from a collection
      * @param values
      *   the collection
      * @tparam T
      *   the type of items of the pool
      * @return
      *   the pool
      */
    def apply[T](values: Iterable[T]): Pool[T] = values.toIndexedSeq

    /** Creates an empty pool
      * @tparam T
      *   the type of items in the pool
      * @return
      *   the pool
      */
    def empty[T]: Pool[T] = IndexedSeq.empty

    extension [T](pool: Pool[T])
      /** The values in the pool
        * @return
        *   the values
        */
      def values: IndexedSeq[T] = pool

      /** The size of the pool
        * @return
        *   the size
        */
      def size: Int = pool.size

      /** Removes items from the pool
        * @param other
        *   the items to remove
        * @return
        *   the pool without the items
        */
      infix def -(other: Pool[T]): Pool[T] = pool.filterNot(other.toSet)

export Types.*
