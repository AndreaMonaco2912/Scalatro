package scalatro
package model.commons

import model.game.GameState

type Level = Int

/** The level of a poker hand */
object Level:
  def apply(l: Int): Level =
    require(l >= 1, "Level must be at least 1")
    l

  /** The initial level for every poker hand */
  def initial: Level = 1

/** The association between hand types and their level */
type HandTypeLevels = Map[HandType, Level]

extension (htl: HandTypeLevels)

  /** Get the level of a hand type
    *
    * @param handType
    *   the hand type
    * @return
    *   the level
    */
  def getLevel(handType: HandType): Level =
    htl.getOrElse(handType, Level.initial)

object HandTypeLevels:
  /** @return
    *   the initial level of the hand types
    */
  def initial: HandTypeLevels = HandType.values.map(ht => ht -> 1).toMap

/** A planet card which can be used to increase the base score of a poker hand
  * by a certain amount
  * @param handType
  *   the hand type associated with the planet card
  * @param increase
  *   the hand score which gets added to the base score of the hand for each
  *   level
  */
enum Planet(val handType: HandType, val increase: HandScore):
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

  private val increaseByHandType: Map[HandType, HandScore] =
    Planet.values.map(p => p.handType -> p.increase).toMap

  /** Get the increase score given by the planet card associated with the given
    * hand type
    * @param handType
    *   the hand type
    * @return
    */
  def getIncrease(handType: HandType): HandScore =
    increaseByHandType(handType)

  extension (planet: Planet)
    /** Use the planet card, which increases the associated hand type by 1 level
      * @param levels
      *   the hand type levels
      * @return
      *   the new hand type levels
      */
    def use(levels: HandTypeLevels): HandTypeLevels =
      val currentLevel: Level = levels.getOrElse(planet.handType, Level.initial)
      levels.updated(planet.handType, currentLevel + 1)

    def use(levels: HandTypeLevels, times: Int): HandTypeLevels =
      (1 to times).foldLeft(levels)((acc, _) => planet.use(acc))
