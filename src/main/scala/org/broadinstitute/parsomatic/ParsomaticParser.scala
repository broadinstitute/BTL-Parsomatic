package org.broadinstitute.parsomatic
import com.typesafe.scalalogging.Logger

import scala.annotation.tailrec

/**
  * Created by Amr on 9/9/2016.
  */
/**
  * Parses the Iterator representation of the metrics file into a map of K/V pairs.
  * @param iter The iterator representation of the metrics file.
  * @param delim The delimiter that split keys and values in the metrics file.
  */
class ParsomaticParser(iter: Iterator[String], delim: String) {
  val logger = Logger("Parsomatic.ParsomaticParser")
  def parseToMap() = {
    val header = iter.next().split(delim)
    logger.info("Parsing file iterator to map.")
    def populateMetrics(lb: scala.collection.mutable.ListBuffer[Map[String,String]]):
    List[Map[String,String]] = {
      @tailrec
      def metricAccumulator(lb: scala.collection.mutable.ListBuffer[Map[String,String]]):
      List[Map[String,String]] = {
        if (iter.hasNext) {
          val row = header zip iter.next().split(delim)
          lb += row.toMap
          metricAccumulator(lb)
        } else {
          lb.toList
        }
      }
      metricAccumulator(lb)
    }
  populateMetrics(scala.collection.mutable.ListBuffer[Map[String, String]]())
  }
}
