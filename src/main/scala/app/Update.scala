package scalatro
package app

import model.game.GameState
import model.commons.{Deck, HandTypeLevels, Modification, OnBuyEffect}
import model.rng.ScalatroRng

/** The update function of the MVU loop.
  *
  * Maps the current [[Model]] and an incoming [[Msg]] to the next [[Model]] and
  * [[Cmd]].
  */

object Update:

  /** The initial model and command of the application. */
  val init: (Model, Cmd) =
    (Model.Playing, Cmd.Deal(GameState.initial))

  /** Computes the next model and the command to perform.
    *
    * @param model
    *   the current model
    * @param msg
    *   the incoming message
    * @return
    *   the next model and the command
    */
  def update(model: Model, msg: Msg)(using ScalatroRng): (Model, Cmd) =
    msg match
      case effect: Msg.InternalEffect => fromEffect(effect)
      case action                     => fromAction(model, action)

  private def fromEffect(effect: Msg.InternalEffect): (Model, Cmd) =
    effect match
      case Msg.InternalEffect.RoundWon(round) =>
        (Model.RoundWon(round), Cmd.NoOp)
      case Msg.InternalEffect.RoundLost(round) =>
        (Model.RoundLost(round), Cmd.NoOp)
      case Msg.InternalEffect.ShopReady(gs, shop) =>
        (Model.InShop(gs, shop), Cmd.NoOp)

  private def fromAction(model: Model, msg: Msg)(using
      ScalatroRng
  ): (Model, Cmd) =
    (model, msg) match
      case (Model.RoundWon(round), Msg.RoundEndAction.NextRound) =>
        (model, Cmd.BuildShop(round.gameState))
      case (Model.RoundLost(_), Msg.RoundEndAction.Restart) =>
        (model, Cmd.Deal(GameState.initial))
      case (m: Model.InShop, action: Msg.ShopAction) =>
        inShop(m, action)
      case (Model.OpeningPack(gs, _), selection: Msg.PackSelection) =>
        (Model.Playing, Cmd.Deal(applySelection(gs, selection).advanceBlind))
      case (Model.ShowDeck(_, prev), Msg.ManagementAction.CloseDeck) =>
        (prev, Cmd.NoOp)
      case (Model.ShowLevels(_, prev), Msg.ManagementAction.CloseLevels) =>
        (prev, Cmd.NoOp)
      case (m, Msg.ManagementAction.ShowDeck) =>
        deckOf(m).fold((m, Cmd.NoOp))(d => (Model.ShowDeck(d, m), Cmd.NoOp))
      case (m, Msg.ManagementAction.ShowLevels) =>
        levelsOf(m).fold((m, Cmd.NoOp))(l => (Model.ShowLevels(l, m), Cmd.NoOp))

      case _ => (model, Cmd.NoOp)

  private def applySelection(
      gs: GameState,
      selection: Msg.PackSelection
  ): GameState =
    selection match
      case Msg.PackSelection.SelectCard(c)   => gs.addCard(c)
      case Msg.PackSelection.SelectPlanet(p) => gs.usePlanet(p)
      case Msg.PackSelection.SelectJoker(j)  =>
        val newGs: GameState = j match
          case j: OnBuyEffect => j.onBuy(gs).applyAll(gs)
          case _              => gs
        newGs.addJoker(j)
      case Msg.PackSelection.SkipPack => gs

  private def inShop(
      model: Model.InShop,
      action: Msg.ShopAction
  )(using ScalatroRng): (Model, Cmd) =
    val gs = model.gameState
    action match
      case Msg.ShopAction.OpenCardPack =>
        (Model.OpeningPack(gs, OpenPack.Cards(model.shop.cardPack)), Cmd.NoOp)
      case Msg.ShopAction.OpenPlanetPack =>
        (
          Model.OpeningPack(gs, OpenPack.Planets(model.shop.planetPack)),
          Cmd.NoOp
        )
      case Msg.ShopAction.OpenJokerPack =>
        (Model.OpeningPack(gs, OpenPack.Jokers(model.shop.jokerPack)), Cmd.NoOp)
      case Msg.ShopAction.SkipShop =>
        (Model.Playing, Cmd.Deal(gs.advanceBlind))

  private def deckOf(model: Model): Option[Deck] = model match
    case Model.RoundWon(round)    => Some(round.gameState.deck)
    case Model.RoundLost(round)   => Some(round.gameState.deck)
    case Model.InShop(gs, _)      => Some(gs.deck)
    case Model.OpeningPack(gs, _) => Some(gs.deck)
    case _                        => None

  private def levelsOf(model: Model): Option[HandTypeLevels] = model match
    case Model.RoundWon(round)    => Some(round.gameState.levels)
    case Model.RoundLost(round)   => Some(round.gameState.levels)
    case Model.InShop(gs, _)      => Some(gs.levels)
    case Model.OpeningPack(gs, _) => Some(gs.levels)
    case _                        => None
