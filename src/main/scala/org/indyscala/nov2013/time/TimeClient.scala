package org.indyscala.nov2013
package time

import akka.actor.{ActorRef, Props, ActorSystem}
import org.indyscala.nov2013.{Listener, Client}
import akka.util.ByteString
import java.util.Date

object TimeClient extends Client {
  protected def connectionListener: ActorRef = system.actorOf(Props[TimeClientHandler])
}

class TimeClientHandler extends Listener {
  protected override def onReceived(data: ByteString): Unit = {
    val time = ((data.iterator.getInt & 0xFFFFFFFFL) - 2208988800L) * 1000L
    println(new Date(time))
    context.stop(self)
  }
}