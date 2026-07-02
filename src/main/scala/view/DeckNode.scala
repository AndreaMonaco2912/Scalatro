package scalatro
package view

import javafx.geometry.Pos
import javafx.scene.layout.VBox

object DeckNode:
  def apply(onClick: () => Unit): VBox =
    val image = ImageViews(Images.deckBack, 85, 125)
    val box = VBox(6, image)
    box.setAlignment(Pos.CENTER)
    box.getStyleClass.add("deck-trigger")
    box.setOnMouseClicked(_ => onClick())
    box
