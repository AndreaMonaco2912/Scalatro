package scalatro
package model.rng

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.SelectionPolicy.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

@SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
class ScalatroRngSpec extends AnyFlatSpec, Matchers:
  "ScalatroRng" should "use independent Random instances per weighable type regardless of call order" in:
    val cards =
      Pool(Seq(Card(Ten, Spades), Card(Ace, Hearts), Card(Queen, Clubs)))
    val jokers = Pool(Seq(JokerType.CleverJoker, JokerType.CraftyJoker))
    val planets = Pool(Seq(Planet.Mercury, Planet.Earth, Planet.Mars))

    val rng1 = ScalatroRng(Seed(123L))
    val card1 = rng1.draw(cards, 1)(using UniformSelection[Card]()).head
    val joker1 = rng1.draw(jokers, 1)(using UniformSelection[Joker]()).head
    val planet1 = rng1.draw(planets, 1)(using UniformSelection[Planet]()).head

    val rng2 = ScalatroRng(Seed(123L))
    val planet2 = rng2.draw(planets, 1)(using UniformSelection[Planet]()).head
    val joker2 = rng2.draw(jokers, 1)(using UniformSelection[Joker]()).head
    val card2 = rng2.draw(cards, 1)(using UniformSelection[Card]()).head

    card1 shouldBe card2
    joker1 shouldBe joker2
    planet1 shouldBe planet2
