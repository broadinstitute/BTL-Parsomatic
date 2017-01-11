package org.broadinstitute.parsomatic
import org.broadinstitute.MD.types.metrics._
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

  def convertToLong(row: Map[String, String], key: String, default: String) =
    converter(row, key, default, (_: String).toLong)
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
          totalReads = convertToLong(row, "TOTAL_READS", "-1"),
          pfReads = convertToLong(row, "PF_READS", "-1"),
          pctPfReads = convertToDouble(row, "PCT_PF_READS", "-1.0"),
          pfNoiseReads = convertToLong(row, "PF_NOISE_READS", "-1"),
          pfReadsAligned = convertToLong(row, "PF_READS_ALIGNED", "-1"),
          pctPfReadsAligned = convertToDouble(row, "PCT_PF_READS_ALIGNED", "-1.0"),
          pfAlignedBases = convertToLong(row, "PF_ALIGNED_BASES", "-1"),
          pfHqAlignedReads = convertToLong(row, "PF_ALIGNED_BASES", "-1"),
          pfHqAlignedBases = convertToLong(row, "PF_ALIGNED_BASES", "-1"),
          pfHqAlignedQ20Bases = convertToLong(row, "PF_HQ_ALIGNED_Q20_BASES", "-1"),
          pfHqMedianMismatches = convertToDouble(row, "PF_HQ_MEDIAN_MISMATCHES", "-1.0"),
          pfMismatchRate = convertToDouble(row, "PF_MISMATCH_RATE", "-1.0"),
          pfHqErrorRate = convertToDouble(row, "PF_HQ_ERROR_RATE", "-1.0"),
          pfIndelRate = convertToDouble(row, "PF_INDEL_RATE", "-1.0"),
          meanReadLength = convertToInt(row, "MEAN_READ_LENGTH", "-1"),
          readsAlignedInPairs = convertToLong(row, "READS_ALIGNED_IN_PAIRS", "-1"),
          pctReadsAlignedInPairs = convertToDouble(row, "PCT_READS_ALIGNED_IN_PAIRS", "-1.0"),
          badCycles = convertToInt(row, "BAD_CYCLES", "-1"),
          strandBalance = convertToDouble(row, "STRAND_BALANCE", "-1.0"),
          pctChimeras = convertToDouble(row, "PCT_CHIMERAS", "-1.0"),
          pctAdapter = convertToDouble(row, "PCT_ADAPTER", "-1.0")
          )
        }
        Right(PicardAlignmentSummaryAnalysis(metrics.toList))
      case "PicardInsertSizeMetrics" =>
        Right(PicardInsertSizeMetrics(
          medianInsertSize = convertToDouble(input.head, "MEDIAN_INSERT_SIZE", "-1.0"),
          medianAbsoluteDeviation = convertToDouble(input.head, "MEDIAN_ABSOLUTE_DEVIATION", "-1.0"),
          minInsertSize = convertToInt(input.head, "MIN_INSERT_SIZE", "-1"),
          maxInsertSize = convertToInt(input.head, "MAX_INSERT_SIZE", "-1"),
          meanInsertSize = convertToDouble(input.head, "MEAN_INSERT_SIZE", "-1.0"),
          standardDeviation = convertToDouble(input.head, "STANDARD_DEVIATION", "-1.0"),
          readPairs = convertToLong(input.head, "READ_PAIRS", "-1"),
          pairOrientation = input.head.getOrElse("PAIR_ORIENTATION", "N/A"),
          widthOf10Pct = convertToInt(input.head, "WIDTH_OF_10_PERCENT", "-1"),
          widthOf20Pct = convertToInt(input.head, "WIDTH_OF_20_PERCENT", "-1"),
          widthOf30Pct = convertToInt(input.head, "WIDTH_OF_30_PERCENT", "-1"),
          widthOf40Pct = convertToInt(input.head, "WIDTH_OF_40_PERCENT", "-1"),
          widthOf50Pct = convertToInt(input.head, "WIDTH_OF_50_PERCENT", "-1"),
          widthOf60Pct = convertToInt(input.head, "WIDTH_OF_60_PERCENT", "-1"),
          widthOf70Pct = convertToInt(input.head, "WIDTH_OF_70_PERCENT", "-1"),
          widthOf80Pct = convertToInt(input.head, "WIDTH_OF_80_PERCENT", "-1"),
          widthOf90Pct = convertToInt(input.head, "WIDTH_OF_90_PERCENT", "-1"),
          widthOf99Pct = convertToInt(input.head, "WIDTH_OF_99_PERCENT", "-1")
          )
        )
      case "PicardMeanQualByCycle" =>
        Right(new PicardMeanQualByCycle(
        r1MeanQual = convertToDouble(input.head, "R1_MEAN_QUAL", "-1.0"),
        r2MeanQual = convertToDouble(input.head, "R2_MEAN_QUAL", "-1.0")
          )
        )
        case "RnaSeqQcStats" =>
        Right (new RnaSeqQcStats(
        sample = input.head.getOrElse("Sample", "N/A"),
        note = input.head.getOrElse("Note", "N/A"),
        alignmentMetrics = RnaSeqQcStats.AlignmentMetrics(
          mapped = convertToLong(input.head, "Mapped", "-1"),
          mappedPairs = convertToLong(input.head, "Mapped Pairs", "-1"),
          mappingRate = convertToDouble(input.head, "Mapping Rate", "-1.0"),
          end1MappingRate = convertToDouble(input.head, "End 1 Mapping Rate", "-1.0"),
          end2MappingRate = convertToDouble(input.head, "End 2 Mapping Rate", "-1.0"),
          mappedUnique = convertToLong(input.head, "Mapped Unique", "-1"),
          uniqueRateofMapped = convertToDouble(input.head, "Unique Rate of Mapped", "-1.0"),
          mappedUniqueRateofTotal = convertToDouble(input.head, "Mapped Unique Rate of Total", "-1.0"),
          // Note that RnaSeqQC tool output misspells Alignments, hence we match on the mispelling.
          alternativeAlignments = convertToLong(input.head, "Alternative Aligments", "-1"),
          duplicationRateOfMapped = convertToDouble(input.head, "Duplication Rate of Mapped", "-1")
        ),
        annotationMetrics = RnaSeqQcStats.AnnotationMetrics(
          rRNA = convertToLong(input.head, "rRNA", "-1"),
          rRNArate = convertToDouble(input.head, "rRNA rate", "-1.0"),
          intragenicRate = convertToDouble(input.head, "Intragenic Rate", "-1.0"),
          exonicRate = convertToDouble(input.head, "Exonic Rate", "-1.0"),
          intergenicRate = convertToDouble(input.head, "Intergenic Rate", "-1.0"),
          intronicRate = convertToDouble(input.head, "Intronic Rate", "-1.0"),
          genesDetected = convertToInt(input.head, "Genes Detected", "-1.0")
        ),
        covMetrics = RnaSeqQcStats.CovMetrics(
          meanCV = convertToDouble(input.head, "Mean CV", "-1.0"),
          meanPerBaseCov = convertToDouble(input.head, "Mean Per Base Cov.", "-1.0")
        ),
        endMetrics = RnaSeqQcStats.EndMetrics(
          end1Sense = convertToLong(input.head, "End 1 Sense", "-1"),
          end1MismatchRate = convertToDouble(input.head, "End 1 Mismatch Rate", "-1.0"),
          end1Antisense = convertToLong(input.head, "End 1 Antisense", "-1"),
          end1PctSense = convertToDouble(input.head, "End 1 % Sense", "-1"),
          end2Sense = convertToLong(input.head, "End 2 Sense", "-1"),
          end2MismatchRate = convertToDouble(input.head, "End 2 Mismatch Rate", "-1.0"),
          end2Antisense = convertToLong(input.head, "End 2 Antisense", "-1"),
          end2PctSense = convertToDouble(input.head, "End 2 % Sense", "-1"),
          noCovered5Prime = convertToLong(input.head, "No. Covered 5'", "-1"),
          fivePrimeNorm = convertToDouble(input.head, "5' Norm", "-1.0")
        ),
        gapMetrics = RnaSeqQcStats.GapMetrics(
          numGaps = convertToInt(input.head, "Num. Gaps", "-1"),
          gapPct = convertToDouble(input.head, "Gap %", "-1.0"),
          cumulGapLength = convertToInt(input.head, "Cumul. Gap Length", "-1")
        ),
        readMetrics = RnaSeqQcStats.ReadMetrics(
          chimericPairs = convertToLong(input.head, "Chimeric Pairs", "-1"),
          readLength = convertToInt(input.head, "Read Length", "-1"),
          unpairedReads = convertToLong(input.head, "Unpaired Reads", "-1"),
          fragmentLengthStdDev = convertToDouble(input.head, "Fragment Length StdDev", "-1.0"),
          totalPurityFilteredReadsSequenced = convertToLong(input.head, "Total Purity Filtered Reads Sequenced", "-1"),
          fragmentLengthMean = convertToInt(input.head, "Fragment Length Mean", "-1"),
          baseMismatchRate = convertToDouble(input.head, "Base Mismatch Rate", "-1.0"),
          failedVendorQCCheck = convertToDouble(input.head, "Failed Vendor QC Check", "-1.0"),
          estimatedLibrarySize = convertToLong(input.head, "Estimated Library Size", "-1"),
          expressionProfilingEfficiency = convertToDouble(input.head, "Expression Profiling Efficiency", "-1.0"),
          transcriptsDetected = convertToInt(input.head, "Transcripts Detected", "-1")
            )
          )
        )
      case "ErccStats" =>
        Right(new ErccStats(
        totalReads = convertToLong(input.head, "TOTAL_READS", "-1"),
        totalErccReads = convertToLong(input.head,"ERCC_READS", "-1"),
        totalUnalignedReads = convertToLong(input.head,"UNALIGNED_ERCC_READS", "-1"),
        fractionGenomeReferenceReads = convertToDouble(input.head, "FRC_GENOME_REF", "-1.0"),
        fractionErccReads = convertToDouble(input.head, "FRC_ERCC_READS", "-1.0"),
        fractionUnalignedReads = convertToDouble(input.head, "FRC_UNALIGNED_ERCC", "-1.0")
        )
      )
      case "PicardMeanGc" =>
        Right(new PicardReadGcMetrics(
        meanGcContent = convertToDouble(input.head, "MEAN_GC_CONTENT", "-1.0")
        )
      )
      case "PicardEstimateLibraryComplexity" => Right(new PicardEstimateLibraryComplexity(
        library = input.head.getOrElse("LIBRARY", "N/A"),
        unpariedReadsExamined = convertToLong(input.head, "UNPAIRED_READS_EXAMINED", "-1.0"),
        readPairsExamined = convertToLong(input.head, "READ_PAIRS_EXAMINED", "-1.0"),
        unmappedReads = convertToLong(input.head, "UNMAPPED_READS", "-1.0"),
        unpairedReadDuplicates = convertToLong(input.head, "UNPAIRED_READ_DUPLICATES", "-1.0"),
        readPairDuplicates = convertToLong(input.head, "READ_PAIR_DUPLICATES", "-1.0"),
        readPairOpticalDuplicates = convertToLong(input.head, "READ_PAIR_OPTICAL_DUPLICATES", "-1.0"),
        percentDuplciation = convertToDouble(input.head, "PERCENT_DUPLICATION", "-1.0"),
        estimatedLibrarySize = convertToLong(input.head, "ESTIMATED_LIBRARY_SIZE", "-1.0")
        )
      )
      case "DemultiplexedStats" => Right(new DemultiplexedStats(
        pctOfTotalDemultiplexed = convertToDouble(input.head, "pctOfTotalDemultiplexed", "-1.0")
        )
      )
      case _ => Left("unrecognized mdType input for MapToAnalysisObject")
    }
  }
}