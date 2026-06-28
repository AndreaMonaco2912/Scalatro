package scalatro
package app

import model.game.GameState

import scalatro.model.round.Round

/** The single, pure state transition function. */
object Update:

  val init: (Model, Cmd) =
    (Model.Playing, Cmd.Deal(GameState.initial))

  def update(model: Model, msg: Msg): (Model, Cmd) =
    (model, msg) match

      case (Model.RoundWon(gs), Msg.RoundEndAction.NextRound) =>
        (model, Cmd.BuildShop(gs))
      case (Model.RoundLost(_, _), Msg.RoundEndAction.Restart) =>
        (model, Cmd.Deal(GameState.initial))

      case (Model.InShop(gs, shop), Msg.ShopAction.OpenCardPack) =>
        (Model.OpeningPack(gs, OpenPack.Cards(shop.cardPack)), Cmd.NoOp)
      case (Model.InShop(gs, shop), Msg.ShopAction.OpenPlanetPack) =>
        (Model.OpeningPack(gs, OpenPack.Planets(shop.planetPack)), Cmd.NoOp)
      case (Model.InShop(gs, shop), Msg.ShopAction.OpenJokerPack) =>
        (Model.OpeningPack(gs, OpenPack.Jokers(shop.jokerPack)), Cmd.NoOp)
      case (Model.InShop(gs, _), Msg.ShopAction.SkipShop) =>
        (model, Cmd.Deal(gs.advanceBlind))

      case (Model.OpeningPack(gs, _), Msg.PackSelection.SelectCard(c)) =>
        (model, Cmd.Deal(gs.addCard(c).advanceBlind))
      case (Model.OpeningPack(gs, _), Msg.PackSelection.SelectPlanet(p)) =>
        (model, Cmd.Deal(gs.usePlanet(p).advanceBlind))
      case (Model.OpeningPack(gs, _), Msg.PackSelection.SelectJoker(j)) =>
        (model, Cmd.Deal(gs.addJoker(j).advanceBlind))
      case (Model.OpeningPack(gs, _), Msg.PackSelection.SkipPack) =>
        (model, Cmd.Deal(gs.advanceBlind))
      case (_, Msg.InternalEffect.RoundWon(gs)) =>
        (Model.RoundWon(gs), Cmd.NoOp)
      case (_, Msg.InternalEffect.RoundLost(blind, score)) =>
        (Model.RoundLost(blind, score), Cmd.NoOp)
      case (_, Msg.InternalEffect.ShopReady(gs, shop)) =>
        (Model.InShop(gs, shop), Cmd.NoOp)
      case (_, Msg.InternalEffect.ShopReady(gs, shop)) =>
        (Model.InShop(gs, shop), Cmd.NoOp)

      case _ => (model, Cmd.NoOp)
