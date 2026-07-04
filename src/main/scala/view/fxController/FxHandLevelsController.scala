package scalatro
package view.fxController

import app.Msg.ManagementAction
import model.commons.*

import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.geometry.Pos
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{HBox, VBox}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxHandLevelsController extends Initializable, Dispatcher:
  @FXML private var levelsBox: VBox = uninitialized
  @FXML private var backButton: Button = uninitialized

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    backButton.setOnAction(_ => dispatch(ManagementAction.CloseLevels))

  def showLevels(levels: HandTypeLevels): Unit =
    Platform.runLater { () =>
      levelsBox.getChildren.clear()
      HandType.values.foreach { handType =>
        val score = Score.getHandTypeBaseScore(handType, levels)

        val nameLabel = new Label(handType.toString)
        nameLabel.setPrefWidth(160)
        val levelLabel = new Label(s"lvl.${levels.getLevel(handType)}")
        val scoreLabel =
          new Label(s"${score.chips.asDouble.toInt} x ${score.mult.asDouble.toInt}")

        val row = new HBox(16, nameLabel, levelLabel, scoreLabel)
        row.setAlignment(Pos.CENTER_LEFT)
        row.getStyleClass.add("hand-level-row")
        levelsBox.getChildren.add(row)
      }
    }
