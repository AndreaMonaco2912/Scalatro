package scalatro
package view

object Resources:
  private val base = "/scalatro"

  object Fxml:
    val gameplay: String = s"$base/scene.fxml"
    val roundWon: String = s"$base/roundWon.fxml"
    val roundLost: String = s"$base/roundLost.fxml"
    val shop: String = s"$base/shop.fxml"
    val cardPack: String = s"$base/cardPack.fxml"
    val planetPack: String = s"$base/planetPack.fxml"
    val jokerPack: String = s"$base/jokerPack.fxml"
    val deck: String = s"$base/deck.fxml"
    val handLevels: String = s"$base/handLevels.fxml"

  val stylesheet: String = s"$base/styles.css"

  def card(name: String): String = s"$base/cards/$name.png"
  def joker(name: String): String = s"$base/jokers/$name.png"
  def planet(name: String): String = s"$base/planets/$name.png"
  def pack(name: String): String = s"$base/packs/$name.png"
  def deckBack: String = s"$base/decks/Red_Deck.png"
