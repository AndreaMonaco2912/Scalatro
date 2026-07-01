package scalatro
package model.extra

import model.commons.Score.{Score, calculateScore}
import model.commons.{Card, ScoreConfig}
import model.round.Hand

import Scala2P.{*, given}
import alice.tuprolog.{SolveInfo, Term}

object Hint:

  private val allCombinationsEngine: Term => LazyList[SolveInfo] =
    mkPrologEngine("""
    combos([], []).
    combos([H|T],[H|T2]) :- combos(T,T2).
    combos([_|T],T2) :- combos(T,T2).
  """)

  def best(hand: Hand)(using ScoreConfig): Seq[Card] =
    require(hand.sizeIs > 0, "Hand must have at least one card")
    rankedPlays(hand).foldLeft(Option.empty[(Seq[Card], Score)]) { (acc, cur) =>
      (acc, cur) match
        case (None, best)                                 => Some(best)
        case (Some((bestHand, bestScore)), (hand, score)) =>
          if score >= bestScore && hand.sizeIs < bestHand.size then Some(cur)
          else acc
    } match
      case Some((bestHand, _)) => bestHand
      case None => throw new IllegalArgumentException("empty hand")

  private def rankedPlays(
      hand: Hand
  )(using ScoreConfig): LazyList[(Seq[Card], Score)] =
    val indices: Term = hand.indices
    val goal: Term = s"combos($indices, Combo)"
    for
      solveInfo <- allCombinationsEngine(goal)
      comboTerm = Scala2P.extractTerm(solveInfo, "Combo")
//      _ = System.out.println(comboTerm)
      comboIndices = comboTerm.toString
        .stripPrefix("[")
        .stripSuffix("]")
        .split(",")
        .filter(_.nonEmpty)
        .map(_.trim.toInt)
        .toSeq
//      _ = System.out.println(comboIndices)
      if comboIndices.nonEmpty && comboIndices.size <= 5
      combo = comboIndices.map(hand)
//      _ = System.out.println(combo)
      score = calculateScore(combo)
//      _ = System.out.println(score)
    yield combo -> score
