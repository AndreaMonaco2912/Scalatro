package scalatro
package view.fxController

import app.Msg.ManagementAction
import view.DeckNode

import javafx.scene.layout.Pane

trait ClickableDeck extends Dispatcher:

  def mountDeck(into: Pane): Unit =
    into.getChildren.add(DeckNode(() => dispatch(ManagementAction.ShowDeck)))
