package scalatro
package model.shop

import model.commons.*
import model.game.ShopInformation
import model.rng.SelectionPolicies
import model.rng.{ScalatroRng, SelectionPolicy}

case class Shop(
    cardPack: Pack[Card],
    planetPack: Pack[Planet],
    jokerPack: Pack[Joker]
)

object Shop:

  def default(shopInformation: ShopInformation)(using
      rng: ScalatroRng,
      selectionPolicies: SelectionPolicies
  ): Shop =
    given SelectionPolicy[Card] = selectionPolicies.cardPolicy
    given SelectionPolicy[Planet] = selectionPolicies.planetPolicy
    given SelectionPolicy[Joker] = selectionPolicies.jokerPolicy
    Shop(
      CardsPack().smallPack,
      PlanetPack().smallPack,
      JokerPack().smallPack(shopInformation.jokers)
    )
