package scalatro
package model.commons

trait EffectSource

trait Context[S <: EffectSource]

// non mi piace troppo. soluzione: rimuovere CardEffect o sistemare
object NoContext extends Context[Nothing]:
  given NoContext.type = NoContext

trait Effect[A, C <: Context[? <: EffectSource]]:
  def apply(value: A)(using C): A

object Effect:
  def identity[A, C <: Context[? <: EffectSource]]: Effect[A, C] =
    new Effect[A, C]:
      def apply(value: A)(using C): A = value

  def apply[A, C <: Context[? <: EffectSource]](f: (A, C) => A): Effect[A, C] =
    new Effect[A, C]:
      def apply(value: A)(using context: C): A =
        f(value, context)

  extension [A, C <: Context[? <: EffectSource]](self: Effect[A, C])
    def andThen(next: Effect[A, C]): Effect[A, C] =
      new Effect[A, C]:
        def apply(value: A)(using C): A =
          next(self(value))
