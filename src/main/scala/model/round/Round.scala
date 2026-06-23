package scalatro
package model.round

import model.commons.Score.Score
import model.commons.{Card, CardOrderer, Deck}
import model.game.Blind

type Hand = Seq[Card]

enum RoundAction:
  case PlayCards(cards: Seq[Card])
  case DiscardCards(cards: Seq[Card])
  case OrderHand(orderer: CardOrderer)

enum RoundResult:
  case Victory, Defeat

trait Round:
  def score: Score
  def hand: Hand
  def deck: Deck
  def remainingPlays: Int
  def remainingDiscards: Int
  def blind: Blind
  def modify(
      score: Score = score,
      hand: Hand = hand,
      deck: Deck = deck,
      remainingPlays: Int = remainingPlays,
      remainingDiscards: Int = remainingDiscards
  ): Round
  def isFinished: Boolean

object Round:
  def apply(score: Score, hand: Hand, deck: Deck, blind: Blind): Round =
    RoundImpl(score, hand, deck, blind.handNum, blind.discardNum, blind)

  private case class RoundImpl(
      score: Score,
      hand: Hand,
      deck: Deck,
      remainingPlays: Int,
      remainingDiscards: Int,
      blind: Blind
  ) extends Round:
    override def modify(
        score: Score,
        hand: Hand,
        deck: Deck,
        remainingPlays: Int,
        remainingDiscards: Int
    ): Round =
      RoundImpl(score, hand, deck, remainingPlays, remainingDiscards, blind)

    override def isFinished: Boolean = blind.isBeaten(score)
