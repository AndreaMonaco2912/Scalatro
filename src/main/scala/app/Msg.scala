package scalatro
package app

import model.commons.{Card, CardOrderer}

object Msg:
  enum DefaultAction:
    case Pause

  /** An action the user can perform during the round */
  enum RoundAction:
    /** The action of playing a group of cards
     * @param cards
     *   the cards to play
     */
    case PlayCards(cards: Seq[Card])

    /** The action of discarding a group of cards
     * @param cards
     *   the cards to discard
     */
    case DiscardCards(cards: Seq[Card])

    /** The action of ordering cards according to a specific [[CardOrderer]]
     * @param orderer
     *   the card orderer
     */
    case OrderHand(orderer: CardOrderer)

  enum RoundEndAction:
    case NextRound
    case Restart

  enum ShopAction:
    case OpenCardPack
    case OpenPlanetPack
    case OpenJokerPack
    case SkipShop
