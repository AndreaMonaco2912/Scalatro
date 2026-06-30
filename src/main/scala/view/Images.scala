package scalatro
package view

import model.commons.{Card, Joker, Planet, Rank}

import javafx.scene.image.Image

object Images:

  private def load(path: String): Image =
    new Image(getClass.getResourceAsStream(path))

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

  def joker(joker: Joker): Image =
    load(Resources.joker(joker.name.replace(" ", "_")))

  def planet(planet: Planet): Image =
    load(Resources.planet(planet.name.replace(" ", "_")))

  def pack(category: String, size: String, version: Int): Image =
    load(Resources.pack(s"${category}_${size}_$version"))

  def deckBack: Image = load(Resources.deckBack)
