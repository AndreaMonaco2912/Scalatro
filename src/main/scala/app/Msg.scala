package scalatro
package app

import model.commons.{Card, CardOrderer, Joker, Planet}
import model.game.GameState
import model.round.Round
import model.shop.Shop

sealed trait Msg

object Msg:
  enum ManagementAction extends Msg:
    case ShowDeck
    case CloseDeck
    case ShowLevels
    case CloseLevels

  /** An action the user can perform during the round */
  enum RoundAction extends Msg:
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

  enum RoundEndAction extends Msg:
    case NextRound
    case Restart

  enum ShopAction extends Msg:
    case OpenCardPack
    case OpenPlanetPack
    case OpenJokerPack
    case SkipShop

  enum PackSelection extends Msg:
    case SelectCard(card: Card)
    case SelectPlanet(planet: Planet)
    case SelectJoker(joker: Joker)
    case SkipPack

  enum InternalEffect extends Msg:
    case RoundWon(round: Round)
    case RoundLost(round: Round)
    case ShopReady(gameState: GameState, shop: Shop)
