package scalatro
package controller

import model.commons.{Deck, Score}
import model.game.Blind
import model.round.{Placeholder, Round, RoundAction}
import view.View

import cats.effect.IO
import cats.effect.std.Queue

import scala.util.Random

trait Controller[S, A]:
  def start(): IO[S]

class SingleRoundController(
    view: View[Round, RoundAction],
    actionQueue: Queue[IO, RoundAction]
) extends Controller[Round, RoundAction]:

  given Random = new Random()
  private val (hand, deck) = Deck().shuffle.draw(8)

  private val initialRound = Round(
    Score.zero,
    hand,
    deck,
    Blind.first
  )

  def start(): IO[Round] =
    for
      roundManager = RoundManager(view.render, actionQueue.take)
      finalRound <- roundManager.startRound(initialRound)
    yield finalRound
