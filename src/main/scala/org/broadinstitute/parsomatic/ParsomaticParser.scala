package org.broadinstitute.parsomatic
import scala.annotation.tailrec

/**
  * Created by Amr on 9/9/2016.
  */
class ParsomaticParser(iter: Iterator[String], delim: String) {
  def parseToMap() = {
    val header = iter.next().split(delim)
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
