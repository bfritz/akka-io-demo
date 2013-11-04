package org.indyscala.nov2013
package time2

import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import akka.io._, Tcp._
import java.net.InetSocketAddress
import org.indyscala.nov2013.Server
import akka.util.ByteString
import java.nio.ByteOrder

object TimeServer extends Server {
  protected def connectionListener: ActorRef = system.actorOf(Props[TimeServerHandler])
}

class TimeServerHandler extends Listener {
  // Think I want a TcpPipelineHandler, but I haven't made the types line up yet..
  val ctx = new PipelineContext {}
  val PipelinePorts(cmd, evt, mgmt) = PipelineFactory.buildFunctionTriple(ctx, new LongCodec)
  override protected def onConnected(remote: InetSocketAddress, local: InetSocketAddress): Unit = {
    super.onConnected(remote, local)
    val (_, byteStrings) = cmd(System.currentTimeMillis())
    byteStrings.foreach(sender ! Write(_))
  }
}
