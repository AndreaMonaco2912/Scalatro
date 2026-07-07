package scalatro
package model.round

import model.commons.{Modification, ScoreConfig}
import app.Msg.RoundAction
import app.Msg.RoundAction.*
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
    * @param render
    *   a lambda to receive the IO for the rendering action
    * @param getAction
    *   an IO for getting the next action of the round
    * @return
    *   the round manager
    */
  def apply(
      render: RoundState => IO[Unit],
      getAction: IO[RoundAction]
  ): RoundManager = RoundManagerImpl(render, getAction)

  private class RoundManagerImpl(
      render: RoundState => IO[Unit],
      getAction: IO[RoundAction]
  ) extends RoundManager:
    override def startRound(initialRoundState: RoundState): IO[RoundState] =
      given ScoreConfig = initialRoundState.gameState.scoreConfig
      def processAction(
          roundState: RoundState,
          action: RoundAction
      ): IO[RoundState] =
        action match
          case PlayCards(cards) => IO(playCards(cards).runS(roundState).value)
          case DiscardCards(cards) =>
            IO(discardCards(cards).runS(roundState).value)
          case OrderHand(orderer) =>
            IO(orderCards(using orderer).runS(roundState).value)

      def roundLoop(initialRoundState: RoundState): IO[RoundState] =
        if initialRoundState.isFinished
        then IO.pure(initialRoundState)
        else
          for
            action <- getAction
            newRound <- processAction(initialRoundState, action)
            _ <- render(newRound)
            result <- roundLoop(newRound)
          yield result

      def invokeOnRoundStartModifications(roundState: RoundState): RoundState =
        val jokerModifications: Seq[Modification] =
          roundState.gameState.jokers.foldLeft(Seq.empty)((acc, joker) =>
            acc ++ joker.onRoundStart(roundState)
          )
        val jokerRoundModifications: Seq[RoundStateModification] =
          Modification.collect[RoundStateModification](jokerModifications)
        val roundStateAfterJokerModifications =
          jokerRoundModifications.foldLeft(roundState)((acc, mod) => mod(acc))
        val bossModifications: Seq[Modification] =
          roundState.gameState.blindProgression.blind
            .onRoundStart(roundStateAfterJokerModifications)
        val bossRoundModifications: Seq[RoundStateModification] =
          Modification.collect[RoundStateModification](bossModifications)
        val roundStateAfterBossModifications = bossRoundModifications.foldLeft(
          roundStateAfterJokerModifications
        )((acc, mod) => mod(acc))
        roundStateAfterBossModifications

      val roundStateAfterModifications = invokeOnRoundStartModifications(
        initialRoundState
      )
      render(roundStateAfterModifications) >> roundLoop(
        roundStateAfterModifications
      )
