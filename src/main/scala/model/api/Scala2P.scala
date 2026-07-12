package scalatro
package model.api

import alice.tuprolog.*
import scala.language.implicitConversions

object Scala2P:
  def extractTerm(solveInfo: SolveInfo, i: Integer): Term =
    solveInfo.getSolution.asInstanceOf[Struct].getArg(i).getTerm

  def extractTerm(solveInfo: SolveInfo, s: String): Term =
    solveInfo.getTerm(s)

  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[?], Term] = _.mkString("[", ",", "]")
  given Conversion[String, Theory] = Theory.parseWithStandardOperators(_)

  def mkPrologEngine(theory: Theory): Term => LazyList[SolveInfo] =
    val engine = Prolog()
    engine.setTheory(theory)

    goal =>
      val solutions: Iterable[SolveInfo] = new Iterable[SolveInfo]:
        override def iterator: Iterator[SolveInfo] = new Iterator[SolveInfo]:
          var solution: Option[SolveInfo] = Some(engine.solve(goal))

          override def hasNext: Boolean =
            solution.fold(false)(si => si.isSuccess || si.hasOpenAlternatives)

          override def next(): SolveInfo =
            solution.fold(
              throw new NoSuchElementException(
                "next() called on exhausted iterator"
              )
            ) { current =>
              solution =
                if current.hasOpenAlternatives then Some(engine.solveNext())
                else None
              current
            }

      solutions.to(LazyList)

  def solveWithSuccess(
      engine: Term => LazyList[SolveInfo],
      goal: Term
  ): Boolean =
    engine(goal).map(_.isSuccess).headOption.contains(true)

  def solveOneAndGetTerm(
      engine: Term => LazyList[SolveInfo],
      goal: Term,
      term: String
  ): Term =
    engine(goal).headOption.fold(
      throw new NoSuchElementException(s"No solution found for goal: $goal")
    )(extractTerm(_, term))
