package org.broadinstitute.parsomatic
/**
  * Created by Amr on 9/20/2016.
  */
class GetPicardMeanQualMetrics(input: Iterator[String], delim: String) {
  val means = scala.collection.mutable.ListBuffer[String]()

  def getMeans() = {
    for (mean <- input.drop(1)) {
      means += mean.split("\t")(1)
    }
    val int_means = means.map(_.toDouble)
    val (r1, r2) = int_means.splitAt(means.length/2)
    List(r1.foldLeft(0.0)(_+_)/r1.length, r2.foldLeft(0.0)(_+_)/r2.length)
  }
}
