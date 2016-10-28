package org.broadinstitute.parsomatic
import com.typesafe.scalalogging.Logger

import scala.math.BigDecimal.RoundingMode

/**
  * Created by amr on 9/22/2016.
  */
class GetPicardMeanGcMetrics(input: Iterator[String], delim: String) {
  var observations = 0
  val logger = Logger("GetPicardMeanGcMetrics")
  def convertAndMultiply(str: String): Long = {
    val intList = str.split(delim)
    observations += intList(1).toInt
    intList(0).toLong * intList(1).toLong
  }

  def getMean: Either[String, Iterator[String]] = {
    try {
      val new_input = input.drop(2).toList
      logger.debug(s"Observations found: $observations")
      val sum_bin_totals = new_input.map(convertAndMultiply(_)).sum
      logger.debug(s"Sum of GC bins: $sum_bin_totals")
      val meanGc = BigDecimal(sum_bin_totals/observations).setScale(2, RoundingMode.HALF_EVEN)
      logger.debug(s"meanGc = $sum_bin_totals/$observations = $meanGc")
      if (meanGc > 0) {
        Right(Iterator("MEAN_GC_CONTENT", meanGc.toString))
      }
      else Left("GetPicardMeanGcMetrics.getMean failed unexpectedly")
    } catch {
      case nfe: NumberFormatException => Left("GetPicardMeanGcMetrics.getMean failed:" + nfe.getMessage)
      case _: Throwable => Left("GetPicardMeanGcMetrics.getMean failed with unknown error")
    }

  }
}
