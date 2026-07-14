package scalatro
package view.fx

import app.Msg
import app.Msg.ManagementAction
import view.DeckNode

import javafx.scene.layout.Pane

/** A deck node that dispatches [[ManagementAction.ShowDeck]] when clicked.
  * @param dispatch
  *   the message dispatch callback
  */
class ClickableDeck(dispatch: Msg => Unit):
  /** Mounts the deck node into the given pane.
    *
    * @param into
    *   the host pane
    */
  def mount(into: Pane): Unit =
    into.getChildren.add(DeckNode(() => dispatch(ManagementAction.ShowDeck)))
