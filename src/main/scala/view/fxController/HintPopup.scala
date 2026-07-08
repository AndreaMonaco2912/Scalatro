package scalatro
package view.fxController

import model.commons.{Card, HandType, ScoreConfig}
import model.extra.Hint
import model.round.RoundState
import view.{ImageViews, Images}

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.image.ImageView
import javafx.scene.layout.{HBox, VBox}
import javafx.stage.{Modality, Stage}

/** A standalone popup that visualizes the best hand to play for a given
  * [[RoundState]]
  */
object HintPopup:

  /** Shows the popup synchronously (must be called on or will hop to the JavaFX
    * Application Thread).
    *
    * @param round
    *   the round whose hand is used to compute the best play
    */
  def show(round: RoundState): Unit =
    Platform.runLater { () =>
      val cards =
        given ScoreConfig = round.gameState.scoreConfig
        Hint.best(round.hand)
      buildAndShow(cards)
    }

  private def buildAndShow(cards: Seq[Card]): Unit =
    val stage = Stage()
    stage.initModality(Modality.APPLICATION_MODAL)
    stage.setTitle("Best Hand to Play")

    val root = VBox(16.0)
    root.getStyleClass.add("hint-popup")
    root.setAlignment(Pos.CENTER)

    val handType = HandType.detect(cards)
    val titleLabel = Label(s"Suggested: $handType")
    titleLabel.getStyleClass.add("hint-popup-title")
    titleLabel.setAlignment(Pos.CENTER)

    val cardsBox = HBox(8.0)
    cardsBox.getStyleClass.add("hint-popup-cards")
    cardsBox.setAlignment(Pos.CENTER)
    cards.foreach { card =>
      val iv = imageNode(Images.card(card))
      cardsBox.getChildren.add(iv)
    }

    val infoLabel = Label("These are the cards that maximize your score.")
    infoLabel.getStyleClass.add("hint-popup-info")
    infoLabel.setAlignment(Pos.CENTER)
    infoLabel.setWrapText(true)
    infoLabel.setMaxWidth(360)

    val closeButton = Button("Close")
    closeButton.getStyleClass.add("hint-popup-close-button")
    closeButton.setOnAction(_ => stage.close())

    root.getChildren.addAll(titleLabel, cardsBox, infoLabel, closeButton)

    val dialogScene = Scene(root, 540, 360)
    dialogScene.getStylesheets.add(
      getClass.getResource("/scalatro/styles.css").toExternalForm
    )
    stage.setScene(dialogScene)
    stage.showAndWait()

  private def imageNode(image: javafx.scene.image.Image): ImageView =
    ImageViews(image, 85, 125, Some("pack-card"))
