package scalatro
package model.game

import model.commons.{
  Card,
  Chips,
  HandScoreModification,
  Mult,
  OnHandPlayedEffect,
  OnRoundStartEffect,
  Rank,
  Score,
  Suit
}
import model.rng.SelectionPolicy.UniformSelection
import model.commons.Score.Score
import model.rng.{ScalatroRng, SelectionPolicy, Weighable}
import model.round.{RoundState, RoundStateModification}
import model.commons.Rank.{Jack, King, Queen}
import model.commons.Suit.{Clubs, Diamonds, Hearts, Spades}
import model.rng.Types.Pool
import model.game.BlindProgression.roundsInAnte

case class BlindProgression(
    roundNum: Int,
    blind: Blind,
    targetScoreFromOutside: Option[Score] = Option.empty
):
  given SelectionPolicy[BossBlind] = UniformSelection[BossBlind]

  private val bossBlindPool: Pool[BossBlind] = Pool(
    Seq(
      TheNeedle,
      TheFlint,
      TheWater,
      TheHead,
      TheClub,
      TheGoad,
      TheWindow,
      ThePlant
    )
  )
  private val defaultBoss: BossBlind = TheGoad

  def anteNum: Int = (roundNum - 1) / roundsInAnte + 1

  def targetScore: Score =
    targetScoreFromOutside.getOrElse(BlindProgression.scoreFor(anteNum, blind))

  def isBeaten(achieved: Score): Boolean = achieved >= targetScore

  def withTargetScore(score: Score): BlindProgression =
    copy(targetScoreFromOutside = Some(score))

  def isBoss: Boolean = blind match
    case _: BossBlind   => true
    case _: NormalBlind => false

  def next(using rng: ScalatroRng): BlindProgression =
    val nextBlind: Blind = blind match
      case SmallBlind => BigBlind
      case BigBlind   =>
        rng.draw(bossBlindPool, 1).headOption.getOrElse(defaultBoss)
      case _: BossBlind => SmallBlind

    BlindProgression(roundNum + 1, nextBlind)

object BlindProgression:
  private val initialScore: Double = 300
  val roundsInAnte: Int = 3
  val initialSmallBlindScore: Score = Score(initialScore)
  private val bigBlindScoreMultiplier = 1.5
  private val bossBlindScoreMultiplier = 2

  def first: BlindProgression =
    BlindProgression(1, SmallBlind)

  private def scoreFor(anteNum: Int, blind: Blind): Score =
    val small = initialSmallBlindScore * scala.math.pow(3, anteNum - 1)
    blind match
      case SmallBlind   => small
      case BigBlind     => small * bigBlindScoreMultiplier
      case _: BossBlind => small * bossBlindScoreMultiplier

sealed trait Blind:
  /** @return
    *   The name of the blind
    */
  def name: String

  /** @return
    *   The description of the blind
    */
  def description: String

object Blind:
  extension (blind: Blind)
    def isDebuffing(card: Card): Boolean = blind match
      case d: Debuffer => d.debuffs(card)
      case _           => false

sealed trait NormalBlind extends Blind
sealed trait BossBlind extends Blind, Weighable

trait Debuffer:
  def debuffs(card: Card): Boolean

trait SuitDebuff(suit: Suit) extends Debuffer:
  override def debuffs(card: Card): Boolean = card.suit == suit

trait RankDebuff(rank: Rank*) extends Debuffer:
  override def debuffs(card: Card): Boolean = rank.contains(card.rank)

object SmallBlind extends NormalBlind:
  val name = "Small Blind"
  val description = "No special effect"

object BigBlind extends NormalBlind:
  val name = "Big Blind"
  val description = "No special effect"

object TheNeedle extends BossBlind with OnRoundStartEffect:
  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    Seq(RoundStateModification.SetRemainingPlays(1))
  val name = "The Needle"
  val description = "Play only 1 hand"

object TheFlint extends BossBlind with OnHandPlayedEffect:
  val name = "The Flint"
  val description =
    "Base Chips and Mult for played poker hands are halved for the entire round"

  override def onHandPlayed(cards: Seq[Card]): Seq[HandScoreModification] = Seq(
    HandScoreModification.MultiplicativeChips(Chips(0.5)),
    HandScoreModification.MultiplicativeMult(Mult(0.5))
  )

object TheWater extends BossBlind with OnRoundStartEffect:
  val name = "The Water"
  val description = "Start with 0 discards"

  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    Seq(RoundStateModification.SetRemainingDiscards(0))

object TheHead extends BossBlind with SuitDebuff(Hearts):
  val name = "The Head"
  val description = "All Heart cards are debuffed"

object TheClub extends BossBlind with SuitDebuff(Clubs):
  val name = "The Club"
  val description = "All Club cards are debuffed"

object TheGoad extends BossBlind with SuitDebuff(Spades):
  val name = "The Goad"
  val description = "All Spade cards are debuffed"

object TheWindow extends BossBlind with SuitDebuff(Diamonds):
  val name = "The Window"
  val description = "All Diamond cards are debuffed"

object ThePlant extends BossBlind with RankDebuff(Jack, Queen, King):
  val name = "The Plant"
  val description = "All face cards are debuffed"
