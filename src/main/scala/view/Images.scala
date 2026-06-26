package scalatro
package view

import model.commons.{Card, Joker, Rank}

import javafx.scene.image.Image

object Images:
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
    new Image(
      getClass.getResourceAsStream(
        s"/scalatro/cards/${rankString}_of_${card.suit}.png"
      )
    )

  def joker(joker: Joker): Image =
    new Image(
      getClass.getResourceAsStream(
        s"/scalatro/jokers/${joker.name.replace(" ", "_")}.png"
      )
    )
