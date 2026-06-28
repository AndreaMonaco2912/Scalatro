package scalatro
package model.shop

import model.commons.{
  Card,
  CardsPack,
  Joker,
  JokerPack,
  Pack,
  Planet,
  PlanetPack
}
import model.game.ShopInformation

import scala.util.Random

case class Shop(
    cardPack: Pack[Card],
    planetPack: Pack[Planet],
    jokerPack: Pack[Joker]
)

object Shop:

  def default(shopInformation: ShopInformation)(using Random): Shop =
    Shop(
      CardsPack.smallPack,
      PlanetPack.smallPack,
      JokerPack.smallPack(shopInformation.jokers)
    )

enum PackAction[+A]:
  case Select(item: A)
  case Skip

enum ShopSelection:
  case CardSelected(card: Card)
  case PlanetSelected(planet: Planet)
  case JokerSelected(joker: Joker)
