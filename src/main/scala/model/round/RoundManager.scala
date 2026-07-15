package scalatro
package model.round

import app.Msg.RoundAction
import app.Msg.RoundAction.*
import model.commons.{Modification, OnRoundStartEffect, ScoreConfig}
import model.round.RoundState

import cats.effect.IO

/** A trait representing a functional round manager */
trait RoundManager:
  /** Creates a template for starting the round in a functional way
    * @param initialRoundState
    *   the initial round
    * @return
    *   an IO representing the computation
    */
  def startRound(initialRoundState: RoundState): IO[RoundState]

object RoundManager:
  import TurnActions.*

  /** Creates a [[RoundManager]] with the default implementation
    * @param updateView
    *   a lambda to receive the IO for the view update action
    * @param getAction
    *   an IO for getting the next action of the round
    * @return
    *   the round manager
    */
  def apply(
      updateView: RoundState => IO[Unit],
      getAction: IO[RoundAction]
  ): RoundManager = RoundManagerImpl(updateView, getAction)

  private class RoundManagerImpl(
      updateView: RoundState => IO[Unit],
      getAction: IO[RoundAction]
  ) extends RoundManager:
    override def startRound(initialRoundState: RoundState): IO[RoundState] =
      def processAction(
          roundState: RoundState,
          action: RoundAction
      ): IO[RoundState] =
        given ScoreConfig = roundState.gameState.scoreConfig
        action match
          case PlayCards(cards) => IO(playCards(cards).runS(roundState).value)
          case DiscardCards(cards) =>
            IO(discardCards(cards).runS(roundState).value)
          case OrderHand(orderer) =>
            IO(orderCards(using orderer).runS(roundState).value)
          case OrderJoker(orderer) =>
            IO(orderJokers(using orderer).runS(roundState).value)

      def roundLoop(initialRoundState: RoundState): IO[RoundState] =
        if initialRoundState.isFinished
        then IO.pure(initialRoundState)
        else
          for
            action <- getAction
            newRound <- processAction(initialRoundState, action)
            _ <- updateView(newRound)
            result <- roundLoop(newRound)
          yield result

      def runOnRoundStartEffects(roundState: RoundState): RoundState =
        val onRoundStartEffectSources = Seq(
          initialRoundState.gameState.blindProgression.blind
        ) ++ initialRoundState.gameState.jokers
        Modification.run(roundState, onRoundStartEffectSources, roundState) {
          case s: OnRoundStartEffect => s.onRoundStart
        }

      val roundStateAfterEffects = runOnRoundStartEffects(initialRoundState)
      updateView(roundStateAfterEffects) >> roundLoop(roundStateAfterEffects)
