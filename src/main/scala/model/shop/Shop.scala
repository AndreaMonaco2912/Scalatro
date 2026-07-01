package scalatro
package model.shop

import model.commons.*
import model.game.ShopInformation
import model.rng.SelectionPolicy.UniformSelection
import model.rng.{ScalatroRng, PresetPolicies, SelectionPolicy}

case class Shop(
    cardPack: Pack[Card],
    planetPack: Pack[Planet],
    jokerPack: Pack[Joker]
)

object Shop:

  def default(shopInformation: ShopInformation)(using ScalatroRng): Shop =
    given SelectionPolicy[Card] = PresetPolicies.noFaces
    given SelectionPolicy[Planet] = PresetPolicies.pairBiasedPlanets
    given SelectionPolicy[Joker] = UniformSelection[Joker]
    Shop(
      CardsPack().smallPack,
      PlanetPack().smallPack,
      JokerPack().smallPack(shopInformation.jokers)
    )
