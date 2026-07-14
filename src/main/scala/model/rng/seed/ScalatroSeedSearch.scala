package scalatro
package model.rng.seed

import model.commons.*
import model.rng.ScalatroRng
import model.rng.Types.Seed
import model.rng.seed.SimulationActions.satisfiesConstraints

import scala.util.Random

/** Runs game simulations to check if all constraints are satisfied */
object ScalatroSeedSearch:
  /** Entry point */
  def main(args: Array[String]): Unit =
    parseArgs(args) match
      case Left(message) =>
        System.err.println(message)
        System.err.println(
          """Usage:
            |  java -jar scalatro.jar -ss [-c <constraint>] [-c <constraint>]...
            |
            |Examples:
            |  -c "initial-hand-cards:1:Ace-Hearts,Ace-Diamonds"
            |  -c "initial-hand-type:1:FullHouse"
            |  -c "card-pack-contains:1:King-Spades"
            |  -c "joker-pack-contains:1:CleverJoker"
            |  -c "planet-pack-contains:1:Earth"
            |""".stripMargin
        )
        sys.exit(1)

      case Right(constraints) =>
        val seed = findSeed(constraints)
        println(s"Found seed: $seed")

  /** Generates an infinite stream of seeds until a seed is found that satisfies
    * all constraints
    * @param constraints
    *   the constraints to satisfy
    * @param maxAttempts
    *   the maximum number of attempts until the stream is interrupted
    * @return
    *   the found seed
    */
  private[seed] def findSeed(
      constraints: Seq[SeedConstraint],
      maxAttempts: Int = Int.MaxValue
  ): Seed =
    LazyList
      .continually(Random.nextLong())
      .map(Seed(_))
      .take(maxAttempts)
      .find(seed => satisfiesConstraints(constraints)(using ScalatroRng(seed)))
      .getOrElse(Seed(0L))

  /** Parse command line arguments into a sequence of [[SeedConstraint]]
    * @param args
    *   the command line arguments
    * @return
    *   an either representing the parsed constraints or a failure message
    */
  private[seed] def parseArgs(
      args: Array[String]
  ): Either[String, Seq[SeedConstraint]] =
    args.toList match
      case Nil => Right(Seq.empty)

      case "-c" :: constraint :: tail =>
        for
          parsedConstraint <- parseConstraint(constraint)
          parsedTail <- parseArgs(tail.toArray)
        yield parsedConstraint +: parsedTail

      case "-c" :: Nil =>
        Left("Missing constraint after -c")

      case unknown :: _ =>
        Left(s"Unknown argument: $unknown")

  /** Parse a string into a [[SeedConstraint]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed constraint or a failure message
    */
  private[seed] def parseConstraint(
      text: String
  ): Either[String, SeedConstraint] =
    text.split(":", 3).toList match
      case "initial-hand-cards" :: round :: cards :: Nil =>
        for
          parsedRound <- parseRound(round)
          parsedCards <- parseCards(cards)
        yield InitialHandWithCards(parsedCards, parsedRound)

      case "initial-hand-type" :: round :: handType :: Nil =>
        for
          parsedRound <- parseRound(round)
          parsedHandType <- parseHandType(handType)
        yield InitialHandWithHandType(parsedHandType, parsedRound)

      case "card-pack-contains" :: round :: card :: Nil =>
        for
          parsedRound <- parseRound(round)
          parsedCard <- parseCard(card)
        yield CardPackContains(parsedCard, parsedRound)

      case "joker-pack-contains" :: round :: joker :: Nil =>
        for
          parsedRound <- parseRound(round)
          parsedJoker <- parseJoker(joker)
        yield JokerPackContains(parsedJoker, parsedRound)

      case "planet-pack-contains" :: round :: planet :: Nil =>
        for
          parsedRound <- parseRound(round)
          parsedPlanet <- parsePlanet(planet)
        yield PlanetPackContains(parsedPlanet, parsedRound)

      case _ =>
        Left(s"Invalid seed constraint: $text")

  /** Parse a string into a round number
    * @param text
    *   the string to parse
    * @return
    *   an either representing the round number or a failure message
    */
  private[seed] def parseRound(text: String): Either[String, Int] =
    text.toIntOption match
      case Some(round) if round >= 1 => Right(round)
      case Some(round)               =>
        Left(s"Invalid round: $round. Round must be at least 1")
      case None => Left(s"Invalid round: $text")

  /** Parse a string into a sequence of [[Card]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed sequence of cards or a failure message
    */
  private[seed] def parseCards(text: String): Either[String, Seq[Card]] =
    text
      .split(",")
      .toSeq
      .map(parseCard)
      .foldLeft[Either[String, Seq[Card]]](Right(Seq.empty)) {
        case (Right(cards), Right(card)) => Right(cards :+ card)
        case (Left(message), _)          => Left(message)
        case (_, Left(message))          => Left(message)
      }

  /** Parse a string into a [[Card]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed card or a failure message
    */
  private[seed] def parseCard(text: String): Either[String, Card] =
    text.split("-", 2).toList match
      case rank :: suit :: Nil =>
        for
          parsedRank <- parseRank(rank)
          parsedSuit <- parseSuit(suit)
        yield Card(parsedRank, parsedSuit)

      case _ =>
        Left(s"Invalid card: $text. Expected format: Rank-Suit")

  /** Parse a string into a [[Rank]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed rank or a failure message
    */
  private[seed] def parseRank(text: String): Either[String, Rank] =
    Rank.values.find(_.productPrefix == text) match
      case Some(rank) => Right(rank)
      case None       => Left(s"Invalid rank: $text")

  /** Parse a string into a [[Suit]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed suit or a failure message
    */
  private[seed] def parseSuit(text: String): Either[String, Suit] =
    Suit.values.find(_.productPrefix == text) match
      case Some(suit) => Right(suit)
      case None       => Left(s"Invalid suit: $text")

  /** Parse a string into a [[HandType]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed hand type or a failure message
    */
  private[seed] def parseHandType(text: String): Either[String, HandType] =
    HandType.values.find(_.productPrefix == text) match
      case Some(handType) => Right(handType)
      case None           => Left(s"Invalid hand type: $text")

  /** Parse a string into a [[Joker]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed joker or a failure message
    */
  private[seed] def parseJoker(text: String): Either[String, Joker] =
    JokerType.values.find(_.productPrefix == text) match
      case Some(joker) => Right(joker)
      case None        => Left(s"Invalid joker: $text")

  /** Parse a string into a [[Planet]]
    * @param text
    *   the string to parse
    * @return
    *   an either representing the parsed planet or a failure message
    */
  private[seed] def parsePlanet(text: String): Either[String, Planet] =
    Planet.values.find(_.productPrefix == text) match
      case Some(planet) => Right(planet)
      case None         => Left(s"Invalid planet: $text")
