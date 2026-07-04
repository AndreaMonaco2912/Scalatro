package scalatro
package view.fxController

import model.commons.*
import model.round.RoundState
import app.Msg.RoundAction
import view.{ImageViews, Images}

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import javafx.animation.*
import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.HBox
import javafx.util.Duration
import scalafx.scene.control.{Label as SfxLabel, TextField as SfxTextField}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class FxController extends Initializable, Bindable[RoundAction]:

  // Joker and hand slots
  @FXML private var jokerSlotsBox: HBox = uninitialized
  @FXML private var handSlotsBox: HBox = uninitialized

  // Info labels
  @FXML private var roundNumLabel: Label = uninitialized
  @FXML private var roundScoreLabel: Label = uninitialized
  @FXML private var goalLabel: Label = uninitialized
  @FXML private var chipsLabel: Label = uninitialized
  @FXML private var multLabel: Label = uninitialized
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

  private var handSlots: List[(ToggleButton, Card)] = List()

  private var lastKnownRoundState: Option[RoundState] = None
  private var cardOrderer: Option[CardOrderer] = None

  private def playAvailable: Boolean = lastKnownRoundState match
    case Some(round) => round.remainingPlays > 0
    case _           => false
  private def discardAvailable: Boolean = lastKnownRoundState match
    case Some(round) => round.remainingDiscards > 0
    case _           => false

  private case class LevelledHandType(
      handType: HandType,
      handScore: HandScore,
      level: Level
  )

  extension (cards: Seq[Card])
    private def levelledHandType: LevelledHandType =
      val handType = HandType.detect(cards)
      val levels: HandTypeLevels = lastKnownRoundState match
        case Some(round) => round.gameState.levels
        case _           => HandTypeLevels.initial
      val handTypeLevel = levels.getLevel(handType)
      val handScore = Score.getHandTypeBaseScore(handType, levels)
      LevelledHandType(handType, handScore, handTypeLevel)

  override def initialize(url: URL, rb: ResourceBundle): Unit =
    playButton.setOnAction(_ => onPlay())
    discardButton.setOnAction(_ => onDiscard())

    sortRankButton.setOnAction(_ =>
      val ord = CardOrderer.sortByRank
      cardOrderer = Some(ord)
      onOrder(ord)
    )
    sortSuitButton.setOnAction(_ =>
      val ord = CardOrderer.sortBySuit
      cardOrderer = Some(ord)
      onOrder(ord)
    )

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

  private def setHandType(levelledHandType: Option[LevelledHandType]): Unit =
    levelledHandType match
      case Some(handType) =>
        chipsLabel.setText(handType.handScore.chips.asDouble.customToString)
        multLabel.setText(handType.handScore.mult.asDouble.customToString)
        handLabel.setText(handType.handType.toString)
        handLevelLabel.setText(s"lvl.${handType.level}")
        val numSelectedCards = selectedCards.length
        playButton.setDisable(
          !playAvailable || selectedCards.isEmpty || numSelectedCards > 5
        )
        discardButton.setDisable(
          !discardAvailable || selectedCards.isEmpty || numSelectedCards > 5
        )
      case _ =>
        chipsLabel.setText("0")
        multLabel.setText("0")
        handLabel.setText("")
        handLevelLabel.setText("")
        playButton.setDisable(true)
        discardButton.setDisable(true)

  /** Update all UI nodes to reflect the new Round state. */
  def update(roundState: RoundState): Unit =
    Platform.runLater { () =>
      goalLabel.setText(
        roundState.gameState.blind.targetScore.asDouble.customToString
      )
      roundScoreLabel.setText(roundState.score.asDouble.customToString)
      deckLabel.setText(s"${roundState.deck.size} left")
      handsRemainingLabel.setText(roundState.remainingPlays.toString)
      discardsRemainingLabel.setText(roundState.remainingDiscards.toString)
      roundNumLabel.setText(s"Round ${roundState.gameState.blind.roundNum}")

      lastKnownRoundState = Some(roundState)
      playButton.setDisable(roundState.remainingPlays <= 0)
      discardButton.setDisable(roundState.remainingDiscards <= 0)

      // Cards overlap
      val handSize = roundState.hand.size
      val maxNormalCards = 8
      if handSize > maxNormalCards then
        val negativeSpacing = -15.0 - ((handSize - maxNormalCards) * 2.0)
        handSlotsBox.setSpacing(negativeSpacing)
      else handSlotsBox.setSpacing(5.0)

      // Rebuild the card slots from the current hand, mirroring jokerSlotsBox
      handSlotsBox.getChildren.clear()
      handSlots = roundState.hand.toList.zipWithIndex.map { case (card, index) =>
        val btn = renderCardButton(card, index)
        handSlotsBox.getChildren.add(btn)
        (btn, card)
      }

      // Jokers overlap
      val numJokers = roundState.gameState.jokers.length
      val maxNormalJokers = 7
      if numJokers > maxNormalJokers then
        val negativeSpacing = -15.0 - ((numJokers - maxNormalJokers) * 2.0)
        jokerSlotsBox.setSpacing(negativeSpacing)
      else jokerSlotsBox.setSpacing(15.0)

      jokerSlotsBox.getChildren.clear()
      roundState.gameState.jokers.foreach(joker =>
        val node = renderJoker(joker)
        jokerSlotsBox.getChildren.add(node)
      )

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
      val SCALE_ANIMATION_DURATION = 125
      val scale = new ScaleTransition(
        Duration.millis(SCALE_ANIMATION_DURATION),
        cardImage
      )
      scale.setFromX(1.0)
      scale.setFromY(1.0)
      scale.setToX(1.25)
      scale.setToY(1.25)
      scale.setCycleCount(2)
      scale.setAutoReverse(true)
      scale
    else
      val FADE_ANIMATION_DURATION = 250
      val fade =
        new FadeTransition(Duration.millis(FADE_ANIMATION_DURATION), cardImage)
      fade.setFromValue(1.0)
      fade.setToValue(0.3)
      fade

  private def playSequentially(
      animations: Seq[Animation],
      onComplete: () => Unit
  ): Unit =
    animations match
      case Nil =>
        val FINAL_PAUSE_DURATION = 500
        val finalPause = PauseTransition(Duration.millis(FINAL_PAUSE_DURATION))
        finalPause.setOnFinished(_ => onComplete())
        finalPause.play()
      case anim :: rest =>
        val BETWEEN_CARDS_PAUSE_DURATION = 100
        val pause = PauseTransition(
          Duration.millis(BETWEEN_CARDS_PAUSE_DURATION)
        )
        val sequence = SequentialTransition(pause, anim)
        sequence.setOnFinished(_ => playSequentially(rest, onComplete))
        sequence.play()

  private def onPlay(): Unit =
    val animations = for
      scoringCards = HandType.getScoringCards(selectedCards)
      (card, cardImage) <- moveCardsToPlayArea(selectedCards)
    yield getAnimation(card, cardImage, scoringCards)
    playSequentially(
      animations,
      () =>
        removeCardsFromPlayArea()
        offer(RoundAction.PlayCards(selectedCards))
        cardOrderer.foreach(onOrder)
    )

  private def onDiscard(): Unit =
    offer(RoundAction.DiscardCards(selectedCards))
    cardOrderer.foreach(onOrder)

  private def onOrder(cardOrderer: CardOrderer): Unit =
    offer(RoundAction.OrderHand(cardOrderer))

  protected def imageNode(image: Image): ImageView =
    ImageViews(image, 85, 125, Some("pack-card"))

  private def renderJoker(joker: Joker): Node =
    val iv = imageNode(Images.joker(joker))
    Tooltip.install(iv, new Tooltip(joker.description))
    iv

  private def setCardImage(imageView: ImageView, card: Card): Unit =
    imageView.setImage(Images.card(card))

  private def renderCardButton(card: Card, index: Int): ToggleButton =
    val iv = imageNode(Images.card(card))

    val btn = new ToggleButton()
    btn.getStyleClass.add("card-button")
    btn.setGraphic(iv)
    btn.setOnAction(_ =>
      setHandType(selectedCards match
        case _ :: _ => Some(selectedCards.levelledHandType)
        case _      => None)
    )
    setupCardDragAndDrop(btn, index)
    btn

  private def setupCardDragAndDrop(btn: ToggleButton, index: Int): Unit =
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
        cardOrderer = None
        onOrder(CardOrderer.moveCard(draggedIdx, targetIdx))
        true
      else false

      event.setDropCompleted(success)
      event.consume()
    }
