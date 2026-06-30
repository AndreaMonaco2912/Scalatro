package scalatro
package model.commons

trait Effect[A, Ctx]:
  def apply(value: A)(using Ctx): A

object Effect:

  def identity[A, Ctx]: Effect[A, Ctx] =
    new Effect[A, Ctx]:
      def apply(value: A)(using Ctx): A = value

  def apply[A, Ctx](f: (A, Ctx) => A): Effect[A, Ctx] =
    new Effect[A, Ctx]:
      def apply(value: A)(using context: Ctx): A =
        f(value, context)

  extension [A, Ctx](self: Effect[A, Ctx])
    def andThen(next: Effect[A, Ctx]): Effect[A, Ctx] =
      new Effect[A, Ctx]:
        def apply(value: A)(using Ctx): A =
          next(self(value))
