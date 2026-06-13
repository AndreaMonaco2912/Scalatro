package scalatro
package model.round

import model.commons.Score.Score
import model.commons.{Card, Deck}
import model.game.Blind

type Hand = Seq[Card]

enum RoundAction:
  case PlayCards(cards: Seq[Card])
  case DiscardCards(cards: Seq[Card])

enum RoundResult:
  case Victory, Defeat

trait Round:
  def score: Score
  def hand: Hand
  def deck: Deck
  def blind: Blind
  def modify(
      score: Score = score,
      hand: Hand = hand,
      deck: Deck = deck
  ): Round
  def isFinished: Boolean

object Round:
  def apply(score: Score, hand: Hand, deck: Deck, blind: Blind): Round =
    RoundImpl(score, hand, deck, blind)

  private case class RoundImpl(
      score: Score,
      hand: Hand,
      deck: Deck,
      blind: Blind
  ) extends Round:
    override def modify(score: Score, hand: Hand, deck: Deck): Round =
      RoundImpl(score, hand, deck, blind)
      
    override def isFinished: Boolean = blind.isBeaten(score)
