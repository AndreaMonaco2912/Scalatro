package scalatro
package view

import view.fxController.*

import cats.effect.IO
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}

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

  def gameplay: IO[FxController] = switchTo(Resources.Fxml.gameplay)

  def roundWon: IO[FxRoundWonController] = switchTo(Resources.Fxml.roundWon)

  def roundLost: IO[FxRoundLostController] = switchTo(Resources.Fxml.roundLost)

  def shop: IO[FxShopController] = switchTo(Resources.Fxml.shop)

  def cardPack: IO[FxCardPackController] = switchTo(Resources.Fxml.cardPack)

  def planetPack: IO[FxPlanetPackController] =
    switchTo(Resources.Fxml.planetPack)

  def jokerPack: IO[FxJokerPackController] = switchTo(Resources.Fxml.jokerPack)

  def deck: IO[FxDeckController] = switchTo(Resources.Fxml.deck)
