package scalatro
package model.game

import model.commons.{
  Card,
  Chips,
  HandScoreModification,
  Modification,
  Mult,
  Score
}
import model.commons.Score.Score
import model.rng.Weighable
import model.round.{RoundState, RoundStateModification}
import model.game.BlindType.*

case class BlindProgression(roundNum: Int, targetScore: Score, blind: Blind):
  def isBeaten(achieved: Score): Boolean = achieved >= targetScore

  def next: BlindProgression =
    BlindProgression(
      roundNum + 1,
      targetScore * BlindProgression.increaseAmount,
      BlindProgression.nextBlind(blind)
    )

object BlindProgression:
  val increaseAmount = 1.5
  val initialScore: Score = Score(300)
  private val initialRound = 1

  def first: BlindProgression =
    BlindProgression(initialRound, initialScore, SmallBlind)

  private def nextBlind(current: Blind) = current match
    case SmallBlind => BigBlind
    case BigBlind   => TheNeedle
    case TheNeedle  => SmallBlind

trait Blind extends Weighable:
  /** @return
    *   The name of the blind
    */
  def name: String

  /** @return
    *   The description of the blind
    */
  def description: String

  /** The effect applied at the start of a round
    * @param round
    *   the round
    * @return
    *   the modifications
    */
  def onRoundStart(round: RoundState): Seq[Modification] = Seq.empty

  /** The effect applied when a card played is scored
    * @return
    *   the modifications
    */
  def onCardScored(card: Card): Seq[Modification] = Seq.empty

  /** The effect applied when a hand is played
    * @return
    *   the modifications
    */
  def onHandPlayed(cards: Seq[Card]): Seq[Modification] = Seq.empty

enum BlindType(val name: String, val description: String) extends Blind:
  case SmallBlind extends BlindType("Small Blind", "No special effect")
  case BigBlind extends BlindType("Big Blind", "No special effect")
  case TheNeedle extends BlindType("The Needle", "Play only 1 hand")
  case TheFlint
      extends BlindType(
        "The Flint",
        "Base Chips and Mult for played poker hands are halved for the entire round "
      )

  override def onRoundStart(round: RoundState): Seq[Modification] = this match
    case TheNeedle => Seq(RoundStateModification.setRemainingPlays(1))
    case _         => Seq.empty

  override def onHandPlayed(cards: Seq[Card]): Seq[Modification] = this match
    case TheFlint =>
      Seq(
        HandScoreModification.MultiplicativeChips(Chips(0.5)),
        HandScoreModification.MultiplicativeMult(Mult(0.5))
      )
    case _ => Seq.empty
