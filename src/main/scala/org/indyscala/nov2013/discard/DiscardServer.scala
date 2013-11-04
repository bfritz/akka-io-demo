package org.indyscala.nov2013
package discard

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

object DiscardServer extends Server {
  protected def connectionListener: ActorRef = system.actorOf(Props[DiscardHandler])
}

class DiscardHandler extends Listener {
  protected override def onReceived(data: ByteString): Unit = {
    println(s"Discarding ${data} ... nom nom nom")
  }
}