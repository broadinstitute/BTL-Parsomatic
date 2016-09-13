package org.broadinstitute.parsomatic

/**
  * Created by Amr on 9/9/2016.
  */
class ParsomaticParser(iter: Either[String, Iterator[String]], delim: String) {
  def parseToMap() = {
    var metrics = scala.collection.mutable.ListBuffer[Map[String, String]]()
    for (x <- iter.right) {
      val header = x.next().split(delim)
      while (x.hasNext) {
        val row = header zip x.next().split(delim)
        val mappedRow = row.toMap
        metrics += mappedRow
      }
    }
    metrics
  }
}
