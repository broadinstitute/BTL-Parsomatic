package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/22/2016.
  */
/**
  * Converts results of get_ercc_stats.py into format usable by Parsomatic.
  */

object GetErccStats {
  // It might be interesting to use reflection in the future to get the case class param names. This would
  // require refactoring at several levels as then headers and case class params would have to be same everywhere.
  private val headers = List(
    "TOTAL_READS",
    "ERCC_READS",
    "UNALIGNED_ERCC_READS",
    "FRC_GENOME_REF",
    "FRC_ERCC_READS",
    "FRC_UNALIGNED_ERCC"
  )

  /**
    *
     * @param input A list of strings containing ERCC stats.
    * @param delim The delimiter to used as string separators.
    * @return
    */
  def getStats(input: String, delim: String):Either[String, List[String]] = {

    val input_list = input.split(delim)
    val head = input_list.head.split(delim)
    val data = input_list(1).split(delim)
    if (data.length==headers.length) {
      Right(List(head.mkString(delim), data.mkString(delim)))
    }
    else Left(s"GetErccStats.getStats failed: Expected input length ${headers.length}, received ${input.length}.")
  }
}
