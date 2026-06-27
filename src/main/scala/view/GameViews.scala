package scalatro
package view

import view.fxController.{
  FxCardPackController,
  FxController,
  FxJokerPackController,
  FxPlanetPackController,
  FxRoundLostController,
  FxRoundWonController,
  FxShopController
}

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

  def gameplay: IO[FxController] =
    switchTo[FxController]("/scalatro/scene.fxml")

  def roundWon: IO[FxRoundWonController] =
    switchTo[FxRoundWonController]("/scalatro/roundWon.fxml")

  def roundLost: IO[FxRoundLostController] =
    switchTo[FxRoundLostController]("/scalatro/roundLost.fxml")

  def shop: IO[FxShopController] =
    switchTo[FxShopController]("/scalatro/shop.fxml")

  def cardPack: IO[FxCardPackController] =
    switchTo[FxCardPackController]("/scalatro/cardPack.fxml")

  def planetPack: IO[FxPlanetPackController] =
    switchTo[FxPlanetPackController]("/scalatro/planetPack.fxml")

  def jokerPack: IO[FxJokerPackController] =
    switchTo[FxJokerPackController]("/scalatro/jokerPack.fxml")
