package org.indyscala.nov2013

import akka.actor.{ActorRef, Actor}
import akka.io.{IO, Tcp}, Tcp._
import java.net.InetSocketAddress
import scala.Some
import akka.io.Tcp.Connected
import akka.util.ByteString

trait Server extends Actor {
  import Tcp._
  import context.system

  override def preStart() {
    super.preStart()
    IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 0))
  }

  def receive = {
    case b @ Bound(localAddress) =>
      println(s"Bound to port ${localAddress.getPort}")

    case CommandFailed(Bind(_, localAddress, _, _)) =>
      System.err.println("Failed to bind to "+localAddress)
      context.stop(self)

    case connected @ Connected(remote, local) =>
      val connection = sender
      val handler = newHandler
      handler.forward(connected)
      connection ! Register(handler)
  }

  protected def newHandler: ActorRef
}

trait InboundHandler extends Actor {
  protected var remote: Option[InetSocketAddress] = None
  protected var local: Option[InetSocketAddress] = None

  def receive = {
    case Connected(remote, local) => onConnected(remote, local)
    case closed: ConnectionClosed => onConnectionClosed(closed)
    case Received(data) => onReceived(data)
  }

  protected def onConnected(remote: InetSocketAddress, local: InetSocketAddress): Unit = {
    println(s"Connected to ${remote}")
    this.remote = Some(remote)
    this.local = Some(local)
  }

  protected def onConnectionClosed(closed: ConnectionClosed): Unit = {
    remote match {
      case Some(address) => println(s"Disconnected from ${address}")
      case None => println("Disconnected from ... huh?  We were connected?")
    }
    context.stop(self)
  }

  protected def onReceived(data: ByteString): Unit
}