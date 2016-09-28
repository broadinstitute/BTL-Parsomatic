package org.broadinstitute.parsomatic
import scala.math.BigDecimal.RoundingMode
/**
  * Created by Amr on 9/20/2016.
  */
/**
  * Processes picard mean quality histogram into read 1(r1) and read 2(r2) mean quality scores.
  * @param input The iterator containing the histogram values.
  * @param delim The delimiter that separates histogram bins from their values.
  */
class GetPicardMeanQualMetrics(input: Iterator[String], delim: String) {
  val means = scala.collection.mutable.ListBuffer[String]()

  /**
    * Process the histogram by converting values to double. Gets r1 and r2 by splitting histogram in half, and assigning
    * the first half to r1 and second half to r2, summing each, and getting mean value.
    * @return
    */
  def getMeans: Either[String, Iterator[String]] = {
    for (mean <- input.drop(1)) {
      means += mean.split(delim)(1)
    }
    val int_means = means.map(_.toDouble)
    val (r1, r2) = int_means.splitAt(means.length/2)
    val r1MeanQual = BigDecimal(r1.foldLeft(0.0)(_+_)/r1.length).setScale(2, RoundingMode.HALF_EVEN)
    val r2MeanQual = BigDecimal(r2.foldLeft(0.0)(_+_)/r2.length).setScale(2, RoundingMode.HALF_EVEN)
      if ((r1MeanQual > -1) && (r2MeanQual > - 1)) Right(Iterator("R1_MEAN_QUAL\tR2_MEAN_QUAL",
        r1MeanQual.toString.concat("\t").concat(r2MeanQual.toString)))
      else Left("getMeans failed.")
  }
}
