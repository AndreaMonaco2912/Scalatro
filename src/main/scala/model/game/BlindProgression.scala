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

case class BlindProgression(anteNum: Int, targetScore: Score, blind: Blind):

  def roundNum: Int =
    (anteNum - 1) * 3 + BlindProgression.blindPosition(blind) + 1

  def isBeaten(achieved: Score): Boolean = achieved >= targetScore

  def next: BlindProgression =
    val nextBlind = blind match
      case SmallBlind => BigBlind
      case BigBlind   => TheNeedle
      case TheNeedle  => SmallBlind

    val nextAnte = if blind == TheNeedle then anteNum + 1 else anteNum
    BlindProgression(
      nextAnte,
      BlindProgression.scoreFor(nextAnte, nextBlind),
      nextBlind
    )

object BlindProgression:
  val initialScore: Score = Score(300)
  private val initialAnte = 1

  def first: BlindProgression =
    BlindProgression(initialAnte, initialScore, SmallBlind)

  private def scoreFor(anteNum: Int, blind: Blind): Score =
    val small = initialScore * Math.pow(3, anteNum - 1)
    blind match
      case SmallBlind => small
      case TheNeedle  => small * 2
      case BigBlind   => (small + small * 2) / 2

  private def blindPosition(blind: Blind): Int = blind match
    case SmallBlind => 0
    case BigBlind   => 1
    case TheNeedle  => 2

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
        "Base Chips and Mult for played poker hands are halved for the entire round"
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
