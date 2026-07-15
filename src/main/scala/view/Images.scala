package scalatro
package view

import model.commons.{Card, Joker, Planet, Rank}

import javafx.scene.image.{Image, ImageView}
import model.game.Blind

/** Loaders for the game's image assets. */
object Images:

  private def load(path: String): Image =
    new Image(getClass.getResourceAsStream(path))

  /** The image of a playing card.
    *
    * @param card
    *   the card
    * @return
    *   the image
    */
  def card(card: Card): Image =
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
    load(Resources.card(s"${rankString}_of_${card.suit}"))

  /** The image of a joker.
    *
    * @param joker
    *   the joker
    * @return
    *   the image
    */
  def joker(joker: Joker): Image =
    load(Resources.joker(joker.name.replace(" ", "_")))

  /** The image of a planet card.
    *
    * @param planet
    *   the planet
    * @return
    *   the image
    */
  def planet(planet: Planet): Image =
    load(Resources.planet(planet.name.replace(" ", "_")))

  /** The image of a pack.
    *
    * @param category
    *   the pack category (e.g. "Standard")
    * @param size
    *   the pack size (e.g. "Normal")
    * @param version
    *   the artwork version
    * @return
    *   the image
    */
  def pack(category: String, size: String, version: Int): Image =
    load(Resources.pack(s"${category}_${size}_$version"))

  /** The image of the deck back.
    *
    * @return
    *   the image
    */
  def deckBack: Image = load(Resources.deckBack)

  /** The image of a blind.
    *
    * @param blind
    *   the blind
    * @return
    *   the image
    */
  def blind(blind: Blind): Image = load(
    Resources.blind(blind.name.replace(" ", "_"))
  )

/** A factory of preconfigured [[javafx.scene.image.ImageView]]s. */
object ImageViews:
  /** Creates an image view of the given size, preserving the aspect ratio.
    *
    * @param image
    *   the image to display
    * @param width
    *   the fit width
    * @param height
    *   the fit height
    * @param styleClass
    *   an optional CSS style class to add
    * @return
    *   the image view
    */
  def apply(
      image: Image,
      width: Double,
      height: Double,
      styleClass: Option[String] = None
  ): ImageView =
    val iv = new ImageView(image)
    iv.setFitWidth(width)
    iv.setFitHeight(height)
    iv.setPreserveRatio(true)
    styleClass.foreach(iv.getStyleClass.add)
    iv
