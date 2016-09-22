package org.broadinstitute.parsomatic
import scala.math.BigDecimal.RoundingMode
/**
  * Created by Amr on 9/20/2016.
  */
class GetPicardMeanQualMetrics(input: Iterator[String], delim: String) {
  val means = scala.collection.mutable.ListBuffer[String]()

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
