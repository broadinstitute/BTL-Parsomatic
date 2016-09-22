package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/22/2016.
  */
class GetErccStats(input: Iterator[String]) {
  val stats = scala.collection.mutable.ListBuffer[String]("TOTAL_READS\tERCC_READS\tUNALIGNED_ERCC_READS\tPCT_NOT_ERCC\tPCT_ERCC\tPCT_UNALIGNED_ERCC")

  def getStats:Either[String, Iterator[String]] = {
    val statList = input.toList
    if ((stats.length == 1) && (statList.length==6)) {
      stats += statList.mkString("\t")
      stats.foreach(println)
      Right(stats.toIterator)
    }
    else Left("GetErccStats.getStats failed")
  }
}
