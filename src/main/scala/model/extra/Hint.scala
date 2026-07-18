package scalatro
package model.extra

import model.commons.Score.{Score, calculateScore}
import model.commons.{Card, ScoreConfig}
import model.round.Hand
import model.api.Scala2P.{*, given}

import alice.tuprolog.{SolveInfo, Term}
import model.api.Scala2P

import scala.language.implicitConversions

/** Provides utilities for finding the best-scoring play out of a [[Hand]].
  */
object Hint:

  private val allCombinationsTheory: String = """
    between(Low, High, Low) :- Low =< High.
    between(Low, High, X) :- Low < High, Low1 is Low + 1, between(Low1, High, X).

    combos(0, _, []).
    combos(N, [H|T], [H|T2]) :- N > 0, N1 is N - 1, combos(N1, T, T2).
    combos(N, [_|T], T2) :- N > 0, combos(N, T, T2).
  """

  private val allCombinationsEngine: Term => LazyList[SolveInfo] =
    mkPrologEngine(allCombinationsTheory)

  /** Finds the best possible play from the given hand.
    *
    * All valid sub-combinations of the hand (up to 5 cards) are scored (using a
    * [[ScoreConfig]]), and the combination with the highest score is returned.
    * In case of a tie, the shortest combination is preferred.
    *
    * @param hand
    *   the hand to evaluate; must contain at least one card
    * @return
    *   the sequence of cards forming the best-scoring play
    * @throws IllegalArgumentException
    *   if the hand is empty, or if no ranked play could be found
    */
  def best(hand: Hand)(using ScoreConfig): Seq[Card] =
    require(hand.sizeIs > 0, "Hand must have at least one card")
    rankedPlays(hand).foldLeft(Option.empty[(Seq[Card], Score)]) { (acc, cur) =>
      (acc, cur) match
        case (None, best)                                 => Some(best)
        case (Some((bestHand, bestScore)), (hand, score)) =>
          if score > bestScore then Some(cur)
          else if score == bestScore && hand.sizeIs < bestHand.size then
            Some(cur)
          else acc
    } match
      case Some((bestHand, _)) => bestHand
      case None => throw new IllegalArgumentException("empty hand")

  /** Generates every playable sub-combination of the given hand.
    *
    * Combinations range in size from 1 up to a maximum of 5 cards (or the
    * hand's size, if smaller), and preserve the relative order of cards as they
    * appear in the hand.
    *
    * @param hand
    *   the hand to generate combinations from
    * @return
    *   a lazily-evaluated stream of card combinations
    */
  def allPlayableHands(hand: Hand): LazyList[Seq[Card]] =
    val indices: Term = hand.indices
    val maxLen: Int = math.min(hand.size, 5)
    val goal: Term = s"between(1, $maxLen, N), combos(N, $indices, Combo)"
    for
      solveInfo <- allCombinationsEngine(goal)
      comboIndices = getIndicesFromSolveInfo(solveInfo)
      combo = comboIndices.map(hand)
    yield combo

  private def rankedPlays(
      hand: Hand
  )(using ScoreConfig): LazyList[(Seq[Card], Score)] =
    for
      combo <- allPlayableHands(hand)
      score = calculateScore(combo)
    yield combo -> score

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  private def getIndicesFromSolveInfo(solveInfo: SolveInfo): Seq[Int] =
    Scala2P
      .extractTerm(solveInfo, "Combo")
      .toString
      .stripPrefix("[")
      .stripSuffix("]")
      .split(",")
      .map(_.trim.toInt)
      .toSeq
