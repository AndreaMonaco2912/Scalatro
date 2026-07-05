package scalatro
package model.commons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.rng.{ScalatroRng, SelectionPolicy}
import model.rng.SelectionPolicy.UniformSelection
import model.extra.CardBuilder.*

class PackSpec extends AnyFlatSpec, Matchers:
  given SelectionPolicy[Card] = UniformSelection[Card]
  given ScalatroRng = ScalatroRng.default

  "A small pack" should "contain exactly 3 cards" in:
    CardsPack().smallPack.items.length shouldBe 3

  "A pack " should "not propose cards from the given blacklist" in:
    val blackList = Seq(A of S, A of D)
    val pack = CardsPack().smallPack(blackList)
    pack.items.foreach(card => blackList should not contain card)
