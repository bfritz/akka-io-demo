package org.indyscala.nov2013
package time2

import akka.actor.{ActorRef, Props, ActorSystem}
import org.indyscala.nov2013.{Listener, Client}
import akka.util.ByteString
import java.util.Date
import akka.io.{PipelineFactory, PipelinePorts, PipelineContext}

object TimeClient extends Client {
  protected def connectionListener: ActorRef = system.actorOf(Props[TimeClientHandler])
}

class TimeClientHandler extends Listener {
  val ctx = new PipelineContext {}
  val PipelinePorts(cmd, evt, mgmt) = PipelineFactory.buildFunctionTriple(ctx, new LongCodec)

  protected override def onReceived(data: ByteString): Unit = {
    val (longs, _) = evt(data)
    longs foreach { l => println(new Date(l)) }
    context.stop(self)
  }
}