package scalatro
package controller

import model.commons.{
  BasicHandScoreCalculator,
  Deck,
  HandScoreCalculator,
  Score
}
import model.game.Blind
import model.round.RoundAction.*
import model.round.{Round, RoundAction, TurnActions}

import cats.effect.IO

trait RoundManager:
  def startRound(initialRound: Round): IO[Round]

object RoundManager:
  import TurnActions.*
  def apply(
      render: Round => IO[Unit],
      getAction: IO[RoundAction]
  ): RoundManager = RoundManagerImpl(render, getAction)

  private val placeholderRound =
    Round(Score.zero, Seq(), Deck(Seq()), Blind.first)

  private class RoundManagerImpl(
      render: Round => IO[Unit],
      getAction: IO[RoundAction]
  ) extends RoundManager:
    private given HandScoreCalculator = BasicHandScoreCalculator

    override def startRound(initialRound: Round): IO[Round] =
      def processAction(round: Round, action: RoundAction): IO[Round] =
        action match
          case PlayCards(cards)    => IO(playCards(cards).runS(round).value)
          case DiscardCards(cards) => IO(discardCards(cards).runS(round).value)

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
