package scalatro
package model.shop

import model.commons.{Card, CardsPack, Joker, JokerPack, Pack, Planet, PlanetPack}

import scala.util.Random

case class Shop(cardPack: Pack[Card], planetPack: Pack[Planet], jokerPack: Pack[Joker])

object Shop:

  def default(using Random): Shop =
    Shop(CardsPack.smallPack, PlanetPack.smallPack, JokerPack.smallPack)

enum ShopActions:
  case OpenCardPack
  case OpenPlanetPack
  case OpenJokerPack
  case SkipShop
