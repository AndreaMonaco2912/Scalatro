package scalatro
package model.game

import model.commons.{
  Card,
  CardDebuffEffect,
  Chips,
  HandScoreModification,
  Modification,
  Mult,
  OnCardScoredEffect,
  OnHandPlayedEffect,
  OnRoundStartEffect,
  Score,
  Suit
}
import model.commons.Score.Score
import model.rng.Weighable
import model.round.{RoundState, RoundStateModification}
import model.commons.Rank.{Jack, King, Queen}
import model.commons.Suit.{Clubs, Diamonds, Hearts, Spades}

case class BlindProgression(anteNum: Int, targetScore: Score, blind: Blind):

  def roundNum: Int =
    (anteNum - 1) * 3 + BlindProgression.blindPosition(blind) + 1

  def isBeaten(achieved: Score): Boolean = achieved >= targetScore

  def next: BlindProgression =
    val nextBlind = blind match
      case SmallBlind => BigBlind
      case BigBlind   => TheFlint
      case _          => SmallBlind

    val nextAnte = if isBoss then anteNum + 1 else anteNum
    BlindProgression(
      nextAnte,
      BlindProgression.scoreFor(nextAnte, nextBlind),
      nextBlind
    )

  def isBoss: Boolean = blind match
    case SmallBlind | BigBlind => false
    case _                     => true

object BlindProgression:
  val initialScore: Score = Score(300)
  private val initialAnte = 1

  def first: BlindProgression =
    BlindProgression(initialAnte, initialScore, SmallBlind)

  private def scoreFor(anteNum: Int, blind: Blind): Score =
    val small = initialScore * scala.math.pow(3, anteNum - 1)
    blind match
      case SmallBlind => small
      case BigBlind   => small * 1.5
      case _          => small * 2

  private def blindPosition(blind: Blind): Int = blind match
    case SmallBlind => 0
    case BigBlind   => 1
    case _          => 2

trait Blind extends Weighable:
  /** @return
    *   The name of the blind
    */
  def name: String

  /** @return
    *   The description of the blind
    */
  def description: String

case class BlindType(name: String, description: String) extends Blind

trait SuitDebuff(suit: Suit) extends CardDebuffEffect:
  override def debuffs(card: Card): Boolean = card.suit == suit

object SmallBlind extends BlindType("Small Blind", "No special effect")
object BigBlind extends BlindType("Big Blind", "No special effect")
object TheNeedle
    extends BlindType("The Needle", "Play only 1 hand")
    with OnRoundStartEffect:
  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    Seq(RoundStateModification.SetRemainingPlays(1))
object TheFlint
    extends BlindType(
      "The Flint",
      "Base Chips and Mult for played poker hands are halved for the entire round"
    )
    with OnHandPlayedEffect:
  override def onHandPlayed(cards: Seq[Card]): Seq[HandScoreModification] = Seq(
    HandScoreModification.MultiplicativeChips(Chips(0.5)),
    HandScoreModification.MultiplicativeMult(Mult(0.5))
  )
object TheWater
    extends BlindType("The Water", "Start with 0 discards"),
      OnRoundStartEffect:
  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    Seq(RoundStateModification.SetRemainingDiscards(0))

object TheHead
    extends BlindType("The Head", "All Heart cards are debuffed")
    with SuitDebuff(Hearts)
object TheClub
    extends BlindType("The Club", "All Club cards are debuffed")
    with SuitDebuff(Clubs)
object TheGoad
    extends BlindType("The Goad", "All Spade cards are debuffed")
    with SuitDebuff(Spades)
object TheWindow
    extends BlindType("The Window", "All Diamond cards are debuffed")
    with SuitDebuff(Diamonds)
object ThePlant
    extends BlindType("The Plant", "All face cards are debuffed"),
      CardDebuffEffect:
  override def debuffs(card: Card): Boolean = Set(Jack, Queen, King)(card.rank)
