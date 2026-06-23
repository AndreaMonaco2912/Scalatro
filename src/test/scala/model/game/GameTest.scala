package scalatro
package model.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.commons.Score.Score

import cats.effect.IO
import cats.effect.unsafe.implicits.global

class GameTest extends AnyFlatSpec, Matchers:
  val seed = 0L
  val blind: Blind = Blind.first

  def handler(
      onPlay: GameState => IO[Score],
      onWon: Blind => IO[Unit] = _ => IO.unit,
      onLost: Blind => IO[Unit] = _ => IO.unit
  ): GameHandler = new GameHandler:
    def playRound(state: GameState): IO[Score] = onPlay(state)
    def onRoundWon(b: Blind): IO[Unit] = onWon(b)
    def onRoundLost(b: Blind): IO[Unit] = onLost(b)

  val alwaysHitsFirstTarget: GameState => IO[Score] =
    _ => IO.pure(blind.targetScore)

  "A Game" should "store the seed it was created with" in:
    Game(handler(alwaysHitsFirstTarget), seed).seed shouldBe seed

  it should "pick a random seed when none is given" in:
    Game(handler(alwaysHitsFirstTarget)).seed should not equal
      Game(handler(alwaysHitsFirstTarget)).seed

  it should "lose against the second blind" in:
    Game(handler(alwaysHitsFirstTarget)).play().unsafeRunSync() shouldBe
      GameResult(blind.next, blind.targetScore)
