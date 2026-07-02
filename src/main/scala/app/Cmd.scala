package scalatro
package app

import model.game.GameState

enum Cmd:
  case NoOp
  case Deal(gameState: GameState)
  case BuildShop(gameState: GameState)
