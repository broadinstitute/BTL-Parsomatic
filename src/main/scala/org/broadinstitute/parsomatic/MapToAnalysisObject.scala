package org.broadinstitute.parsomatic
import org.broadinstitute.MD.types._
/**
  * Created by Amr on 9/12/2016.
  */
/**
  * Takes the map representation of the metrics file and converts it to an mdType object.
  *
  * @param mdType A string indicating the mdType the map should be turned into.
  * @param input The map representation of the metrics file.
  */
class MapToAnalysisObject(mdType: String, input: List[Map[String, String]]) {
  def converter[T](row: Map[String, String], key: String, default: String, cvt: String => T) =
    cvt(row.getOrElse(key, default))

  /**
    * Converts a string representation of an integer to an integer.
    *
    * @param row The contents of the row containing the key.
    * @param key The key of the value to convert to an int.
    * @param default A default int to use if the key is not found.
    * @return
    */
  def convertToInt(row: Map[String, String], key: String, default: String) =
    converter(row, key, default, (_: String).toInt)

  /**
    * Converts a string representation of an double to a double.
    *
    * @param row The contents of the row containing the key.
    * @param key The key of the value to convert to an int.
    * @param default A default int to use if the key is not found.
    * @return
    */
  def convertToDouble(row: Map[String, String], key: String, default: String) =
    converter(row, key, default, (_: String).toDouble)

  /**
    * Execution engine for MapToObjects.
    *
    * @return
    */

