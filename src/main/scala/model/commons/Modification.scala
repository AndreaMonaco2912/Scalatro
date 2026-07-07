package scalatro
package model.commons

import scala.reflect.ClassTag

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
  
  def collect[M <: Modification](modifications: Seq[Modification])(using ClassTag[M]): Seq[M] =
    modifications.collect { case m: M => m }
