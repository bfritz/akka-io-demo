package org.indyscala.nov2013

import akka.actor.Actor
import java.net.InetSocketAddress
import akka.io.Tcp.{Received, ConnectionClosed, Connected}
import akka.util.ByteString

trait Listener extends Actor {
  protected var remote: Option[InetSocketAddress] = None
  protected var local: Option[InetSocketAddress] = None

  def receive = {
    case Connected(remote, local) => onConnected(remote, local)
    case closed: ConnectionClosed => onConnectionClosed(closed)
    case Received(data) => onReceived(data)
    case Ack => onAck
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

  protected def onReceived(data: ByteString): Unit = {}

  protected def onAck(): Unit = {
    println(s"Ack on write to ${remote}")
  }
}
