package org.indyscala.nov2013
package discard

import akka.actor.{ActorRef, ActorSystem, Props}
import java.net.InetSocketAddress
import akka.util.ByteString

object Discard extends App {
  val system = ActorSystem()
  system.actorOf(Props[DiscardServer])
}

class DiscardServer extends Server {
  protected def newHandler: ActorRef = context.system.actorOf(Props[DiscardHandler])
}

class DiscardHandler extends InboundHandler {
  protected def onReceived(data: ByteString): Unit = {
    println(s"Discarding ${data} ... nom nom nom")
  }
}