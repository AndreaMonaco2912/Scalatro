package scalatro
package app

import model.commons.{Card, Joker, Orderer, Planet}
import model.game.GameState
import model.round.RoundState
import model.shop.Shop

/** An internally exchanged message that causes Changes on game [[Model]]
  * without the need of any further information, it is usually caused by a
  * player move or an and of a computation.
  */
sealed trait Msg

object Msg:
  /** An action for inspecting some game information that is reversible. It
    * doesn't cause persistent modification
    */
  enum ManagementAction extends Msg:
    /** The action of opening the deck screen */
    case ShowDeck

    /** The action of closing the deck screen */
    case CloseDeck

    /** The action of opening the hand levels screen */
    case ShowLevels

    /** The action of closing the hand levels screen */
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

    /** The action of ordering cards according to a specific [[Orderer]]
      *
      * @param orderer
      *   the card orderer
      */
    case OrderHand(orderer: Orderer[Card])

    /** The action of ordering jokers according to a specific [[Orderer]]
      *
      * @param orderer
      *   the joker orderer
      */
    case OrderJoker(orderer: Orderer[Joker])

  /** An action the user performs only on the round end screens */
  enum RoundEndAction extends Msg:
    /** The action of going to the next round */
    case NextRound

    /** The action of restarting the game from zero after a lost */
    case Restart

  /** An action the user perform only in the shop screen */
  enum ShopAction extends Msg:
    /** The action of opening a Card Pack */
    case OpenCardPack

    /** The action of opening a Planet Pack */
    case OpenPlanetPack

    /** The action of opening a Joker Pack */
    case OpenJokerPack

    /** The action of skipping the shop */
    case SkipShop

  /** An action the user perform only inside a pack screen */
  enum PackSelection extends Msg:
    /** The action of selecting a card from the pack
      *
      * @param card
      *   the selected card
      */
    case SelectCard(card: Card)

    /** The action of selecting a planet from the pack
      *
      * @param planet
      *   the selected planet
      */
    case SelectPlanet(planet: Planet)

    /** The action of selecting a joker from the pack
      *
      * @param joker
      *   the selected joker
      */
    case SelectJoker(joker: Joker)

    /** The action of skipping the pack without a selection */
    case SkipPack

  /** A message not directly caused by the player, but emitted after the process
    * of a [[Cmd]]
    */
  enum InternalEffect extends Msg:
    /** The round finished with the target score beaten
      *
      * @param roundState
      *   the finished round
      */
    case RoundWon(roundState: RoundState)

    /** The round finished without beating the target score
      *
      * @param roundState
      *   the finished round
      */
    case RoundLost(roundState: RoundState)

    /** The shop has been generated and is ready to display
      *
      * @param gameState
      *   the game state the shop was built from
      * @param shop
      *   the generated shop
      */
    case ShopReady(gameState: GameState, shop: Shop)
