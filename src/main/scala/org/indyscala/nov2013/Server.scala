package org.indyscala.nov2013

import akka.actor.{ActorSystem, Props, ActorRef, Actor}
import akka.io.{IO, Tcp}, Tcp._
import java.net.{InetAddress, InetSocketAddress}

abstract class Server(val address: InetSocketAddress = DefaultInetSocketAddress) extends App {
  implicit lazy val system = ActorSystem()
  protected def connectionListener: ActorRef

  class ServerActor extends Actor {
    override def preStart() {
      super.preStart()
      println(s"Starting server on ${address}")
      IO(Tcp) ! Bind(self, address)
    }

    def receive = {
      case b @ Bound(localAddress) =>
        println(s"Bound to port ${localAddress}")

      case CommandFailed(Bind(_, localAddress, _, _)) =>
        System.err.println(s"Failed to bind to ${localAddress}")
        context.stop(self)

      case connected @ Connected(remote, local) =>
        val connection = sender
        val listener = connectionListener
        listener.forward(connected)
        connection ! Register(listener)
    }
  }

  system.actorOf(Props(new ServerActor))
}

