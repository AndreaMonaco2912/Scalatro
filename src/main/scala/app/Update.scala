package scalatro
package app

import model.game.GameState

object Update:

  val init: (Model, Cmd) =
    (Model.Playing, Cmd.Deal(GameState.initial))

  def update(model: Model, msg: Msg): (Model, Cmd) =
    msg match
      case effect: Msg.InternalEffect => fromEffect(effect)
      case action                     => fromAction(model, action)

  private def fromEffect(effect: Msg.InternalEffect): (Model, Cmd) =
    effect match
      case Msg.InternalEffect.RoundWon(gs) =>
        (Model.RoundWon(gs), Cmd.NoOp)
      case Msg.InternalEffect.RoundLost(blind, score) =>
        (Model.RoundLost(blind, score), Cmd.NoOp)
      case Msg.InternalEffect.ShopReady(gs, shop) =>
        (Model.InShop(gs, shop), Cmd.NoOp)

  private def fromAction(model: Model, msg: Msg): (Model, Cmd) =
    (model, msg) match
      case (Model.RoundWon(gs), Msg.RoundEndAction.NextRound) =>
        (model, Cmd.BuildShop(gs))
      case (Model.RoundLost(_, _), Msg.RoundEndAction.Restart) =>
        (model, Cmd.Deal(GameState.initial))
      case (m: Model.InShop, action: Msg.ShopAction) =>
        inShop(m, action)
      case (Model.OpeningPack(gs, _), selection: Msg.PackSelection) =>
        (model, Cmd.Deal(applySelection(gs, selection).advanceBlind))

      case _ => (model, Cmd.NoOp)

  private def applySelection(
      gs: GameState,
      selection: Msg.PackSelection
  ): GameState =
    selection match
      case Msg.PackSelection.SelectCard(c)   => gs.addCard(c)
      case Msg.PackSelection.SelectPlanet(p) => gs.usePlanet(p)
      case Msg.PackSelection.SelectJoker(j)  => gs.addJoker(j)
      case Msg.PackSelection.SkipPack        => gs

  private def inShop(
      model: Model.InShop,
      action: Msg.ShopAction
  ): (Model, Cmd) =
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
        (model, Cmd.Deal(gs.advanceBlind))
