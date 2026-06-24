package scalatro
package view

import model.commons.*
import model.round.{Round, RoundAction}

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import eu.iamgio.animated.binding.value.AnimatedIntValueLabel
import eu.iamgio.animated.transition.container.AnimatedHBox
import javafx.animation.{
  Animation,
  FadeTransition,
  PauseTransition,
  ScaleTransition,
  SequentialTransition
}
import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label, TextField, ToggleButton}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.{HBox, StackPane}
import javafx.util.Duration
import scalafx.scene.control.{Label as SfxLabel, TextField as SfxTextField}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxController extends Initializable:

  // Card graphics
  @FXML private var card1: ImageView = uninitialized
  @FXML private var card2: ImageView = uninitialized
  @FXML private var card3: ImageView = uninitialized
  @FXML private var card4: ImageView = uninitialized
  @FXML private var card5: ImageView = uninitialized
  @FXML private var card6: ImageView = uninitialized
  @FXML private var card7: ImageView = uninitialized
  @FXML private var card8: ImageView = uninitialized

  // Card toggle buttons
  @FXML private var cardBtn1: ToggleButton = uninitialized
  @FXML private var cardBtn2: ToggleButton = uninitialized
  @FXML private var cardBtn3: ToggleButton = uninitialized
  @FXML private var cardBtn4: ToggleButton = uninitialized
  @FXML private var cardBtn5: ToggleButton = uninitialized
  @FXML private var cardBtn6: ToggleButton = uninitialized
  @FXML private var cardBtn7: ToggleButton = uninitialized
  @FXML private var cardBtn8: ToggleButton = uninitialized

  // Info labels
  @FXML private var roundNumLabel: Label = uninitialized
  @FXML private var roundScoreLabel: Label = uninitialized
  @FXML private var goalLabel: Label = uninitialized
  @FXML private var chipsLabel: AnimatedIntValueLabel = uninitialized
  @FXML private var multLabel: AnimatedIntValueLabel = uninitialized
  @FXML private var handLabel: Label = uninitialized
  @FXML private var handLevelLabel: Label = uninitialized
  @FXML private var deckLabel: Label = uninitialized
  @FXML private var handsRemainingLabel: Label = uninitialized
  @FXML private var discardsRemainingLabel: Label = uninitialized

  // Action buttons
  @FXML private var playButton: Button = uninitialized
  @FXML private var discardButton: Button = uninitialized
  @FXML private var sortRankButton: Button = uninitialized
  @FXML private var sortSuitButton: Button = uninitialized

  // Play area
  @FXML private var playAreaBox: HBox = uninitialized
  // Convenient grouped lists (order matters – matches FXML fx:id numbering)
  private def cardViews: List[ImageView] =
    List(card1, card2, card3, card4, card5, card6, card7, card8)

  private def cardButtons: List[ToggleButton] = List(
    cardBtn1,
    cardBtn2,
    cardBtn3,
    cardBtn4,
    cardBtn5,
    cardBtn6,
    cardBtn7,
    cardBtn8
  )

  private var actionQueue: Option[Queue[IO, RoundAction]] = None

  private var handSlots: List[(ToggleButton, Card)] = List()

  private var playAvailable: Boolean = false
  private var discardAvailable: Boolean = false

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    playButton.setOnAction(_ => onPlay())
    discardButton.setOnAction(_ => onDiscard())
    cardButtons.foreach(
      _.setOnAction(_ =>
        setHandType(selectedCards match
          case h :: _ => Some(HandType.detect(selectedCards))
          case _      => None)
      )
    )

    cardButtons.zipWithIndex.foreach { case (btn, index) =>
      btn.setOnDragDetected { event =>
        val db = btn.startDragAndDrop(TransferMode.MOVE)
        val content = new ClipboardContent()
        content.putString(index.toString)
        db.setContent(content)
        event.consume()
      }
      btn.setOnDragOver { event =>
        if event.getGestureSource != btn && event.getDragboard.hasString then
          event.acceptTransferModes(TransferMode.MOVE)
        event.consume()
      }
      btn.setOnDragDropped { event =>
        val db = event.getDragboard
        val success = if db.hasString then
          val draggedIdx = db.getString.toInt
          val targetIdx = index
          onOrder(CardOrderer.moveCard(draggedIdx, targetIdx))
          true
        else false

        event.setDropCompleted(success)
        event.consume()
      }
    }

    sortRankButton.setOnAction(_ => onOrder(CardOrderer.sortByRank))
    sortSuitButton.setOnAction(_ => onOrder(CardOrderer.sortBySuit))

  /** Injects the action queue. Must be called before the first render. */
  def setActionQueue(queue: Queue[IO, RoundAction]): Unit =
    actionQueue = Some(queue)

  private def selectedCards: Seq[Card] =
    handSlots.collect { case (btn, card) if btn.isSelected => card }

  extension (d: Double)
    private def customToString: String =
      if d % 1 == 0 then d.toInt.toString else d.toString

  private def disableButtons(): Unit =
    playButton.setDisable(true)
    discardButton.setDisable(true)

  private def enableButtons(): Unit =
    playButton.setDisable(true)
    discardButton.setDisable(true)

  private def setHandType(handType: Option[HandType]): Unit = handType match
    case Some(handType) =>
      chipsLabel.setValue(handType.baseScore.chips.toInt)
      multLabel.setValue(handType.baseScore.mult.toInt)
      handLabel.setText(handType.toString)
      playButton.setDisable(!playAvailable || selectedCards.isEmpty)
      discardButton.setDisable(!discardAvailable || selectedCards.isEmpty)
    case _ =>
      chipsLabel.setValue(0)
      multLabel.setValue(0)
      handLabel.setText("")
      playButton.setDisable(true)
      discardButton.setDisable(true)

  /** Update all UI nodes to reflect the new Round state. */
  def update(round: Round): Unit =
    Platform.runLater { () =>
      goalLabel.setText(
        round.gameState.blind.targetScore.asDouble.customToString
      )
      roundScoreLabel.setText(round.score.asDouble.customToString)
      deckLabel.setText(s"${round.deck.size} left")
      handsRemainingLabel.setText(round.remainingPlays.toString)
      discardsRemainingLabel.setText(round.remainingDiscards.toString)
      roundNumLabel.setText(s"Round ${round.gameState.blind.roundNum}")

      playAvailable = round.remainingPlays > 0
      playButton.setDisable(round.remainingPlays <= 0)
      discardAvailable = round.remainingDiscards > 0
      discardButton.setDisable(round.remainingDiscards <= 0)

      // Reset all card slots, then repopulate from the current hand
      cardViews.foreach(_.setImage(null))
      cardButtons.foreach { btn =>
        btn.setSelected(false)
        btn.setVisible(false)
      }

      handSlots = round.hand.toList.zip(cardButtons).map { case (card, btn) =>
        setCardImage(cardViews(cardButtons.indexOf(btn)), card)
        btn.setVisible(true)
        (btn, card)
      }

      setHandType(None)
    }

  private def moveCardsToPlayArea(cards: Seq[Card]): Seq[(Card, ImageView)] =
    cards.map { card =>
      val iv = new ImageView()
      setCardImage(iv, card)
      iv.setFitWidth(75)
      iv.setFitHeight(110)
      iv.setPreserveRatio(true)
      playAreaBox.getChildren.add(iv)
      (card, iv)
    }
  private def removeCardsFromPlayArea(): Unit =
    playAreaBox.getChildren.clear()

  private def getAnimation(
      card: Card,
      cardImage: ImageView,
      scoringCards: Seq[Card]
  ): Animation =
    if scoringCards.contains(card) then
      val scale = new ScaleTransition(Duration.millis(250), cardImage)
      scale.setFromX(1.0)
      scale.setFromY(1.0)
      scale.setToX(1.25)
      scale.setToY(1.25)
      scale.setCycleCount(2)
      scale.setAutoReverse(true)
      scale
    else
      val fade = new FadeTransition(Duration.millis(500), cardImage)
      fade.setFromValue(1.0)
      fade.setToValue(0.3)
      fade

  private def playSequentially(
      animations: Seq[Animation],
      onComplete: () => Unit
  ): Unit =
    animations match
      case Nil =>
        val finalPause = PauseTransition(Duration.millis(500))
        finalPause.setOnFinished(_ => onComplete())
        finalPause.play()
      case anim :: rest =>
        val pause = PauseTransition(Duration.millis(200))
        val sequence = SequentialTransition(pause, anim)
        sequence.setOnFinished(_ => playSequentially(rest, onComplete))
        sequence.play()

  private def onPlay(): Unit =
    val animations = for
      scoringCards = HandType.getScoringCards(selectedCards)
      (card, cardImage) <- moveCardsToPlayArea(selectedCards)
    yield getAnimation(card, cardImage, scoringCards)
    playSequentially(animations, () => removeCardsFromPlayArea())

    actionQueue.foreach(
      _.offer(RoundAction.PlayCards(selectedCards)).unsafeRunAndForget()
    )

  private def onDiscard(): Unit =
    actionQueue.foreach(
      _.offer(RoundAction.DiscardCards(selectedCards)).unsafeRunAndForget()
    )

  private def onOrder(cardOrderer: CardOrderer): Unit =
    actionQueue.foreach(
      _.offer(RoundAction.OrderHand(cardOrderer)).unsafeRunAndForget()
    )

  private def setCardImage(imageView: ImageView, card: Card): Unit =
    val rankString = card.rank match
      case Rank.Two   => "2"
      case Rank.Three => "3"
      case Rank.Four  => "4"
      case Rank.Five  => "5"
      case Rank.Six   => "6"
      case Rank.Seven => "7"
      case Rank.Eight => "8"
      case Rank.Nine  => "9"
      case Rank.Ten   => "10"
      case other      => other
    val imagePath = s"/scalatro/cards/${rankString}_of_${card.suit}.png"
    val image = new Image(getClass.getResourceAsStream(imagePath))
    imageView.setImage(image)

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class StartController extends Initializable:
  @FXML private var mirror: Label = uninitialized
  @FXML private var input: TextField = uninitialized

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit =
    val sfxMirror = new SfxLabel(mirror)
    val sfxInput = new SfxTextField(input)
    sfxMirror.text <== sfxInput.text
