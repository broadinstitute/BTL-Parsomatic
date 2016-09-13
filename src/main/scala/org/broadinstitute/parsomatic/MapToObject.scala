package org.broadinstitute.parsomatic
import org.broadinstitute.MD.types._
import scala.collection.mutable.ListBuffer
import org.broadinstitute.parsomatic._
/**
  * Created by Amr on 9/12/2016.
  */
class MapToObject(mdType: String, input: ListBuffer[Map[String, String]]) {
  mdType match {
    case "PicardAlignmentSummaryMetrics" => for (row <- input) new PicardAlignmentSummaryMetrics(
      category = List(row.getOrElse("CATEGORY", "None")),
      totalReads = List(row.getOrElse("TOTAL_READS", "").asInstanceOf[Int]),
      pfReads = List(row.getOrElse("PC_READS", "").asInstanceOf[Int]),
      pctPfReads = List(row.getOrElse("PCT_PF_READS", "").asInstanceOf[Double]),
      pfNoiseReads = List(row.getOrElse("PF_NOISE_READS", "").asInstanceOf[Int]),
      pfReadsAligned = List(row.getOrElse("PF_READS_ALIGNED", "").asInstanceOf[Int]),
      pctPfReadsAligned = List(row.getOrElse("PCT_PF_READS_ALIGNED", "").asInstanceOf[Double]),
      pfAlignedBases = List(row.getOrElse("PF_ALIGNED_BASES", "").asInstanceOf[Int]),
      pfHqAlignedReads = List(row.getOrElse("PF_ALIGNED_BASES", "").asInstanceOf[Int]),
      pfHqAlignedBases= List(row.getOrElse("PF_ALIGNED_BASES", "").asInstanceOf[Int]),
      pfHqAlignedQ20Bases = List(row.getOrElse("PF_HQ_ALIGNED_20_BASES", "").asInstanceOf[Int])
      pfHqMedianMismatches = List(row.getOrElse("PF_HQ_MEDIAN_MISMATCHES", "")),
      pfMismatchRate = List(row.getOrElse("PF_MISMATCH_RATE", "").asInstanceOf[Double]),
      pfHqErrorRate = List(row.getOrElse("PF_HQ_ERROR_RATE", "").asInstanceOf[Double]),
      pfIndelRate = List(row.getOrElse("PF_INDEL_RATE", "").asInstanceOf[Double]),
      meanReadLength = List(row.getOrElse("MEAN_READ_LENGTH", "").asInstanceOf[Int]),
      readsAlignedInPairs = List(row.getOrElse("READS_ALIGNED_IN_PAIRS", "").asInstanceOf[Int]),
      pctReadsAlignedInPairs = List(row.getOrElse("PCT_READS_ALIGNED_IN_PAIRS", "").asInstanceOf[Int]),
      badCycles = List(row.getOrElse("BAD_CYCLES", "").asInstanceOf[Int]),
      strandBalance = List(row.getOrElse("STRAND_BALANCE", "").asInstanceOf[Int]),
      pctChimeras = List(row.getOrElse("PCT_CHIMERAS", "").asInstanceOf[Double]),
      pctAdapter = List(row.getOrElse("PCT_ADAPTER", "").asInstanceOf[Double])
    )
    case "PicardInsertSizeMetrics" => println(mdType)
    case _ => println("unrecognized input for MapToObject")
      System.exit(1)
  }
}
