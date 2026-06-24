package scalatro
package model.round

import model.commons.ScoreConfig
import model.round.RoundAction.*
import model.round.{Round, RoundAction, TurnActions}

import cats.effect.IO

/** A trait representing a functional round manager */
trait RoundManager:
  /** Creates a template for starting the round in a functional way
    * @param initialRound
    *   the initial round
    * @return
    *   an IO representing the computation
    */
  def startRound(initialRound: Round): IO[Round]

object RoundManager:
  import TurnActions.*

  /** Creates a [[RoundManager]] with the default implementation
    * @param render
    *   a lambda to receive the IO for the rendering action
    * @param getAction
    *   an IO for getting the next action of the round
    * @return
    *   the round manager
    */
  def apply(
      render: Round => IO[Unit],
      getAction: IO[RoundAction]
  ): RoundManager = RoundManagerImpl(render, getAction)

  private class RoundManagerImpl(
      render: Round => IO[Unit],
      getAction: IO[RoundAction]
  ) extends RoundManager:
    private given ScoreConfig = ScoreConfig.default

    override def startRound(initialRound: Round): IO[Round] =
      def processAction(round: Round, action: RoundAction): IO[Round] =
        action match
          case PlayCards(cards)    => IO(playCards(cards).runS(round).value)
          case DiscardCards(cards) => IO(discardCards(cards).runS(round).value)
          case OrderHand(orderer)  =>
            IO(orderCards(using orderer).runS(round).value)

      def roundLoop(initialRound: Round): IO[Round] =
        if initialRound.isFinished
        then IO.pure(initialRound)
        else
          for
            action <- getAction
            newRound <- processAction(initialRound, action)
            _ <- render(newRound)
            result <- roundLoop(newRound)
          yield result

      render(initialRound) >> roundLoop(initialRound)
