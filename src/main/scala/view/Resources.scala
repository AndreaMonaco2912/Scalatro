package scalatro
package view

object Resources:
  private val baseResource = "/scalatro/view"
  private val baseView = baseResource + "/fx"

  object Fxml:
    val gameplay: String = s"$baseView/scene.fxml"
    val roundWon: String = s"$baseView/roundWon.fxml"
    val roundLost: String = s"$baseView/roundLost.fxml"
    val shop: String = s"$baseView/shop.fxml"
    val cardPack: String = s"$baseView/cardPack.fxml"
    val planetPack: String = s"$baseView/planetPack.fxml"
    val jokerPack: String = s"$baseView/jokerPack.fxml"
    val deck: String = s"$baseView/deck.fxml"
    val handLevels: String = s"$baseView/handLevels.fxml"

  val stylesheet: String = s"$baseView/styles.css"

  def card(name: String): String = s"$baseResource/cards/$name.png"
  def joker(name: String): String = s"$baseResource/jokers/$name.png"
  def planet(name: String): String = s"$baseResource/planets/$name.png"
  def pack(name: String): String = s"$baseResource/packs/$name.png"
  def deckBack: String = s"$baseResource/decks/Red_Deck.png"
  def blind(name: String): String = s"$baseResource/blinds/$name.png"
