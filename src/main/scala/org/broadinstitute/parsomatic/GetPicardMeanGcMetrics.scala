package org.broadinstitute.parsomatic
import com.typesafe.scalalogging.Logger

import scala.math.BigDecimal.RoundingMode
import scala.util.control.NonFatal

/**
  * Created by amr on 9/22/2016.
  * A tool for calculating the mean GC from a Picard Tools GC histogram.
  */

object GetPicardMeanGcMetrics{
  private val logger = Logger("GetPicardMeanGcMetrics")
  //Header_lines refers to the lines prior to the actual data that we will want to drop.
  private val header_lines = 2

  def getMean(input: List[String], delim: String): Either[String, List[String]] = {
    try {
      val new_input = input.drop(header_lines)
      //Here fold left is used to get total observations and total reads across all observations.
      val (observations, sum_bin_totals) = new_input.foldLeft((0, 0L)) {
        case((seen, sum), next) =>
          val intList = next.split(delim)
          (seen + intList(1).toInt, sum +(intList(0).toLong * intList(1).toLong))
      }
      logger.debug(s"Observations found: $observations")
      logger.debug(s"Sum of GC bins: $sum_bin_totals")
      // Using the resolds of fold left we can calculate meanGc as follows.
      val meanGc = BigDecimal(sum_bin_totals/observations).setScale(2, RoundingMode.HALF_EVEN)
      logger.debug(s"meanGc = $sum_bin_totals/$observations = $meanGc")
      if (meanGc > 0) {
        Right(List("MEAN_GC_CONTENT", meanGc.toString))
      }
      else Left("GetPicardMeanGcMetrics.getMean failed: meanGC was less than zero.")
    } catch {
      case NonFatal(e) => Left("GetPicardMeanGcMetrics.getMean failed:" + e.getLocalizedMessage)
    }
  }
}
