package scalatro
package model.round

import model.commons.Score
import model.commons.Score.Score
import model.game.GameState

import cats.data.State

object Placeholder:

  def playRound: State[GameState, Score] = State { out => (out, Score(450)) }
