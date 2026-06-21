package scalatro
package model.commons

import model.commons.HandType.HighCard

enum Planet(val handType: HandType, val increase: HandScore.HandScore):
  case Pluto extends Planet(HandType.HighCard, HandScore(10, 1))
  case Mercury extends Planet(HandType.Pair, HandScore(15, 1))
  case Uranus extends Planet(HandType.TwoPair, HandScore(20, 1))
  case Venus extends Planet(HandType.ThreeOfAKind, HandScore(20, 2))
  case Saturn extends Planet(HandType.Straight, HandScore(30, 3))
  case Jupiter extends Planet(HandType.Flush, HandScore(15, 2))
  case Earth extends Planet(HandType.FullHouse, HandScore(25, 2))
  case Mars extends Planet(HandType.FourOfAKind, HandScore(30, 3))
  case Neptune extends Planet(HandType.StraightFlush, HandScore(40, 4))
  case PlanetX extends Planet(HandType.FiveOfAKind, HandScore(35, 3))
  case Ceres extends Planet(HandType.FlushHouse, HandScore(40, 4))
  case Eris extends Planet(HandType.FlushFive, HandScore(50, 3))

object Planet:
  private val handTypeToIncrease: Map[HandType, HandScore.HandScore] =
    Planet.values.map(p => p.handType -> p.increase).toMap

  def getIncrease(handType : HandType) : HandScore.HandScore =
    handTypeToIncrease(handType)