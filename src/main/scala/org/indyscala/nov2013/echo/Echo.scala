package org.indyscala.nov2013.echo

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import akka.io.{IO, Tcp}, Tcp._
import java.net.InetSocketAddress
import org.indyscala.nov2013.{InboundHandler, InboundActor, Server}
import akka.util.ByteString

object Echo extends App {
  val system = ActorSystem()
  system.actorOf(Props[EchoServer])
}

class EchoServer extends Server {
  protected def handlerFor(remote: InetSocketAddress, local: InetSocketAddress): ActorRef =
    context.system.actorOf(Props[EchoHandler])
}

class EchoHandler extends InboundHandler {
  protected def onReceived(data: ByteString): Unit =
    sender ! Write(data)
}