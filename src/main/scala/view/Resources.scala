package scalatro
package view

/** The classpath locations of the application resources. */
object Resources:
  private val baseResource = "/scalatro/view"
  private val baseView = baseResource + "/fx"

  /** The paths of the FXML screen definitions. */
  object Fxml:
    /** The gameplay screen. */
    val gameplay: String = s"$baseView/scene.fxml"

    /** The round won screen. */
    val roundWon: String = s"$baseView/roundWon.fxml"

    /** The round lost screen. */
    val roundLost: String = s"$baseView/roundLost.fxml"

    /** The shop screen. */
    val shop: String = s"$baseView/shop.fxml"

    /** The card pack screen. */
    val cardPack: String = s"$baseView/cardPack.fxml"

    /** The planet pack screen. */
    val planetPack: String = s"$baseView/planetPack.fxml"

    /** The joker pack screen. */
    val jokerPack: String = s"$baseView/jokerPack.fxml"

    /** The deck screen. */
    val deck: String = s"$baseView/deck.fxml"

    /** The hand levels screen. */
    val handLevels: String = s"$baseView/handLevels.fxml"

  /** The application stylesheet. */
  val stylesheet: String = s"$baseView/styles.css"

  /** The image path of a playing card.
    * @param name
    *   the card image name
    * @return
    *   the path
    */
  def card(name: String): String = s"$baseResource/cards/$name.png"

  /** The image path of a joker.
    * @param name
    *   the joker image name
    * @return
    *   the path
    */
  def joker(name: String): String = s"$baseResource/jokers/$name.png"

  /** The image path of a planet card.
    * @param name
    *   the planet image name
    * @return
    *   the path
    */
  def planet(name: String): String = s"$baseResource/planets/$name.png"

  /** The image path of a pack.
    * @param name
    *   the pack image name
    * @return
    *   the path
    */
  def pack(name: String): String = s"$baseResource/packs/$name.png"

  /** The image path of the deck back.
    * @return
    *   the path
    */
  def deckBack: String = s"$baseResource/decks/Red_Deck.png"

  /** The image path of a blind.
    * @param name
    *   the blind image name
    * @return
    *   the path
    */
  def blind(name: String): String = s"$baseResource/blinds/$name.png"
