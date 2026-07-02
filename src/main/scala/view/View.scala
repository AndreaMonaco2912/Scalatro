package scalatro
package view

import app.{Model, Msg, OpenPack}
import model.commons.{Deck, HandTypeLevels}
import model.round.Round
import view.fxController.{FxController, FxPackController, FxRoundEndController}

import cats.effect.IO

trait View:
  def render(model: Model): IO[Unit]

private enum Screen:
  case Gameplay, Won, Lost, ShopScreen, CardPack, PlanetPack, JokerPack, Deck,
    HandLevels

class FxView(screens: GameViews, dispatch: Msg => Unit) extends View:
  private var current: Option[Screen] = None
  private var gameplay: Option[FxController] = None

  def render(model: Model): IO[Unit] = model match
    case Model.RoundWon(round) =>
      enterRoundEnd(Screen.Won, screens.roundWon, round)
    case Model.RoundLost(round) =>
      enterRoundEnd(Screen.Lost, screens.roundLost, round)
    case Model.InShop(_, _) =>
      enter(Screen.ShopScreen, screens.shop)(_.onMessage(dispatch))

    case Model.OpeningPack(_, OpenPack.Cards(pack)) =>
      enterPack(Screen.CardPack, screens.cardPack, pack.items)
    case Model.OpeningPack(_, OpenPack.Planets(pack)) =>
      enterPack(Screen.PlanetPack, screens.planetPack, pack.items)
    case Model.OpeningPack(_, OpenPack.Jokers(pack)) =>
      enterPack(Screen.JokerPack, screens.jokerPack, pack.items)
    case Model.ShowDeck(deck, _)     => enterDeck(deck)
    case Model.ShowLevels(levels, _) => enterHandLevels(levels)
    case Model.Playing               => IO.unit

  def enterGameplay: IO[FxController] =
    screens.gameplay.flatMap: ctrl =>
      IO:
        gameplay = Some(ctrl)
        current = Some(Screen.Gameplay)
        ctrl

  private def enter[C](screen: Screen, load: IO[C])(wire: C => Unit): IO[Unit] =
    if current.contains(screen) then IO.unit
    else load.flatMap(ctrl => IO { wire(ctrl); current = Some(screen) })

  private def enterRoundEnd(
      screen: Screen,
      load: IO[FxRoundEndController],
      round: Round
  ): IO[Unit] =
    if current.contains(screen) then IO.unit
    else
      load.flatMap: ctrl =>
        IO:
          ctrl.onMessage(dispatch)
          ctrl.showStats(round)
          current = Some(screen)

  private def enterPack[A](
      screen: Screen,
      load: IO[FxPackController[A]],
      items: Seq[A]
  ): IO[Unit] =
    if current.contains(screen) then IO.unit
    else
      load.flatMap: ctrl =>
        IO:
          ctrl.onMessage(dispatch)
          ctrl.showItems(items)
          current = Some(screen)

  private def enterDeck(deck: Deck): IO[Unit] =
    screens.deck.flatMap: ctrl =>
      IO:
        ctrl.onMessage(dispatch)
        ctrl.showCards(deck.sort)
        current = Some(Screen.Deck)

  private def enterHandLevels(levels: HandTypeLevels): IO[Unit] =
    screens.handLevels.flatMap: ctrl =>
      IO:
        ctrl.onMessage(dispatch)
        ctrl.showLevels(levels)
        current = Some(Screen.HandLevels)
