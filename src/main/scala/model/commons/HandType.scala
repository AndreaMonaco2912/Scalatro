package scalatro
package model.commons

enum HandType(val baseScore: HandScore, val name: String):
  case HighCard extends HandType(HandScore(Chips(5), Mult(1)), "High Card")
  case Pair extends HandType(HandScore(Chips(10), Mult(2)), "Pair")
  case TwoPair extends HandType(HandScore(Chips(20), Mult(2)), "Two Pair")
  case ThreeOfAKind
      extends HandType(HandScore(Chips(30), Mult(3)), "Three of a Kind")
  case Straight extends HandType(HandScore(Chips(30), Mult(4)), "Straight")
  case Flush extends HandType(HandScore(Chips(35), Mult(4)), "Flush")
  case FullHouse extends HandType(HandScore(Chips(40), Mult(4)), "Full House")
  case FourOfAKind
      extends HandType(HandScore(Chips(60), Mult(7)), "Four of a Kind")
  case StraightFlush
      extends HandType(HandScore(Chips(100), Mult(8)), "Straight Flush")
  case FiveOfAKind
      extends HandType(HandScore(Chips(120), Mult(12)), "Five of a Kind")
  case FlushHouse
      extends HandType(HandScore(Chips(140), Mult(14)), "Flush House")
  case FlushFive extends HandType(HandScore(Chips(160), Mult(16)), "Flush Five")

  override def toString: String = name

object HandType:
  def contains(cards: Seq[Card], handType: HandType): Boolean =
    val isFlush = cards.sizeIs == 5 && cards.map(_.suit).distinct.sizeIs == 1
    val ranks = cards.groupBy(_.rank)
    val containsAce = ranks.contains(Rank.Ace)
    val normalStraight =
      cards.sizeIs == 5 && cards.map(_.rank.value).sorted.sliding(2).forall {
        case Seq(a, b) => b - a == 1
      }
    val aceLowStraight = cards.sizeIs == 5 && containsAce && (1 +: cards
      .map(_.rank.value)
      .sorted
      .dropRight(1)).sliding(2).forall { case Seq(a, b) => b - a == 1 }
    val isStraight = normalStraight || aceLowStraight
    val numRanks = ranks.values.map(_.size).toSeq.sorted.reverse
    val isFullHouse = numRanks.take(2) == Seq(3, 2)

    handType match
      case FlushFive     => numRanks.contains(5) && isFlush
      case FlushHouse    => isFullHouse && isFlush
      case FiveOfAKind   => numRanks.contains(5)
      case StraightFlush => isStraight && isFlush
      case FourOfAKind   => numRanks.exists(_ >= 4)
      case FullHouse     => isFullHouse
      case Flush         => isFlush
      case Straight      => isStraight
      case ThreeOfAKind  => numRanks.exists(_ >= 3)
      case TwoPair       => isFullHouse || numRanks.take(2) == Seq(2, 2)
      case Pair          => numRanks.exists(_ >= 2)
      case HighCard      => true

  def detect(cards: Seq[Card]): HandType =
    HandType.values
      .findLast(handType => contains(cards, handType))
      .getOrElse(HighCard)

  def getScoringCards(cards: Seq[Card]): Seq[Card] =
    val handType: HandType = detect(cards)
    val ranks = cards.groupBy(_.rank)

    handType match
      case FlushFive | FlushHouse | FiveOfAKind | FullHouse | StraightFlush |
          Straight | Flush =>
        cards
      case FourOfAKind =>
        ranks.values
          .find(_.sizeIs == 4)
          .map(matches => cards.filter(matches.contains))
          .getOrElse(Seq.empty)
      case ThreeOfAKind =>
        ranks.values
          .find(_.sizeIs == 3)
          .map(matches => cards.filter(matches.contains))
          .getOrElse(Seq.empty)
      case TwoPair =>
        cards.filter(ranks.values.filter(_.sizeIs == 2).flatten.toSet.contains)
      case Pair =>
        ranks.values
          .find(_.sizeIs == 2)
          .map(matches => cards.filter(matches.contains))
          .getOrElse(Seq.empty)
      case HighCard =>
        cards.maxByOption(_.rank.ordinal) match
          case None    => Seq.empty
          case Some(c) => Seq(c)
