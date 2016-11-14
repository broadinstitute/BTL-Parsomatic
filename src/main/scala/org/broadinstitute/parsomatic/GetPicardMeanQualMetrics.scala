package org.broadinstitute.parsomatic
import scala.math.BigDecimal.RoundingMode
import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal
/**
  * Created by Amr on 9/20/2016.
  */
/**
  * Processes picard mean quality histogram into read 1(r1) and read 2(r2) mean quality scores.
  */
object GetPicardMeanQualMetrics{
  private val header_lines = 1

  /**
    * Process the histogram by converting values to double. Gets r1 and r2 by splitting histogram in half, and assigning
    * the first half to r1 and second half to r2, summing each, and getting mean value.
    *
    * @return
    */
  def getMeans(input: List[String], delim: String) : Either[String, List[String]] = {
    val means = ListBuffer[String]()
    for (mean <- input.drop(header_lines)) {
      means += mean.split(delim)(1)
    }
    try {
      val int_means = means.map(_.toDouble)
      val (r1, r2) = int_means.splitAt(means.length/2)
      val r1MeanQual = BigDecimal(r1.foldLeft(0.0)(_+_)/r1.length).setScale(2, RoundingMode.HALF_EVEN)
      val r2MeanQual = BigDecimal(r2.foldLeft(0.0)(_+_)/r2.length).setScale(2, RoundingMode.HALF_EVEN)
      Right(List(s"R1_MEAN_QUAL${delim}R2_MEAN_QUAL", r1MeanQual.toString.concat(delim).concat(r2MeanQual.toString)))
    } catch {
      case NonFatal(e) => Left("GetPicardMeanQualMetrics.getMeans failed:" + e.getLocalizedMessage)
    }

  }
}
