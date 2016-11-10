package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/22/2016.
  */
/**
  * Converts results of get_ercc_stats.py into format usable by Parsomatic.
  * @param input An iterator representing the ercc stats file.
  */
// TODO: Can convert this to an object which is more efficient memory wise because you don't have to to make this
// more than once. In general use objects when just using the thing one time. In this case, input can be moved to
// the method call as opposed to being part of the class.
// TODO: Make delimiter a variable.
// TODO: Replace output of iterator with list.
// TODO: I don't really need statsList.length==6. If I can make the list, what can I do with the list to know how many
// Headers you have any can insert the delimiters.
// TODO: Improve fail statement to be more specific about seeing X columns but expecting 6.
// TODO: Use private val for any val that is only being used in the scope of the val exists in. This is good documentation
// but also can possibly be more efficient for the compiler.

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
