package scalatro
package model.shop

import model.commons.{Deck, HandTypeLevels, Joker, JokerPack, JokerType}
import model.game.ShopInformation

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class ShopTest extends AnyFlatSpec, Matchers:

  given Random = Random(0L)

  private val smallPackSize = 3

  private def shopOwning(blackList: Seq[Joker]): Shop =
    Shop.default(ShopInformation(Deck(), HandTypeLevels.initial, blackList))

  "default" should "offer a card pack of the small pack size" in:
    shopOwning(Seq.empty).cardPack.items.length shouldBe smallPackSize

  "default" should "offer a planet pack of the small pack size" in:
    shopOwning(Seq.empty).planetPack.items.length shouldBe smallPackSize

  "default" should "offer a joker pack of the small pack size" in:
    shopOwning(Seq.empty).jokerPack.items.length shouldBe smallPackSize

  it should "exclude owned jokers from the joker pack" in:
    val owned = Seq(JokerType.CleverJoker, JokerType.CraftyJoker)
    shopOwning(owned).jokerPack.items.foreach(joker =>
      owned should not contain joker
    )

  it should "offer an empty joker pack when every joker is owned" in:
    shopOwning(JokerPack.pool).jokerPack.items shouldBe empty
