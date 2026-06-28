package scalatro
package app

import model.game.{Blind, GameState}
import model.commons.Score.Score
import model.commons.{Joker, Pack, Planet, Card}
import model.round.Round
import model.shop.Shop

enum Model:
  case Playing
  case RoundWon(gameState: GameState)
  case RoundLost(blind: Blind, finalScore: Score)
  case InShop(gameState: GameState, shop: Shop)
  case OpeningPack(gameState: GameState, pack: OpenPack)

enum OpenPack:
  case Cards(pack: Pack[Card])
  case Planets(pack: Pack[Planet])
  case Jokers(pack: Pack[Joker])
