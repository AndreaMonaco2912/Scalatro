package scalatro
package view.fxController

import app.Msg.RoundEndAction

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Button
import javafx.scene.layout.VBox

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
abstract class FxRoundEndController
    extends Initializable,
      Dispatcher,
      ClickableDeck:

  @FXML private var deckHost: VBox = uninitialized

  protected def button: Button
  protected def message: RoundEndAction

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    button.setOnAction(_ => dispatch(message))
    mountDeck(deckHost)

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundWonController extends FxRoundEndController:
  @FXML private var nextRoundButton: Button = uninitialized
  override protected def button: Button = nextRoundButton
  override protected def message: RoundEndAction = RoundEndAction.NextRound

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxRoundLostController extends FxRoundEndController:
  @FXML private var restartButton: Button = uninitialized
  override protected def button: Button = restartButton
  override protected def message: RoundEndAction = RoundEndAction.Restart
