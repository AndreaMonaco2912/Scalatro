package scalatro
package model.commons

import model.round.Hand

trait Joker:

  def independent(handScore: HandScore): HandScore = handScore

  def onCardScored(handScore: HandScore, card: Card): HandScore = handScore

  def onHandPlayed(handScore: HandScore, hand: Hand): HandScore = handScore

  def afterRound(handScore: HandScore): HandScore = handScore

  def onCardHeld(handScore: HandScore, card: Card): HandScore = handScore
