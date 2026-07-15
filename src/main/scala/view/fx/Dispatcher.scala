package scalatro
package view.fx

import app.Msg

/** A controller capability for emitting [[Msg]]s to a configurable sink.
  *
  * Messages dispatched before a handler is set are silently dropped.
  */
trait Dispatcher:
  private var sink: Msg => Unit = _ => ()

  /** Sets the handler that receives dispatched messages.
    *
    * @param handler
    *   the message handler
    */
  final def onMessage(handler: Msg => Unit): Unit = sink = handler

  /** Sends a message to the configured handler.
    *
    * @param msg
    *   the message to send
    */
  protected final def dispatch(msg: Msg): Unit = sink(msg)
