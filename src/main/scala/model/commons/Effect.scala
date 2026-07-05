package scalatro
package model.commons

// TODO: da valutare
trait Effectible

// A <: Effectible ?
trait Modification[A]:
  def apply(value: A): A

trait Effect[A]:
  def apply(value: A): Seq[Modification[?]]

object Effect:
  def identity[A]: Effect[A] =
    (value: A) => Seq.empty

  def apply[A](
      predicate: A => Boolean,
      modification: Modification[?]
  ): Effect[A] =
    (value: A) => if predicate(value) then Seq(modification) else Seq.empty

  def apply[A](modification: Modification[?]): Effect[A] =
    (value: A) => Seq(modification)

  extension [A](self: Effect[A])
    def andThen(next: Effect[A]): Effect[A] =
      (value: A) => self(value) ++ next(value)
