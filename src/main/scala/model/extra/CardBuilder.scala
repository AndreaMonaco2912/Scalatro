package scalatro
package model.extra

import model.commons.{Card, Rank, Suit}

/** A DSL for building [[Card]] instances. * Example usage:
  * {{{
  * val fiveOfHearts = 5 of H
  * val aceOfSpades = A of S
  * }}}
  */
object CardBuilder:

  /** Shorthand for [[Suit.Spades]]. */
  val S: Suit = Suit.Spades

  /** Shorthand for [[Suit.Hearts]]. */
  val H: Suit = Suit.Hearts

  /** Shorthand for [[Suit.Clubs]]. */
  val C: Suit = Suit.Clubs

  /** Shorthand for [[Suit.Diamonds]]. */
  val D: Suit = Suit.Diamonds

  extension (value: Int)
    /** Creates a numeric card (2 through 10) of the specified suit. * @param
      * suit the suit of the card
      * @return
      *   a [[Card]] with the corresponding rank and suit
      * @throws IllegalArgumentException
      *   if the integer is not between 2 and 10
      */
    infix def of(suit: Suit): Card = value match
      case 2  => Card(Rank.Two, suit)
      case 3  => Card(Rank.Three, suit)
      case 4  => Card(Rank.Four, suit)
      case 5  => Card(Rank.Five, suit)
      case 6  => Card(Rank.Six, suit)
      case 7  => Card(Rank.Seven, suit)
      case 8  => Card(Rank.Eight, suit)
      case 9  => Card(Rank.Nine, suit)
      case 10 => Card(Rank.Ten, suit)
      case _  =>
        throw new IllegalArgumentException(
          s"Invalid numeric rank: $value (must be 2-10)"
        )

  /** Builder for a Jack. */
  object J:
    /** @param suit
      *   the suit of the card
      */
    infix def of(suit: Suit): Card = Card(Rank.Jack, suit)

  /** Builder for a Queen. */
  object Q:
    /** @param suit
      *   the suit of the card
      */
    infix def of(suit: Suit): Card = Card(Rank.Queen, suit)

  /** Builder for a King. */
  object K:
    /** @param suit
      *   the suit of the card
      */
    infix def of(suit: Suit): Card = Card(Rank.King, suit)

  /** Builder for an Ace. */
  object A:
    /** @param suit
      *   the suit of the card
      */
    infix def of(suit: Suit): Card = Card(Rank.Ace, suit)
