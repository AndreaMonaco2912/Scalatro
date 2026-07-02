package scalatro
package model.extra

import model.commons.Score.{Score, calculateScore}
import model.commons.{Card, ScoreConfig}
import model.round.Hand

import Scala2P.{*, given}
import alice.tuprolog.{SolveInfo, Term}

import scala.language.implicitConversions

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

  def best(hand: Hand)(using ScoreConfig): Seq[Card] =
    require(hand.sizeIs > 0, "Hand must have at least one card")
    rankedPlays(hand).foldLeft(Option.empty[(Seq[Card], Score)]) { (acc, cur) =>
      (acc, cur) match
        case (None, best)                                 => Some(best)
        case (Some((bestHand, bestScore)), (hand, score)) =>
          if score > bestScore then Some(cur)
          else if score == bestScore && hand.sizeIs < bestHand.size then Some(cur)
          else acc
    } match
      case Some((bestHand, _)) => bestHand
      case None => throw new IllegalArgumentException("empty hand")

  private def getIndicesFromSolveInfo(solveInfo: SolveInfo): Seq[Int] =
    Scala2P
      .extractTerm(solveInfo, "Combo")
      .toString
      .stripPrefix("[")
      .stripSuffix("]")
      .split(",")
      .map(_.trim.toInt)
      .toSeq

  private def rankedPlays(
      hand: Hand
  )(using ScoreConfig): LazyList[(Seq[Card], Score)] =
    val indices: Term = hand.indices
    val maxLen: Int = math.min(hand.size, 5)
    val goal: Term = s"between(1, $maxLen, N), combos(N, $indices, Combo)"

    for
      solveInfo <- allCombinationsEngine(goal)
      comboIndices = getIndicesFromSolveInfo(solveInfo)
      combo = comboIndices.map(hand)
      score = calculateScore(combo)
    yield combo -> score
