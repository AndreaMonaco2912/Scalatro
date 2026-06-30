package scalatro
package view

import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox

object DeckNode:
  def apply(onClick: () => Unit): VBox =
    val image = ImageView(Images.deckBack)
    image.setFitWidth(85)
    image.setFitHeight(125)
    image.setPreserveRatio(true)
    val box = VBox(6, image)
    box.setAlignment(Pos.CENTER)
    box.getStyleClass.add("deck-trigger")
    box.setOnMouseClicked(_ => onClick())
    box