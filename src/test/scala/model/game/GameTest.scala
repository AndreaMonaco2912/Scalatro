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
  val playRoundPlaceHolder: GameState => IO[Score] = a =>
    IO.pure(blind.targetScore)

  "A Game" should "store the seed it was created with" in:
    Game(playRoundPlaceHolder, seed).seed shouldBe seed

  it should "pick a random seed when none is given" in:
    Game(playRoundPlaceHolder).seed should not equal Game(
      playRoundPlaceHolder
    ).seed

  it should "loose against the second blind" in:
    Game(playRoundPlaceHolder).play().unsafeRunSync() shouldBe GameResult(
      blind.next,
      blind.targetScore
    )
