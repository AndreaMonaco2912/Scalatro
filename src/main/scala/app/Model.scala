package scalatro
package app

import model.game.{Blind, GameState}
import model.commons.Score.Score
import model.commons.{Card, Deck, Joker, Pack, Planet}
import model.round.Round
import model.shop.Shop

enum Model:
  case Playing
  case RoundWon(gameState: GameState)
  case RoundLost(gameState: GameState, finalScore: Score)
  case InShop(gameState: GameState, shop: Shop)
  case OpeningPack(gameState: GameState, pack: OpenPack)
  case ShowDeck(deck: Deck, previousState: Model)

enum OpenPack:
  case Cards(pack: Pack[Card])
  case Planets(pack: Pack[Planet])
  case Jokers(pack: Pack[Joker])
