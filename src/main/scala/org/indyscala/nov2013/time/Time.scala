package org.indyscala.nov2013.time

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import akka.io.{IO, Tcp}, Tcp._
import java.net.InetSocketAddress
import org.indyscala.nov2013.{InboundHandler, InboundActor, Server}
import akka.util.ByteString

object Time extends App {
  val system = ActorSystem()
  system.actorOf(Props[TimeServer])
}

class TimeServer extends Server {
  protected def newHandler: ActorRef = context.system.actorOf(Props[TimeHandler])
}

class TimeHandler extends InboundHandler {
  override protected def onConnected(remote: InetSocketAddress, local: InetSocketAddress): Unit = {
    super.onConnected(remote, local)
    sender ! Write(ByteString(System.currentTimeMillis / 1000L + 2208988800L))
  }

  protected def onReceived(data: ByteString): Unit =
    sender ! Write(data)
}