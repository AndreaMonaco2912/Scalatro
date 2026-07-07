package scalatro
package model.rng

import model.commons.*
import model.commons.Rank.*
import model.commons.Suit.*
import model.rng.SelectionPolicy.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SelectionPolicySpec extends AnyFlatSpec, Matchers:
  "UniformSelection" should "assign weight 1.0 for any element" in:
    val policy = UniformSelection[Card]()
    policy.weight(Card(Ten, Spades)).value shouldBe 1.0

  "BoostRank" should "boost weight for matching rank only" in:
    val policy = new UniformSelection[Card]
      with BoostRank(Rank.Ace, Weight(3.0))
    policy.weight(Card(Rank.Ace, Hearts)).value shouldBe 3.0
    policy.weight(Card(Rank.Ten, Hearts)).value shouldBe 1.0

  "BoostSuit" should "boost weight for matching suit only" in:
    val policy = new UniformSelection[Card]
      with BoostSuit(Suit.Spades, Weight(5.0))
    policy.weight(Card(Rank.Ten, Suit.Spades)).value shouldBe 5.0
    policy.weight(Card(Rank.Ten, Suit.Hearts)).value shouldBe 1.0

  "BoostCard" should "boost a specific card" in:
    val card = Card(Rank.Ten, Suit.Spades)
    val policy = new UniformSelection[Card] with BoostCard(card, Weight(4.0))
    policy.weight(card).value shouldBe 4.0
    policy.weight(Card(Rank.Ten, Suit.Hearts)).value shouldBe 1.0

  "BoostFaces" should "boost face cards only" in:
    val policy = new UniformSelection[Card] with BoostFaces(Weight(2.0))
    policy.weight(Card(Rank.Jack, Suit.Clubs)).value shouldBe 2.0
    policy.weight(Card(Rank.Ten, Suit.Clubs)).value shouldBe 1.0

  "BoostJoker" should "boost specific joker type only" in:
    val policy = new UniformSelection[Joker]
      with BoostJoker(JokerType.CleverJoker, Weight(4.0))
    policy.weight(JokerType.CleverJoker).value shouldBe 4.0
    policy.weight(JokerType.CraftyJoker).value shouldBe 1.0

  "BoostPlanetHandType" should "boost planets with matching hand type" in:
    val policy = new UniformSelection[Planet]
      with BoostPlanetHandType(HandType.Pair, Weight(100.0))
    policy.weight(Planet.Mercury).value shouldBe 100.0
    policy.weight(Planet.Pluto).value shouldBe 1.0
