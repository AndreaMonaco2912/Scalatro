package scalatro
package view.fx

import app.Msg
import app.Msg.ManagementAction
import view.DeckNode

import javafx.scene.layout.Pane

class ClickableDeck(dispatch: Msg => Unit):
  def mount(into: Pane): Unit =
    into.getChildren.add(DeckNode(() => dispatch(ManagementAction.ShowDeck)))
