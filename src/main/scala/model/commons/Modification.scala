package scalatro
package model.commons

import scala.annotation.tailrec

trait Modification[A]:
  def apply(value: A): A

object Modification:
  def when[A](condition: Boolean)(
      modification: Modification[A]
  ): Seq[Modification[A]] =
    when(condition)(Seq(modification))

  def when[A](condition: Boolean)(
      modifications: Seq[Modification[A]]
  ): Seq[Modification[A]] =
    if condition then modifications else Seq.empty
