package org.indyscala.nov2013.discard

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import akka.io.{IO, Tcp}, Tcp._
import java.net.InetSocketAddress

object Discard extends App {
  val system = ActorSystem()
  system.actorOf(Props[DiscardServer])
}

class DiscardServer extends Actor {
  import Tcp._
  import context.system

  private var boundAddress: Option[InetSocketAddress] = None

  override def preStart() {
    super.preStart()
    IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 0))
  }

  def receive = {
    case b @ Bound(localAddress) =>
      println(s"Bound to port ${localAddress.getPort}")
      boundAddress = Some(localAddress)

    case CommandFailed(Bind(_, localAddress, _, _)) =>
      System.err.println("Failed to bind to "+localAddress)
      context.stop(self)

    case c @ Connected(remote, local) =>
      val connection = sender
      connection ! Register(handler(remote, local))
  }

  protected def handler(remote: InetSocketAddress, local: InetSocketAddress): ActorRef =
    system.actorOf(Props(classOf[DiscardActor], remote))
}

class DiscardActor(remote: InetSocketAddress) extends Actor {
  override def preStart(): Unit = {
    super.preStart()
    println(s"Received connection from ${remote}")
  }

  override def postStop(): Unit = {
    println(s"Terminating connection from ${remote}")
    super.postStop()
  }

  def receive = {
    case Received(data) =>
      println(s"Discarding ${data} ... nom nom nom")

    case PeerClosed =>
      println(s"Peer closed for ${remote}.")
      context.stop(self)
  }
}