package scalatro
package model.commons

import model.extra.Scala2P.{*, given}

import alice.tuprolog.{SolveInfo, Term}

import scala.language.implicitConversions

/** A trait that defines a strategy for ordering a sequence of cards. */
trait CardOrderer:
  /** Orders the cards according to a rule defined by the implementing
    * CardOrderer.
    *
    * @param cards
    *   the sequence of cards to be ordered
    * @return
    *   the new sequence of cards
    */
  def order(cards: Seq[Card]): Seq[Card]

/** A collection of predefined [[CardOrderer]]s or templates to create them */
object CardOrderer:
  /** An [[scala.math.Ordering]] for [[Rank]] in decreasing order by their
    * numeric value
    */
  given rankOrdering: Ordering[Rank] = (r1, r2) => r2.value compare r1.value

  /** An [[scala.math.Ordering]] for [[Suit]], based on their ordinal position
    * Suit
    */
  given suitOrdering: Ordering[Suit] = _.ordinal compare _.ordinal

  /** Maintains the original card order
    * @return
    *   the card orderer
    */
  val identity: CardOrderer = cards => cards

  /** Sorts cards by increasing [[Rank]], then sorts each rank group by [[Suit]]
    * @return
    *   the card orderer
    */
  val sortByRank: CardOrderer = _.sortBy(c => (c.rank, c.suit))

  /** Sorts cards by [[Suit]] according to the suit ordering, then sorts each
    * suit group by rank.
    * @return
    *   the card orderer
    */
  val sortBySuit: CardOrderer = _.sortBy(c => (c.suit, c.rank))

  /** swap(+List, +I, +J, -NewList)
    *
    * getElem(+List, +Index, -Elem)
    *
    * setElem(+List, +Index, +Elem, -NewList)
    */
  private val swapCardsTheory: String = """
      swap(List, I, J, O) :- getElem(List, I, Ei), getElem(List, J, Ej), setElem(List, I, Ej, Tmp), setElem(Tmp, J, Ei, O).
 
      getElem([H|_], 0, H) :- !.
      getElem([_|T], I, E) :- I1 is I-1, getElem(T, I1, E).
 
      setElem([_|T], 0, E, [E|T]) :- !.
      setElem([H|T], I, E, [H|O]) :- I1 is I-1, setElem(T, I1, E, O).
    """

  private val swapCardsEngine: Term => LazyList[SolveInfo] =
    mkPrologEngine(swapCardsTheory)

  /** Swaps two cards at the given positions.
    *
    * Both indexes must be within the sequence bounds.
    *
    * @param i
    *   the index of the first card to swap
    * @param j
    *   the index of the second card to swap
    * @return
    *   the card orderer
    */
  def swapCards(i: Int, j: Int): CardOrderer = cards =>
    require(i >= 0 && i < cards.size, s"i must be in [0, ${cards.size})")
    require(j >= 0 && j < cards.size, s"j must be in [0, ${cards.size})")
    reorderByIndices(
      cards,
      swapCardsEngine,
      indicesStr => s"swap($indicesStr, $i, $j, O)"
    )

  /** move(+List, +FromIndex, +ToIndex, -NewList)
    *
    * remove(+List, +Index, -Elem, -Rest)
    *
    * insert(+List, +Card, +Index, -NewList)
    */
  private val moveCardTheory: String = """
      move(List, From, To, O) :- remove(List, From, Elem, Rest), insert(Rest, Elem, To, O).

      remove([H|T], 0, H, T) :- !.
      remove([H|T], From, Elem, [H|Rest]) :- From2 is From - 1, remove(T, From2, Elem, Rest).

      insert(T, Elem, 0, [Elem|T]) :- !.
      insert([H|T], Elem, To, [H|O]) :- To2 is To-1, insert(T, Elem, To2, O).
    """

  private val moveCardEngine: Term => LazyList[SolveInfo] =
    mkPrologEngine(moveCardTheory)

  /** Moves a card from one position to another, shifting other cards to fill
    * the gap.
    *
    * Both indexes must be within the sequence bounds.
    *
    * @param from
    *   the initial index of the card
    * @param to
    *   the target index of the card
    * @return
    *   the card orderer
    */
  def moveCard(from: Int, to: Int): CardOrderer = cards =>
    require(
      from >= 0 && from < cards.size,
      s"from must be in [0, ${cards.size})"
    )
    require(to >= 0 && to < cards.size, s"to must be in [0, ${cards.size})")
    reorderByIndices(
      cards,
      moveCardEngine,
      indicesStr => s"move($indicesStr, $from, $to, O)"
    )

  private def reorderByIndices(
      cards: Seq[Card],
      engine: Term => LazyList[SolveInfo],
      goal: String => String
  ): Seq[Card] =
    val indices: Seq[Int] = cards.indices
    val indicesTerm: Term = indices
    val goalTerm: Term = goal(indicesTerm.toString)
    val resultTerm: Term = solveOneAndGetTerm(engine, goalTerm, "O")

    val newOrder: Seq[Int] = resultTerm.toString
      .stripPrefix("[")
      .stripSuffix("]")
      .split(",")
      .map(_.trim.toInt)
      .toSeq

    newOrder.map(cards)
