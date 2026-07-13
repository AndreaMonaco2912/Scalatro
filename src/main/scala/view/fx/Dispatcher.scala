package scalatro
package view.fx

import app.Msg

trait Dispatcher:
  private var sink: Msg => Unit = _ => ()
  final def onMessage(handler: Msg => Unit): Unit = sink = handler
  protected final def dispatch(msg: Msg): Unit = sink(msg)