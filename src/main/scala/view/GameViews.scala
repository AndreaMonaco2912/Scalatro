package scalatro
package view

import view.fx.*

import cats.effect.IO
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}

/** Loads FXML screens and swaps the scene root to the loaded one.
  *
  * @param scene
  *   the application scene whose root gets replaced
  */
class GameViews(scene: Scene):

  private def switchTo[C](fxml: String): IO[C] =
    IO.async_ { cb =>
      Platform.runLater { () =>
        try
          val loader = new FXMLLoader(getClass.getResource(fxml))
          val root: Parent = loader.load()
          scene.setRoot(root)
          cb(Right(loader.getController[C]))
        catch case t: Throwable => cb(Left(t))
      }
    }

  /** Loads the gameplay screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def gameplay: IO[FxController] = switchTo(Resources.Fxml.gameplay)

  /** Loads the round won screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def roundWon: IO[FxRoundWonController] = switchTo(Resources.Fxml.roundWon)

  /** Loads the round lost screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def roundLost: IO[FxRoundLostController] = switchTo(Resources.Fxml.roundLost)

  /** Loads the shop screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def shop: IO[FxShopController] = switchTo(Resources.Fxml.shop)

  /** Loads the card pack screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def cardPack: IO[FxCardPackController] = switchTo(Resources.Fxml.cardPack)

  /** Loads the planet pack screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def planetPack: IO[FxPlanetPackController] =
    switchTo(Resources.Fxml.planetPack)

  /** Loads the joker pack screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def jokerPack: IO[FxJokerPackController] = switchTo(Resources.Fxml.jokerPack)

  /** Loads the deck screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def deck: IO[FxDeckController] = switchTo(Resources.Fxml.deck)

  /** Loads the hand levels screen.
    *
    * @return
    *   an IO yielding its controller
    */
  def handLevels: IO[FxHandLevelsController] = switchTo(
    Resources.Fxml.handLevels
  )
