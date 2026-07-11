package scalatro
package model.rng.seed

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.game.GameState
import model.rng.seed.SimulationTypes.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** A test spec for [[PickFromPackPolicy]] */
class PickFromPackPolicySpec extends AnyFlatSpec, Matchers:

  "PickFromPackPolicy for Card" should "add a picked card to the game deck" in:
    val card = Card(Ace, Spades)
    val initial = GameState.initial

    val updated = Seq(card).pickedBy(initial)

    updated.deck.cards should contain(card)
    updated.deck.size shouldBe initial.deck.size + 1

  "PickFromPackPolicy for Joker" should "add a picked joker to the game jokers" in:
    val joker = JokerType.CleverJoker
    val initial = GameState.initial

    val updated = Seq[Joker](joker).pickedBy(initial)

    updated.jokers should contain(joker)
    updated.jokers.size shouldBe initial.jokers.size + 1

  "PickFromPackPolicy for Planet" should "increase the picked planet hand type level" in:
    val planet = Planet.Earth
    val initial = GameState.initial

    val updated = Seq(planet).pickedBy(initial)

    updated.levels.getLevel(planet.handType) shouldBe
      initial.levels.getLevel(planet.handType) + 1

  "pickedBy" should "apply all picked items in order" in:
    val cards = Seq(Card(Ace, Spades), Card(King, Hearts))
    val initial = GameState.initial

    val updated = cards.pickedBy(initial)

    updated.deck.cards.takeRight(2) shouldBe cards
