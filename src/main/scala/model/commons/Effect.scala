package scalatro
package model.commons

import model.game.GameState

trait EffectSource

// idea: aggiungere EffectDestination e fare in modo che EffectSource sia una case class con sottotipi di EffectDestination
// aggiungere GlobalContext in modo che CardContext e JokerContext siano suoi sottotipi
// in questo modo GlobalContext e' utilizzabile facilmente nelle foldLeft
trait Context[S <: EffectSource]

//object GlobalContext extends Context[Nothing]:
//  given GlobalContext.type = GameState

trait Effect[C <: Context[? <: EffectSource]]:
  def apply(context: C): C

object Effect:
  def identity[C <: Context[? <: EffectSource]]: Effect[C] =
    (context: C) => context

  def apply[C <: Context[? <: EffectSource]](f: C => C): Effect[C] =
    (context: C) => f(context)

  extension [C <: Context[? <: EffectSource]](self: Effect[C])
    def andThen(next: Effect[C]): Effect[C] =
      (context: C) => next(self(context))
