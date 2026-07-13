package scalatro
package view.fx

import app.Msg.RoundEndAction
import model.round.RoundState

import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.VBox

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
abstract class FxRoundEndController extends Initializable, Dispatcher:

  @FXML private var deckHost: VBox = uninitialized

  @FXML private var roundStatsBox: VBox = uninitialized
  @FXML private var scoreLabel: Label = uninitialized
  @FXML private var targetScoreLabel: Label = uninitialized
  @FXML private var handsRemainingLabel: Label = uninitialized
  @FXML private var discardsRemainingLabel: Label = uninitialized

  private val clickableDeck = ClickableDeck(dispatch)

  protected def button: Button
  protected def message: RoundEndAction

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    button.setOnAction(_ => dispatch(message))
    clickableDeck.mount(deckHost)

  // TODO: valutare se mantenere hands e discards remaining solo nel round won
  // e se mostrare ulteriori informazioni nel round lost (es. blind raggiunto)
  def showStats(roundState: RoundState): Unit =
    Platform.runLater { () =>
      scoreLabel.setText(s"You scored: ${roundState.score.asDouble.toString}")
      targetScoreLabel.setText(
        s"Score at least: ${roundState.gameState.blindProgression.targetScore.asDouble.toString}"
      )
      handsRemainingLabel.setText(
        s"Remaining hands: ${roundState.remainingPlays.toString}"
      )
      discardsRemainingLabel.setText(
        s"Remaining discards: ${roundState.remainingDiscards.toString}"
      )
    }

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