  def go(): Either[String, AnalysisMetrics]  = {
    mdType match {
      case "PicardAlignmentMetrics" =>
        val metrics = scala.collection.mutable.ListBuffer[PicardAlignmentSummaryMetrics]()
        for (row <- input) {
        metrics += new PicardAlignmentSummaryMetrics(
          category = row.getOrElse("CATEGORY", "N/A"),
          totalReads = convertToInt(row, "TOTAL_READS", "0"),
          pfReads = convertToInt(row, "PF_READS", "0"),
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
          )
        }
        Right(PicardAlignmentSummaryAnalysis(metrics.toList))
      case "PicardInsertSizeMetrics" =>
        Right(PicardInsertSizeMetrics(
          medianInsertSize = convertToInt(input.head, "MEDIAN_INSERT_SIZE", "0"),
          medianAbsoluteDeviation = convertToInt(input.head, "MEDIAN_ABSOLUTE_DEVIATION", "0"),
          minInsertSize = convertToInt(input.head, "MIN_INSERT_SIZE", "0"),
          maxInsertSize = convertToInt(input.head, "MAX_INSERT_SIZE", "0"),
          meanInsertSize = convertToDouble(input.head, "MEAN_INSERT_SIZE", "0.0"),
          standardDeviation = convertToDouble(input.head, "STANDARD_DEVIATION", "0.0"),
          readPairs = convertToInt(input.head, "READ_PAIRS", "0"),
          pairOrientation = input.head.getOrElse("PAIR_ORIENTATION", "N/A"),
          widthOf10Pct = convertToInt(input.head, "WIDTH_OF_10_PERCENT", "0"),
          widthOf20Pct = convertToInt(input.head, "WIDTH_OF_20_PERCENT", "0"),
          widthOf30Pct = convertToInt(input.head, "WIDTH_OF_30_PERCENT", "0"),
          widthOf40Pct = convertToInt(input.head, "WIDTH_OF_40_PERCENT", "0"),
          widthOf50Pct = convertToInt(input.head, "WIDTH_OF_50_PERCENT", "0"),
          widthOf60Pct = convertToInt(input.head, "WIDTH_OF_60_PERCENT", "0"),
          widthOf70Pct = convertToInt(input.head, "WIDTH_OF_70_PERCENT", "0"),
          widthOf80Pct = convertToInt(input.head, "WIDTH_OF_80_PERCENT", "0"),
          widthOf90Pct = convertToInt(input.head, "WIDTH_OF_90_PERCENT", "0"),
          widthOf99Pct = convertToInt(input.head, "WIDTH_OF_99_PERCENT", "0")
          )
        )
      case "PicardMeanQualByCycle" =>
        Right(new PicardMeanQualByCycle(
        r1MeanQual = convertToDouble(input.head, "R1_MEAN_QUAL", "0.0"),
        r2MeanQual = convertToDouble(input.head, "R2_MEAN_QUAL", "0.0")
          )
        )
        case "RnaSeqQcStats" =>
        Right (new RnaSeqQcStats(
        sample = input.head.getOrElse("Sample", "N/A"),
        note = input.head.getOrElse("Note", "N/A"),
        alignmentMetrics = RnaSeqQcStats.AlignmentMetrics(
          mapped = convertToInt(input.head, "Mapped", "0"),
          mappedPairs = convertToInt(input.head, "Mapped Pairs", "0"),
          mappingRate = convertToDouble(input.head, "Mapping Rate", "0.0"),
          end1MappingRate = convertToDouble(input.head, "End 1 Mapping Rate", "0.0"),
          end2MappingRate = convertToDouble(input.head, "End 2 Mapping Rate", "0.0"),
          mappedUnique = convertToInt(input.head, "Mapped Unique", "0"),
          uniqueRateofMapped = convertToDouble(input.head, "Unique Rate of Mapped", "0.0"),
          mappedUniqueRateofTotal = convertToDouble(input.head, "Mapped Unique Rate of Total", "0.0"),
          alternativeAlignments = convertToInt(input.head, "Alternative Alignments", "0"),
          duplicationRateOfMapped = convertToDouble(input.head, "Duplication Rate of Mapped", "0")
        ),
        annotationMetrics = RnaSeqQcStats.AnnotationMetrics(
          rRNA = convertToInt(input.head, "rRNA", "0"),
          rRNArate = convertToDouble(input.head, "rRNA Rate", "0.0"),
          intragenicRate = convertToDouble(input.head, "Intragenic Rate", "0.0"),
          exonicRate = convertToDouble(input.head, "Exonic Rate", "0.0"),
          intergenicRate = convertToDouble(input.head, "Intergenic Rate", "0.0"),
          intronicRate = convertToDouble(input.head, "Intronic Rate", "0.0"),
          genesDetected = convertToInt(input.head, "Genes Detected", "0.0")
        ),
        covMetrics = RnaSeqQcStats.CovMetrics(
          meanCV = convertToDouble(input.head, "Mean CV", "0.0"),
          meanPerBaseCov = convertToDouble(input.head, "Mean Per Base Cov.", "0.0")
        ),
        endMetrics = RnaSeqQcStats.EndMetrics(
          end1Sense = convertToInt(input.head, "End 1 Sense", "0"),
          end1MismatchRate = convertToDouble(input.head, "End 1 Sense", "0"),
          end1Antisense = convertToInt(input.head, "End 1 Antisense", "0"),
          end1PctSense = convertToDouble(input.head, "End 1 % Sense", "0"),
          end2Sense = convertToInt(input.head, "End 2 Sense", "0"),
          end2MismatchRate = convertToDouble(input.head, "End 2 Sense", "0"),
          end2Antisense = convertToInt(input.head, "End 2 Antisense", "0"),
          end2PctSense = convertToDouble(input.head, "End 2 % Sense", "0"),
          noCovered5Prime = convertToInt(input.head, "No Covered 5'", "0"),
          fivePrimeNorm = convertToDouble(input.head, "5' Norm", "0.0")
        ),
        gapMetrics = RnaSeqQcStats.GapMetrics(
          numGaps = convertToInt(input.head, "Num. Gaps", "0"),
          gapPct = convertToDouble(input.head, "Gap %", "0.0"),
          cumulGapLength = convertToInt(input.head, "Cumul. Gap Length", "0")
        ),
        readMetrics = RnaSeqQcStats.ReadMetrics(
          chimericPairs = convertToInt(input.head, "Chimeric Pairs", "0"),
          readLength = convertToInt(input.head, "Read Length", "0"),
          unpairedReads = convertToInt(input.head, "Unpaired Reads", "0"),
          fragmentLengthStdDev = convertToDouble(input.head, "Fragment Length Standard Deviation", "0.0"),
          totalPurityFilteredReadsSequenced = convertToInt(input.head, "Total Purity Filtered Reads Sequenced", "0"),
          fragmentLengthMean = convertToInt(input.head, "Fragment Length Mean", "0"),
          baseMismatchRate = convertToDouble(input.head, "Base Mismatch Rate", "0.0"),
          failedVendorQCCheck = convertToInt(input.head, "Failed Vendor QC Check", "0"),
          estimatedLibrarySize = convertToInt(input.head, "Estimated Library Size", "0"),
          expressionProfilingEfficiency = convertToDouble(input.head, "Expression Profiling Efficiency", "0.0"),
          transcriptsDetected = convertToInt(input.head, "Transcripts Detected", "0")
            )
          )
        )
      case "ErccStats" =>
        Right(new ErccStats(
        totalReads = convertToInt(input.head, "TOTAL_READS", "0"),
        totalErccReads = convertToInt(input.head,"ERCC_READS", "0"),
        totalUnalignedReads = convertToInt(input.head,"UNALIGNED_ERCC_READS", "0"),
        fractionGenomeReferenceReads = convertToDouble(input.head, "PCT_NOT_ERCC", "0.0"),
        fractionErccReads = convertToDouble(input.head, "PCT_ERCC", "0.0"),
        fractionUnalignedReads = convertToDouble(input.head, "PCT_UNALIGNED_ERCC", "0.0")
        )
      )
      case "PicardMeanGc" =>
        Right(new PicardReadGcMetrics(
        meanGcContent = convertToDouble(input.head, "MEAN_GC_CONTENT", "0.0")
        )
      )
      case _ => Left("unrecognized mdType input for MapToObject")
    }
  }
}