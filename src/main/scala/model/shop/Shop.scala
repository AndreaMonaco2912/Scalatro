package scalatro
package model.shop

import model.commons.*
import model.game.ShopInformation
import model.rng.SelectionPolicies
import model.rng.{ScalatroRng, SelectionPolicy}

/** The contents of the shop: one pack per item category.
  * @param cardPack
  *   the pack of cards
  * @param planetPack
  *   the pack of planets
  * @param jokerPack
  *   the pack of jokers
  */
case class Shop(
    cardPack: Pack[Card],
    planetPack: Pack[Planet],
    jokerPack: Pack[Joker]
)

object Shop:

  /** Creates the default shop, with a small pack per category. Jokers already
    * owned by the player are excluded from the joker pack.
    *
    * @param shopInformation
    *   the game information influencing pack generation
    * @param rng
    *   the random number generator
    * @param selectionPolicies
    *   the selection policies for each category
    * @return
    *   the shop
    */
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
