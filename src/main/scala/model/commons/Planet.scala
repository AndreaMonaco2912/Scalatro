package scalatro
package model.commons

import model.rng.Weighable

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

  /** Update the level of an hand type
    * @param handType
    *   the hand type
    * @param newLevel
    *   the new level
    * @return
    *   the updated hand levels
    */
  def update(handType: HandType, newLevel: Level): HandTypeLevels =
    htl.updated(handType, newLevel)

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
enum Planet(val handType: HandType, val increase: HandScore, val name: String)
    extends Weighable:
  case Pluto
      extends Planet(HandType.HighCard, HandScore(Chips(10), Mult(1)), "Pluto")
  case Mercury
      extends Planet(HandType.Pair, HandScore(Chips(15), Mult(1)), "Mercury")
  case Uranus
      extends Planet(HandType.TwoPair, HandScore(Chips(20), Mult(1)), "Uranus")
  case Venus
      extends Planet(
        HandType.ThreeOfAKind,
        HandScore(Chips(20), Mult(2)),
        "Venus"
      )
  case Saturn
      extends Planet(HandType.Straight, HandScore(Chips(30), Mult(3)), "Saturn")
  case Jupiter
      extends Planet(HandType.Flush, HandScore(Chips(15), Mult(2)), "Jupiter")
  case Earth
      extends Planet(HandType.FullHouse, HandScore(Chips(25), Mult(2)), "Earth")
  case Mars
      extends Planet(
        HandType.FourOfAKind,
        HandScore(Chips(30), Mult(3)),
        "Mars"
      )
  case Neptune
      extends Planet(
        HandType.StraightFlush,
        HandScore(Chips(40), Mult(4)),
        "Neptune"
      )
  case PlanetX
      extends Planet(
        HandType.FiveOfAKind,
        HandScore(Chips(35), Mult(3)),
        "Planet X"
      )
  case Ceres
      extends Planet(
        HandType.FlushHouse,
        HandScore(Chips(40), Mult(4)),
        "Ceres"
      )
  case Eris
      extends Planet(HandType.FlushFive, HandScore(Chips(50), Mult(3)), "Eris")

  override def toString: String = name

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
      levels.update(planet.handType, levels.getLevel(planet.handType) + 1)
