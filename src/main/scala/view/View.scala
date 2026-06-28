package scalatro
package view

import app.{Model, Msg, OpenPack}
import view.fxController.{FxController, FxPackController}

import cats.effect.IO
import cats.effect.std.Queue
import model.round.Round

trait View:
  def render(model: Model): IO[Unit]

private enum Screen:
  case Gameplay, Won, Lost, ShopScreen, CardPack, PlanetPack, JokerPack

class FxView(screens: GameViews, dispatch: Msg => Unit) extends View:
  private var current: Option[Screen] = None
  private var gameplay: Option[FxController] = None

  def render(model: Model): IO[Unit] = model match
    case Model.RoundWon(_) =>
      enter(Screen.Won, screens.roundWon)(_.onMessage(dispatch))
    case Model.RoundLost(_, _) =>
      enter(Screen.Lost, screens.roundLost)(_.onMessage(dispatch))
    case Model.InShop(_, _) =>
      enter(Screen.ShopScreen, screens.shop)(_.onMessage(dispatch))

    case Model.OpeningPack(_, OpenPack.Cards(pack)) =>
      enterPack(Screen.CardPack, screens.cardPack, pack.items)
    case Model.OpeningPack(_, OpenPack.Planets(pack)) =>
      enterPack(Screen.PlanetPack, screens.planetPack, pack.items)
    case Model.OpeningPack(_, OpenPack.Jokers(pack)) =>
      enterPack(Screen.JokerPack, screens.jokerPack, pack.items)
    case Model.Playing => IO.unit

  def enterGameplay: IO[FxController] =
    screens.gameplay.flatMap: ctrl =>
      IO:
        gameplay = Some(ctrl)
        current = Some(Screen.Gameplay)
        ctrl

  private def enter[C](screen: Screen, load: IO[C])(wire: C => Unit): IO[Unit] =
    if current.contains(screen) then IO.unit
    else load.flatMap(ctrl => IO { wire(ctrl); current = Some(screen) })

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
