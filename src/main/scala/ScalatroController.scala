package scalatro

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Label, TextField}
import javafx.scene.image.{Image, ImageView}
import scalafx.scene.control.{Label as SfxLabel, TextField as SfxTextField}
import model.commons.{Deck, Rank}

import java.net.URL
import java.util.ResourceBundle
import scala.compiletime.uninitialized
import scala.util.Random

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class ScalatroController extends Initializable:

  @FXML private var card1: ImageView = uninitialized
  @FXML private var card2: ImageView = uninitialized
  @FXML private var card3: ImageView = uninitialized
  @FXML private var card4: ImageView = uninitialized
  @FXML private var card5: ImageView = uninitialized
  @FXML private var card6: ImageView = uninitialized
  @FXML private var card7: ImageView = uninitialized
  @FXML private var card8: ImageView = uninitialized

  override def initialize(url: URL, rb: ResourceBundle): Unit = {
    given Random = new Random()
    val startingDeck = Deck().shuffle

    val (hand, remainingDeck) = startingDeck.draw(8)

    val imageViews = List(card1, card2, card3, card4, card5, card6, card7, card8)

    hand.zip(imageViews).foreach { case (card, imageView) =>
      val rankString = card.rank match
        case Rank.Two => "2"
        case Rank.Three => "3"
        case Rank.Four => "4"
        case Rank.Five => "5"
        case Rank.Six => "6"
        case Rank.Seven => "7"
        case Rank.Eight => "8"
        case Rank.Nine => "9"
        case Rank.Ten => "10"
        case other => other
      val imagePath = s"/scalatro/cards/${rankString}_of_${card.suit}.png"
      val cardImage = new Image(getClass.getResourceAsStream(imagePath))
      imageView.setImage(cardImage)
    }
  }

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class StartController extends Initializable:
  @FXML private var mirror: Label = uninitialized
  @FXML private var input: TextField = uninitialized

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit =
    val sfxMirror = new SfxLabel(mirror)
    val sfxInput = new SfxTextField(input)
    sfxMirror.text <== sfxInput.text
