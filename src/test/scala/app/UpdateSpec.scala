package scalatro
package app

import model.commons.*
import model.commons.CardBuilder.*
import model.game.{GameState, GameStateBuilder}
import model.game.GameStateBuilder.DSL.*
import model.round.{RoundBuilder, RoundState}
import model.round.RoundBuilder.DSL.*
import model.shop.Shop

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object UpdateDSL:
  extension (model: Model)
    infix def on(msg: Msg): (Model, Cmd) = Update.update(model, msg)

    infix def !(msg: Msg): Model = model on msg match
      case (m, _) => m

    infix def ?(msg: Msg): Cmd = model on msg match
      case (_, c) => c

class UpdateSpec extends AnyFlatSpec with Matchers:
  export UpdateDSL.*

  private val gs: GameState = GameStateBuilder.configure {
    Jokers := Seq(JokerType.CleverJoker)
  }

  private val round: RoundState = RoundBuilder.configure {
    GameStateInRound := gs
  }

  private val shop: Shop = Shop(
    cardPack = Pack(Seq(2 of S, 7 of H)),
    planetPack = Pack(Seq(Planet.Pluto)),
    jokerPack = Pack(Seq(JokerType.CraftyJoker))
  )

  "InternalEffect.RoundWon" should "move to RoundWon" in:
    Model.Playing ! Msg.InternalEffect.RoundWon(round) shouldBe Model.RoundWon(
      round
    )

  "InternalEffect.RoundLost" should "move to RoundLost" in:
    Model.Playing ! Msg.InternalEffect.RoundLost(round) shouldBe Model
      .RoundLost(round)

  "InternalEffect.ShopReady" should "move to InShop" in:
    Model.Playing ! Msg.InternalEffect.ShopReady(gs, shop) shouldBe Model
      .InShop(gs, shop)

  "RoundWon on NextRound" should "Build the shop" in:
    Model.RoundWon(round) ? Msg.RoundEndAction.NextRound shouldBe Cmd
      .BuildShop(round.gameState)

  "RoundLost on Restart" should "Create a new Game" in:
    Model.RoundLost(round) ? Msg.RoundEndAction.Restart shouldBe Cmd.Deal(
      GameState.initial
    )

  "InShop on OpenCardPack" should "move to OpeningPack with the shop's card pack" in:
    Model.InShop(gs, shop) ! Msg.ShopAction.OpenCardPack shouldBe Model
      .OpeningPack(gs, OpenPack.Cards(shop.cardPack))

  "InShop on OpenPlanetPack" should "move to OpeningPack with the shop's planet pack" in:
    Model.InShop(gs, shop) ! Msg.ShopAction.OpenPlanetPack shouldBe Model
      .OpeningPack(gs, OpenPack.Planets(shop.planetPack))

  "InShop on OpenJokerPack" should "move to OpeningPack with the shop's joker pack" in:
    Model.InShop(gs, shop) ! Msg.ShopAction.OpenJokerPack shouldBe Model
      .OpeningPack(gs, OpenPack.Jokers(shop.jokerPack))

  "InShop on SkipShop" should "Advanced blind" in:
    Model.InShop(gs, shop) ? Msg.ShopAction.SkipShop shouldBe Cmd.Deal(
      gs.advanceBlind
    )

  "OpeningPack on SelectCard" should "Advance Blind on a game with the card added" in:
    val card = A of S
    Model.OpeningPack(gs, OpenPack.Cards(shop.cardPack)) ? Msg.PackSelection
      .SelectCard(card) shouldBe Cmd.Deal(gs.addCard(card).advanceBlind)

  "OpeningPack on SelectPlanet" should "Advance Blind on a game with the planet used" in:
    val planet = Planet.Mars
    Model.OpeningPack(gs, OpenPack.Planets(shop.planetPack)) ? Msg.PackSelection
      .SelectPlanet(planet) shouldBe Cmd.Deal(gs.usePlanet(planet).advanceBlind)

  "OpeningPack on SelectJoker" should "Advance Blind on a game with the joker added" in:
    val joker = JokerType.Arrowhead
    Model.OpeningPack(gs, OpenPack.Jokers(shop.jokerPack)) ? Msg.PackSelection
      .SelectJoker(joker) shouldBe Cmd.Deal(gs.addJoker(joker).advanceBlind)

  "OpeningPack on SkipPack" should "Advance Blind" in:
    Model.OpeningPack(
      gs,
      OpenPack.Cards(shop.cardPack)
    ) ? Msg.PackSelection.SkipPack shouldBe Cmd.Deal(gs.advanceBlind)

  "ShowDeck on CloseDeck" should "return to the previous model" in:
    val previous = Model.InShop(gs, shop)
    Model.ShowDeck(
      gs.deck,
      previous
    ) ! Msg.ManagementAction.CloseDeck shouldBe previous

  "ShowLevels on CloseLevels" should "return to the previous model" in:
    val previous = Model.InShop(gs, shop)
    Model.ShowLevels(
      gs.levels,
      previous
    ) ! Msg.ManagementAction.CloseLevels shouldBe previous

  "ShowDeck" should "open showing the deck of the current model" in:
    Model.RoundWon(round) ! Msg.ManagementAction.ShowDeck shouldBe Model
      .ShowDeck(round.gameState.deck, Model.RoundWon(round))
    Model.RoundLost(round) ! Msg.ManagementAction.ShowDeck shouldBe Model
      .ShowDeck(round.gameState.deck, Model.RoundLost(round))
    Model.InShop(gs, shop) ! Msg.ManagementAction.ShowDeck shouldBe Model
      .ShowDeck(gs.deck, Model.InShop(gs, shop))
    Model.OpeningPack(
      gs,
      OpenPack.Cards(shop.cardPack)
    ) ! Msg.ManagementAction.ShowDeck shouldBe Model.ShowDeck(
      gs.deck,
      Model.OpeningPack(gs, OpenPack.Cards(shop.cardPack))
    )

  "ShowLevels" should "open showing the levels of the current model" in:
    Model.RoundWon(round) ! Msg.ManagementAction.ShowLevels shouldBe
      Model.ShowLevels(round.gameState.levels, Model.RoundWon(round))
    Model.RoundLost(round) ! Msg.ManagementAction.ShowLevels shouldBe
      Model.ShowLevels(round.gameState.levels, Model.RoundLost(round))
    Model.InShop(gs, shop) ! Msg.ManagementAction.ShowLevels shouldBe
      Model.ShowLevels(gs.levels, Model.InShop(gs, shop))

  "init" should "start a new game" in:
    Update.init shouldBe (Model.Playing, Cmd.Deal(GameState.initial))
