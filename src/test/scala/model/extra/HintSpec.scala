package scalatro
package model.extra

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.round.Hand
import CardBuilder.*

import model.commons.ScoreConfig

class HintSpec extends AnyFlatSpec, Matchers:
  
  val hand: Hand = Cards(A of S, A of H, 2 of S, A of S,A of S,A of S,A of S,A of S,A of S,A of S,A of S,A of S,A of S,A of S).cards

  "Best" should "give the best hand with max size 5" in:

    given ScoreConfig = ScoreConfig.default

    Hint.best(hand) shouldBe Cards(A of S, A of S,A of S,A of S,A of S).cards
  
  
