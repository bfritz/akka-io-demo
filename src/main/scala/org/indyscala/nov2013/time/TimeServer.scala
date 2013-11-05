package org.indyscala.nov2013
package time

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import akka.io.{IO, Tcp}, Tcp._
import java.net.InetSocketAddress
import org.indyscala.nov2013.Server
import akka.util.ByteString
import java.nio.ByteOrder

object TimeServer extends Server {
  protected def connectionListener: ActorRef = system.actorOf(Props[TimeServerHandler])
}

class TimeServerHandler extends Listener {
  override protected def onConnected(remote: InetSocketAddress, local: InetSocketAddress): Unit = {
    super.onConnected(remote, local)
    val bs = ByteString.newBuilder
      .putInt((System.currentTimeMillis / 1000L + 2208988800L).toInt)
      .result()
    sender ! Write(bs)
    sender ! Close
  }
}
