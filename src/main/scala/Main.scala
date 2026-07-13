package scalatro

import model.rng.seed.*

object Main:
  def main(args: Array[String]): Unit =
    args.toList match
      case ("-ss" | "--seed-search") :: tail =>
        ScalatroSeedSearch.main(tail.toArray)

      case Nil =>
        ScalatroApp.main(Array.empty)

      case ("-s" | "--seed") :: _ =>
        ScalatroApp.main(args)

      case unknown :: _ =>
        System.err.println(s"Unknown argument: $unknown")
        System.err.println(
          "Usage: java -jar scalatro.jar [-s|--seed <seed>] [-ss|--seed-search]"
        )
        sys.exit(1)
