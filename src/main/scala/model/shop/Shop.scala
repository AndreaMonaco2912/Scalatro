package scalatro
package model.shop

import model.commons.{Card, CardsPack, Pack, Planet, PlanetPack}

import scala.util.Random

case class Shop(cardPack: Pack[Card], planetPack: Pack[Planet])

object Shop:
  private val cardPackSize = 3
  private val planetPackSize = 3

  def default(using Random): Shop =
    Shop(CardsPack(cardPackSize), PlanetPack(planetPackSize))

enum ShopActions:
  case OpenPack(number: Int)
  case SkipShop
