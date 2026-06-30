package scalatro
package view.fxController

import app.Msg.ManagementAction
import model.commons.{Card, Deck}
import view.Images

import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxDeckController extends Initializable, Dispatcher:
  @FXML private var deckPane: FlowPane = uninitialized
  @FXML private var backButton: Button = uninitialized

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    backButton.setOnAction(_ => dispatch(ManagementAction.CloseDeck))

  def showCards(cards: Deck): Unit =
    Platform.runLater { () =>
      deckPane.getChildren.clear()
      cards.foreach { card =>
        val iv = new ImageView(Images.card(card))
        iv.setFitWidth(60)
        iv.setFitHeight(88)
        iv.setPreserveRatio(true)
        iv.getStyleClass.add("pack-card")
        deckPane.getChildren.add(iv)
      }
    }
