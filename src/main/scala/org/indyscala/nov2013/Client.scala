package org.indyscala.nov2013

import akka.actor.{Props, ActorSystem, ActorRef, Actor}
import akka.io.{IO, Tcp}, Tcp._
import java.net.InetSocketAddress

abstract class Client(val address: InetSocketAddress = DefaultInetSocketAddress) extends App {
  implicit lazy val system = ActorSystem()
  protected def connectionListener: ActorRef

  class ClientActor extends Actor {
    override def preStart() {
      super.preStart()
      println(s"Starting client on ${address}")
      IO(Tcp) ! Connect(address)
    }

    def receive = {
      case CommandFailed(_: Connect) =>
        println("Failed to connect to ${localAddress}")
        context stop self

      case c @ Connected(remote, local) =>
        val listener = connectionListener
        listener ! c
        val connection = sender
        connection ! Register(listener)
    }
  }

  system.actorOf(Props(new ClientActor))
}
