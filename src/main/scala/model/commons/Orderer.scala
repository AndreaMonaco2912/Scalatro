package scalatro
package model.commons

import model.api.Scala2P.{*, given}

import alice.tuprolog.{SolveInfo, Term}

import scala.language.implicitConversions

/** A trait that defines a strategy for ordering elements. */
trait Orderer[T]:
  /** Orders the elements according to a rule defined by the implementation.
    *
    * @param elements
    *   the elements to be ordered
    * @return
    *   the reordered elements
    */
  def order(elements: Seq[T]): Seq[T]

/** A collection of predefined [[Orderer]]s or templates to create them */
object Orderer:
  /** An [[scala.math.Ordering]] for [[Rank]] in decreasing order by their
    * numeric value
    */
  given rankOrdering: Ordering[Rank] = (r1, r2) => r2.value compare r1.value

  /** An [[scala.math.Ordering]] for [[Suit]], based on their ordinal position
    * Suit
    */
  given suitOrdering: Ordering[Suit] = _.ordinal compare _.ordinal

  /** Maintains the original order
    * @return
    *   the orderer
    */
  def identity[T]: Orderer[T] = elems => elems

  /** Sorts cards by increasing [[Rank]], then sorts each rank group by [[Suit]]
    * @return
    *   the orderer
    */
  val sortByRank: Orderer[Card] = _.sortBy(c => (c.rank, c.suit))

  /** Sorts cards by [[Suit]] according to the suit ordering, then sorts each
    * suit group by rank.
    * @return
    *   the orderer
    */
  val sortBySuit: Orderer[Card] = _.sortBy(c => (c.suit, c.rank))

  /** swap(+List, +I, +J, -NewList)
    *
    * getElem(+List, +Index, -Elem)
    *
    * setElem(+List, +Index, +Elem, -NewList)
    */
  private val swapElemsTheory: String = """
      swap(List, I, J, O) :- getElem(List, I, Ei), getElem(List, J, Ej), setElem(List, I, Ej, Tmp), setElem(Tmp, J, Ei, O).
 
      getElem([H|_], 0, H) :- !.
      getElem([_|T], I, E) :- I1 is I-1, getElem(T, I1, E).
 
      setElem([_|T], 0, E, [E|T]) :- !.
      setElem([H|T], I, E, [H|O]) :- I1 is I-1, setElem(T, I1, E, O).
    """

  private val swapElemsEngine: Term => LazyList[SolveInfo] =
    mkPrologEngine(swapElemsTheory)

  /** Swaps two elements at the given positions.
    *
    * Both indexes must be within the sequence bounds.
    *
    * @param i
    *   the index of the first element to swap
    * @param j
    *   the index of the second element to swap
    * @return
    *   the orderer
    */
  def swapElements[T](i: Int, j: Int): Orderer[T] = elems =>
    require(i >= 0 && i < elems.size, s"i must be in [0, ${elems.size})")
    require(j >= 0 && j < elems.size, s"j must be in [0, ${elems.size})")
    reorderByIndices(
      elems,
      swapElemsEngine,
      indicesStr => s"swap($indicesStr, $i, $j, O)"
    )

  /** move(+List, +FromIndex, +ToIndex, -NewList)
    *
    * remove(+List, +Index, -Elem, -Rest)
    *
    * insert(+List, +Elem, +Index, -NewList)
    */
  private val moveElemTheory: String = """
      move(List, From, To, O) :- remove(List, From, Elem, Rest), insert(Rest, Elem, To, O).

      remove([H|T], 0, H, T) :- !.
      remove([H|T], From, Elem, [H|Rest]) :- From2 is From - 1, remove(T, From2, Elem, Rest).

      insert(T, Elem, 0, [Elem|T]) :- !.
      insert([H|T], Elem, To, [H|O]) :- To2 is To-1, insert(T, Elem, To2, O).
    """

  private val moveElemEngine: Term => LazyList[SolveInfo] =
    mkPrologEngine(moveElemTheory)

  /** Moves an element from one position to another, shifting other elements to
    * fill the gap.
    *
    * Both indexes must be within the sequence bounds.
    *
    * @param from
    *   the initial index of the element
    * @param to
    *   the target index of the element
    * @return
    *   the orderer
    */
  def moveElement[T](from: Int, to: Int): Orderer[T] = elems =>
    require(
      from >= 0 && from < elems.size,
      s"from must be in [0, ${elems.size})"
    )
    require(to >= 0 && to < elems.size, s"to must be in [0, ${elems.size})")
    reorderByIndices(
      elems,
      moveElemEngine,
      indicesStr => s"move($indicesStr, $from, $to, O)"
    )

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  private def reorderByIndices[T](
      elements: Seq[T],
      engine: Term => LazyList[SolveInfo],
      goal: String => String
  ): Seq[T] =
    val indices: Seq[Int] = elements.indices
    val indicesTerm: Term = indices
    val goalTerm: Term = goal(indicesTerm.toString)
    val resultTerm: Term = solveOneAndGetTerm(engine, goalTerm, "O")

    val newOrder: Seq[Int] = resultTerm.toString
      .stripPrefix("[")
      .stripSuffix("]")
      .split(",")
      .map(_.trim.toInt)
      .toSeq

    newOrder.map(elements)
