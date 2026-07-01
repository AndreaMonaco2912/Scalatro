package scalatro
package model.extra

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.round.{Cards, Hand}
import model.commons.CardBuilder.*

import model.commons.ScoreConfig

class HintSpec extends AnyFlatSpec, Matchers:
  
  val hand: Hand = Cards(A of S, A of H, 2 of S).cards
  
  "Best" should "give the best hand with no jokers involved" in:

    given ScoreConfig = ScoreConfig.default
    
    Hint.best(hand) shouldBe Cards(A of S, A of H).cards
  
  
