package org.broadinstitute.parsomatic
import org.broadinstitute.MD.types._
import org.broadinstitute.parsomatic.Parsomatic.failureExit
/**
  * Created by Amr on 9/12/2016.
  */
class MapToObjects(mdType: String, input: List[Map[String, String]]) {
  def converter[T](row: Map[String, String], key: String, default: String, cvt: String => T) =
    cvt(row.getOrElse(key, default))

  def convertToInt(row: Map[String, String], key: String, default: String) =
    converter(row, key, default, (_: String).toInt)

  def convertToDouble(row: Map[String, String], key: String, default: String) =
    converter(row, key, default, (_: String).toDouble)

  sealed trait Metrics
  val metrics = scala.collection.mutable.ListBuffer[Metrics]()

  def go() = {
    mdType match {
      case "PicardAlignmentMetrics" => for (row <- input) metrics += new PicardAlignmentSummaryMetrics(
        category = row.getOrElse("CATEGORY", "N/A"),
        totalReads = convertToInt(row, "TOTAL_READS", "0"),
        pfReads = convertToInt(row, "PC_READS", "0"),
        pctPfReads = convertToDouble(row, "PCT_PF_READS", "0.0"),
        pfNoiseReads = convertToInt(row, "PF_NOISE_READS", "0"),
        pfReadsAligned = convertToInt(row, "PF_READS_ALIGNED", "0"),
        pctPfReadsAligned = convertToDouble(row, "PCT_PF_READS_ALIGNED", "0.0"),
        pfAlignedBases = convertToInt(row, "PF_ALIGNED_BASES", "0"),
        pfHqAlignedReads = convertToInt(row, "PF_ALIGNED_BASES", "0"),
        pfHqAlignedBases = convertToInt(row, "PF_ALIGNED_BASES", "0"),
        pfHqAlignedQ20Bases = convertToInt(row, "PF_HQ_ALIGNED_20_BASES", "0"),
        pfHqMedianMismatches = convertToDouble(row, "PF_HQ_MEDIAN_MISMATCHES", "0.0"),
        pfMismatchRate = convertToDouble(row, "PF_MISMATCH_RATE", "0.0"),
        pfHqErrorRate = convertToDouble(row, "PF_HQ_ERROR_RATE", "0.0"),
        pfIndelRate = convertToDouble(row, "PF_INDEL_RATE", "0.0"),
        meanReadLength = convertToInt(row, "MEAN_READ_LENGTH", "0"),
        readsAlignedInPairs = convertToInt(row, "READS_ALIGNED_IN_PAIRS", "0"),
        pctReadsAlignedInPairs = convertToDouble(row, "PCT_READS_ALIGNED_IN_PAIRS", "0.0"),
        badCycles = convertToInt(row, "BAD_CYCLES", "0"),
        strandBalance = convertToInt(row, "STRAND_BALANCE", "0"),
        pctChimeras = convertToDouble(row, "PCT_CHIMERAS", "0.0"),
        pctAdapter = convertToDouble(row, "PCT_ADAPTER", "0.0")
      ) with Metrics
        metrics.toList
      case "PicardInsertSizeMetrics" => for (row <- input) metrics += new PicardInsertSizeMetrics(
        medianInsertSize = convertToInt(row, "MEDIAN_INSERT_SIZE", "0"),
        medianAbsoluteDeviation = convertToInt(row, "MEDIAN_ABSOLUTE_DEVIATION", "0"),
        minInsertSize = convertToInt(row, "MIN_INSERT_SIZE", "0"),
        maxInsertSize = convertToInt(row, "MAX_INSERT_SIZE", "0"),
        meanInsertSize = convertToDouble(row, "MEAN_INSERT_SIZE", "0.0"),
        standardDeviation = convertToDouble(row, "STANDARD_DEVIATION", "0.0"),
        readPairs = convertToInt(row, "READ_PAIRS", "0"),
        pairOrientation = row.getOrElse("PAIR_ORIENTATION", "N/A"),
        widthOf10Pct = convertToInt(row, "WIDTH_OF_10_PERCENT", "0"),
        widthOf20Pct = convertToInt(row, "WIDTH_OF_20_PERCENT", "0"),
        widthOf30Pct = convertToInt(row, "WIDTH_OF_30_PERCENT", "0"),
        widthOf40Pct = convertToInt(row, "WIDTH_OF_40_PERCENT", "0"),
        widthOf50Pct = convertToInt(row, "WIDTH_OF_50_PERCENT", "0"),
        widthOf60Pct = convertToInt(row, "WIDTH_OF_60_PERCENT", "0"),
        widthOf70Pct = convertToInt(row, "WIDTH_OF_70_PERCENT", "0"),
        widthOf80Pct = convertToInt(row, "WIDTH_OF_80_PERCENT", "0"),
        widthOf90Pct = convertToInt(row, "WIDTH_OF_90_PERCENT", "0"),
        widthOf99Pct = convertToInt(row, "WIDTH_OF_99_PERCENT", "0")
      ) with Metrics
        metrics.toList
      case _ => failureExit("unrecognized mdType input for MapToObject")
    }
  }
}