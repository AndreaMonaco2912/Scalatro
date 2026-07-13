package scalatro
package view.fx

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global

/** A controller that receives user actions through an injected queue.
  *
  * Owns the queue and the offer plumbing, so each concrete controller only
  * calls [[offer]] from its event handlers.
  */
trait Bindable[A]:
  private var actionQueue: Option[Queue[IO, A]] = None

  /** Injects the action queue. Must be called before the controller emits. */
  final def setActionQueue(queue: Queue[IO, A]): Unit =
    actionQueue = Some(queue)

  /** Forwards an action to the queue, if one has been injected. */
  protected def offer(action: A): Unit =
    actionQueue.foreach(_.offer(action).unsafeRunAndForget())
