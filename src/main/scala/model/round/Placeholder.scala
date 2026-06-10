package scalatro
package model.round

import model.commons.{Deck, Score}
import model.commons.Score.Score
import model.game.Blind

import cats.data.State

object Placeholder:

  def playRound: State[(Deck, Blind), Score] = State { out => (out, Score(450)) }
