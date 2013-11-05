package org.indyscala

import akka.io.Tcp.Event
import java.net.InetSocketAddress
import java.nio.ByteOrder

package object nov2013 {
  val DefaultInetSocketAddress = new InetSocketAddress("localhost", 47990)
  implicit val DefaultByteOrder: ByteOrder = ByteOrder.BIG_ENDIAN
  case object Ack extends Event
}
