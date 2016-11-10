package org.broadinstitute.parsomatic
import com.typesafe.scalalogging.Logger
import scala.math.BigDecimal.RoundingMode

/**
  * Created by amr on 9/22/2016.
  */

// TODO: Can convert this to an object which is more efficient memory wise because you don't have to to make this
// more than once. In general use objects when just using the thing one time. In this case, input can be moved to
// the method call as opposed to being part of the class.
// TODO: Replace output of iterator with list.
// TODO: Use private val for any val that is only being used in the scope of the val exists in. This is good documentation
// but also can possibly be more efficient for the compiler.
// TODO: print more informative error for case e: Exception
// TODO: More comments in general.
// TODO: Should never make a var public because should.
// TODO: Can make this an object.
// TODO: Use object instead of a class unless you are going to use the thing more than once.
class GetPicardMeanGcMetrics(input: Iterator[String], delim: String) {
  var observations = 0 //TODO: Can use fold left instead of observations over a this as a tuple where I sum observations.
  // as well as the other thing in this list. Inside the fold, use GetOrElse to defend against empty values.
  val logger = Logger("GetPicardMeanGcMetrics")

  def convertAndMultiply(str: String): Long = {
    val intList = str.split(delim)
    // TODO: If intList length isn't 2 we should fail gracefully.
    observations += intList(1).toInt
    intList(0).toLong * intList(1).toLong
  }

  def getMean: Either[String, Iterator[String]] = {
    try {
      //TODO: should use a header_length private val in class instead of the #2 so to make this more clear what the two is doing.
      val new_input = input.drop(2).toList
      logger.debug(s"Observations found: $observations")
      val sum_bin_totals = new_input.map(convertAndMultiply(_)).sum
      //TODO: Fold here will eliminate convertandmutliply. Remember to use getOrElse.
      logger.debug(s"Sum of GC bins: $sum_bin_totals")
      //TODO: Why did I use HALF_EVEN?
      val meanGc = BigDecimal(sum_bin_totals/observations).setScale(2, RoundingMode.HALF_EVEN)
      logger.debug(s"meanGc = $sum_bin_totals/$observations = $meanGc")
      if (meanGc > 0) {
        Right(Iterator("MEAN_GC_CONTENT", meanGc.toString))
      }
      else Left("GetPicardMeanGcMetrics.getMean failed because meanGC was less than or greater than zero.")
    } catch {
      case nfe: NumberFormatException => Left("GetPicardMeanGcMetrics.getMean failed:" + nfe.getLocalizedMessage)
      // TODO: Can make the error below more informative
      case e: Exception => Left("GetPicardMeanGcMetrics.getMean failed with unknown error")
      case _ => Left("GetPicardMeanGcMetrics.getMean failed with unknown error")
    }
  }
}
