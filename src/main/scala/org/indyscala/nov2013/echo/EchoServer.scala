package org.indyscala.nov2013
package echo

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import akka.io.{IO, Tcp}, Tcp._
import akka.util.ByteString

object EchoServer extends Server {
  protected def connectionListener: ActorRef = system.actorOf(Props[EchoServerHandler])
}

class EchoServerHandler extends Listener {
  protected override def onReceived(data: ByteString): Unit = sender ! Write(data)
}