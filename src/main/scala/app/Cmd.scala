package scalatro
package app

import model.game.GameState

/** A consequence of a message sent to a certain model that can't be completely
  * run inside [[Update]].
  */
enum Cmd:
  /** The command performing no effect. */
  case NoOp

  /** Starts a new round from the given game state.
    * @param gameState
    *   the game state of the match
    */
  case Deal(gameState: GameState)

  /** Generates a shop for the given game state.
    * @param gameState
    *   the game state of the match
    */
  case BuildShop(gameState: GameState)

  /** Starts the First Round
    */
  case DealFirstRound
