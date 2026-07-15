package scalatro
package app

import model.game.GameState
import model.commons.{Card, Deck, HandTypeLevels, Joker, Pack, Planet}
import model.round.RoundState
import model.shop.Shop

/** The state of the application and the data associated with it.
  */
enum Model:
  /** A round is in progress */
  case Playing

  /** The round won state.
    *
    * @param roundState
    *   the just finished round
    */
  case RoundWon(roundState: RoundState)

  /** The round, and game, lost state.
    *
    * @param roundState
    *   the just finished round
    */
  case RoundLost(roundState: RoundState)

  /** The state assumed by the game inside the shop
    * @param gameState
    *   the current game state
    * @param shop
    *   the shop contents
    */
  case InShop(gameState: GameState, shop: Shop)

  /** The state assumed during a pack opening
    * @param gameState
    *   the current game state
    * @param pack
    *   the opened pack
    */
  case OpeningPack(gameState: GameState, pack: OpenPack)

  /** The state assumed when player inspects the deck
    * @param deck
    *   the deck to display
    * @param previousState
    *   the model to return to when the deck is closed
    */
  case ShowDeck(deck: Deck, previousState: Model)

  /** The state assumed when player inspects the hand levels.
    *
    * @param levels
    *   the hand type levels to display
    * @param previousState
    *   the model to return to when the levels are closed
    */
  case ShowLevels(levels: HandTypeLevels, previousState: Model)

/** An opened pack. */
enum OpenPack:
  /** A pack of playing cards.
    *
    * @param pack
    *   the pack
    */
  case Cards(pack: Pack[Card])

  /** A pack of planet cards.
    *
    * @param pack
    *   the pack
    */
  case Planets(pack: Pack[Planet])

  /** A pack of jokers.
    *
    * @param pack
    *   the pack
    */
  case Jokers(pack: Pack[Joker])
