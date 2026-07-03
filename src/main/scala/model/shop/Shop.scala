package scalatro
package model.shop

import model.commons.*
import model.game.ShopInformation
import model.rng.SelectionPolicy.UniformSelection
import model.rng.{ScalatroRng, SelectionPolicy}

case class Shop(
    cardPack: Pack[Card],
    planetPack: Pack[Planet],
    jokerPack: Pack[Joker]
)

object Shop:

  def default(shopInformation: ShopInformation)(using ScalatroRng): Shop =
    given SelectionPolicy[Card] = UniformSelection[Card]
    given SelectionPolicy[Planet] = UniformSelection[Planet]
    given SelectionPolicy[Joker] = UniformSelection[Joker]
    Shop(
      CardsPack().smallPack,
      PlanetPack().smallPack,
      JokerPack().smallPack(shopInformation.jokers)
    )
