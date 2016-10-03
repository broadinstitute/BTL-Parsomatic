package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/22/2016.
  */
/**
  * Converts results of get_ercc_stats.py into format usable by Parsomatic.
  * @param input An iterator representing the ercc stats file.
  */
class GetErccStats(input: Iterator[String]) {
  val stats = scala.collection.mutable.ListBuffer[String]("TOTAL_READS\tERCC_READS\tUNALIGNED_ERCC_READS\tFRC_GENOME_REF\tFRC_ERCC_READS\tFRC_UNALIGNED_ERCC")

  /**
    * Returns an iterator formatted for Parsomatic.
    * @return
    */
  def getStats:Either[String, Iterator[String]] = {
    val statList = input.toList
    if (statList.length==6) {
      stats += statList.mkString("\t")
      Right(stats.toIterator)
    }
    else Left("GetErccStats.getStats failed.")
  }
}
