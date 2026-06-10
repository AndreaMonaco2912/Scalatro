package scalatro
package model.commons

enum HandType(val baseScore: HandScore.HandScore):
  case HighCard extends HandType(HandScore(5, 1))
  case Pair extends HandType(HandScore(10, 2))
  case TwoPair extends HandType(HandScore(20, 2))
  case ThreeOfAKind extends HandType(HandScore(30, 3))
  case Straight extends HandType(HandScore(30, 4))
  case Flush extends HandType(HandScore(35, 4))
  case FullHouse extends HandType(HandScore(40, 4))
  case FourOfAKind extends HandType(HandScore(60, 7))
  case StraightFlush extends HandType(HandScore(100, 8))
  case RoyalFlush extends HandType(HandScore(100, 8))
  case FiveOfAKind extends HandType(HandScore(120, 12))
  case FlushHouse extends HandType(HandScore(140, 14))
  case FlushFive extends HandType(HandScore(160, 16))

object HandType:
  def contains(cards: Seq[Card], handType: HandType): Boolean =
    val isFlush = cards.sizeIs == 5 && cards.map(_.suit).distinct.sizeIs == 1
    val ranks = cards.groupBy(_.rank)
    val containsAce = ranks.contains(Rank.Ace)
    // da sostiture .ordinal con un .value o qualcosa di simile
    val normalStraight =
      cards.sizeIs == 5 && cards.map(_.rank.ordinal).sorted.sliding(2).forall {
        case Seq(a, b) => b - a == 1
      }
    val lowStraight = cards.sizeIs == 5 && containsAce && (-1 +: cards
      .map(_.rank.ordinal)
      .sorted
      .dropRight(1)).sliding(2).forall { case Seq(a, b) => b - a == 1 }
    val isStraight = normalStraight || lowStraight
    val numRanks = ranks.values.map(_.size).toSeq.sorted.reverse

    handType match
      case FlushFive     => numRanks.contains(5) && isFlush
      case FlushHouse    => numRanks.take(2) == Seq(3, 2) && isFlush
      case FiveOfAKind   => numRanks.contains(5)
      case RoyalFlush    => isStraight && isFlush && containsAce
      case StraightFlush => isStraight && isFlush
      case FourOfAKind   => numRanks.contains(4)
      case FullHouse     => numRanks.take(2) == Seq(3, 2)
      case Flush         => isFlush
      case Straight      => isStraight
      case ThreeOfAKind  => numRanks.contains(3)
      case TwoPair       => numRanks.take(2) == Seq(2, 2)
      case Pair          => numRanks.contains(2)
      case HighCard      => true

  def detect(cards: Seq[Card]): HandType =
    HandType.values
      .findLast(handType => contains(cards, handType))
      .getOrElse(HighCard)
