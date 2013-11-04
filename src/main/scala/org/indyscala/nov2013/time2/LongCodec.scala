package org.indyscala.nov2013
package time2

import akka.io.{SymmetricPipePair, PipelineContext, SymmetricPipelineStage}
import akka.util.ByteString
import java.lang.{Long => JLong}
import scala.annotation.tailrec

class LongCodec extends SymmetricPipelineStage[PipelineContext, JLong, ByteString] {
  override def apply(ctx: PipelineContext) = new SymmetricPipePair[JLong, ByteString] {
    override def commandPipeline = { long: JLong =>
      val bs = ByteString.newBuilder.putInt((long / 1000L + 2208988800L).toInt).result
      ctx.singleCommand(bs)
    }

    @tailrec
    def extractFrames(bs: ByteString, acc: List[JLong]): (Option[ByteString], Seq[JLong]) = {
      if (bs.isEmpty) {
        (None, acc)
      } else if (bs.length < 4) {
        (Some(bs.compact), acc)
      } else {
        extractFrames(bs drop 4, ((bs.iterator.getInt & 0xFFFFFFFFL) - 2208988800L) * 1000L :: acc)
      }
    }

    var buffer: Option[ByteString] = None
    override def eventPipeline = { bs: ByteString =>
      val data = if (buffer.isEmpty) bs else buffer.get ++ bs
      val (newBuffer, frames) = extractFrames(data, Nil)
      buffer = newBuffer
      frames match {
        case Nil        => Nil
        case one :: Nil => ctx.singleEvent(one)
        case many       => many reverseMap (Left(_))
      }
    }
  }
}