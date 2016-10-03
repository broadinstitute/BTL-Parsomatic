package org.broadinstitute.parsomatic
import scala.math.BigDecimal.RoundingMode

/**
  * Created by amr on 9/22/2016.
  */
class GetPicardMeanGcMetrics(input: Iterator[String], delim: String) {
  var observations = 0

  def convertAndMultiply(str: String): Int = {
    val intList = str.split(delim)
    observations += intList(1).toInt
    intList(0).toInt * intList(1).toInt
  }

  def getMean: Either[String, Iterator[String]] = {
    try {
      val denominators = input.drop(1).map(convertAndMultiply(_))
      val meanGc = BigDecimal(denominators.sum/observations).setScale(2, RoundingMode.HALF_EVEN)
      if (meanGc > 0) Right(Iterator("MEAN_GC_CONTENT", meanGc.toString))
      else Left("GetPicardMeanGcMetrics.getMean failed unexpectedly")
    } catch {
      case nfe: NumberFormatException => Left("GetPicardMeanGcMetrics.getMean failed:" + nfe.getMessage)
      case _: Throwable => Left("GetPicardMeanGcMetrics.getMean failed with unknown error")
    }

  }
}
