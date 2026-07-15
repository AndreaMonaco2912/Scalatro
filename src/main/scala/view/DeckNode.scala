package scalatro
package view

import javafx.geometry.Pos
import javafx.scene.layout.VBox

/** A factory for the clickable deck node. */
object DeckNode:
  /** Creates the deck node.
    *
    * @param onClick
    *   the action invoked when the node is clicked
    * @return
    *   the node
    */
  def apply(onClick: () => Unit): VBox =
    val image = ImageViews(Images.deckBack, 85, 125)
    val box = VBox(6, image)
    box.setAlignment(Pos.CENTER)
    box.getStyleClass.add("deck-trigger")
    box.setOnMouseClicked(_ => onClick())
    box
