package scalatro
package model.commons

trait Modification:
  type T
  def apply(value: T): T

object Modification:
  def when[A](condition: Boolean)(
      modification: Modification
  ): Seq[Modification] =
    when(condition)(Seq(modification))

  def when[A](condition: Boolean)(
      modifications: Seq[Modification]
  ): Seq[Modification] =
    if condition then modifications else Seq.empty
