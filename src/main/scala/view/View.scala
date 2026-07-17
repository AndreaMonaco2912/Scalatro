package scalatro
package view

import app.{Model, Msg, OpenPack}
import view.fx.{Dispatcher, FxRoundController}

import cats.effect.IO

trait View:
  def render(model: Model): IO[Unit]

private enum Screen:
  case Gameplay, Won, Lost, ShopScreen, CardPack, PlanetPack, JokerPack, Deck,
    HandLevels

class FxView(screens: ScreenRouter, dispatch: Msg => Unit) extends View:
  private var current: Option[Screen] = None
  private var gameplay: Option[FxRoundController] = None

  def render(model: Model): IO[Unit] = model match
    case Model.RoundWon(round) =>
      enter(Screen.Won, screens.roundWon)(_.showStats(round))
    case Model.RoundLost(round) =>
      enter(Screen.Lost, screens.roundLost)(_.showStats(round))
    case Model.InShop(_, _) =>
      enter(Screen.ShopScreen, screens.shop)(_ => ())
    case Model.OpeningPack(_, OpenPack.Cards(pack)) =>
      enter(Screen.CardPack, screens.cardPack)(_.showItems(pack.items))
    case Model.OpeningPack(_, OpenPack.Planets(pack)) =>
      enter(Screen.PlanetPack, screens.planetPack)(_.showItems(pack.items))
    case Model.OpeningPack(_, OpenPack.Jokers(pack)) =>
      enter(Screen.JokerPack, screens.jokerPack)(_.showItems(pack.items))
    case Model.ShowDeck(deck, _) =>
      enter(Screen.Deck, screens.deck)(_.showCards(deck.sort))
    case Model.ShowLevels(levels, _) =>
      enter(Screen.HandLevels, screens.handLevels)(_.showLevels(levels))
    case Model.Playing => IO.unit

  def enterGameplay: IO[FxRoundController] =
    screens.gameplay.flatMap: ctrl =>
      IO:
        gameplay = Some(ctrl)
        current = Some(Screen.Gameplay)
        ctrl

  private def enter[C <: Dispatcher](screen: Screen, load: IO[C])(
      show: C => Unit
  ): IO[Unit] =
    if current.contains(screen) then IO.unit
    else
      load.flatMap: ctrl =>
        IO:
          ctrl.onMessage(dispatch)
          show(ctrl)
          current = Some(screen)
