package scalatro
package app

import model.game.GameState
import model.commons.{Card, Deck, HandTypeLevels, Joker, Pack, Planet}
import model.round.Round
import model.shop.Shop

enum Model:
  case Playing
  case RoundWon(round: Round)
  case RoundLost(round: Round)
  case InShop(gameState: GameState, shop: Shop)
  case OpeningPack(gameState: GameState, pack: OpenPack)
  case ShowDeck(deck: Deck, previousState: Model)
  case ShowLevels(levels: HandTypeLevels, previousState: Model)

enum OpenPack:
  case Cards(pack: Pack[Card])
  case Planets(pack: Pack[Planet])
  case Jokers(pack: Pack[Joker])
