package org.broadinstitute.parsomatic
import org.broadinstitute.parsomatic.ParserTraits._
import org.broadinstitute.parsomatic.Parsomatic.failureExit
import org.broadinstitute.parsomatic.Parsomatic.filterResultHandler

import scala.io.Source
/**
  * Created by amr on 9/7/2016.
  */
/**
  * Presets for commonly parsed metrics files. Presets include start/end specifications for filtering as well as
  * custom executions for parsing processes.
  */
object Presets {

  class PicardAlignmentMetricPreset(config: Config) extends Rows with RowFilter{
    val inputFile: String = config.inputFile
    /*Note these preset numbers factor in that inputProcessor removes blank lines.
     The header line in the file is line 7, but because there is a blank line above it, start is 6 and end is 9.
      */
    val start = 6
    val end = 9
    config.delimiter = "\t"
    config.mdType = "PicardAlignmentMetrics"
    def run(): Unit = filterResultHandler(filter(start, end), config)
  }

  class PicardMeanQualByCyclePreset(config: Config) extends Keys with KeyFilter {
    val inputFile: String = config.inputFile
    val start = "CYCLE"
    val end = ""
    config.delimiter = "\t"
    config.mdType = "PicardMeanQualByCycle"
    def run(): Unit = {
      filter(start, end) match {
        case Right(filterResult) =>
          filterResultHandler(GetPicardMeanQualMetrics.getMeans(filterResult, config.delimiter), config)
        case Left(unexpectedResult) => failureExit("PicardMeanQualByCyclePreset failed.")
      }
    }
  }

  class PicardMeanGcPreset (config: Config) extends Keys with KeyFilter {
    val inputFile: String = config.inputFile
    val start = "GC"
    val end = ""
    config.delimiter = "\t"
    config.mdType = "PicardMeanGc"
    def run(): Unit = {
      filter(start, end) match {
        case Right(filterResult) =>
          filterResultHandler(GetPicardMeanGcMetrics.getMean(filterResult, config.delimiter), config)
        case Left(unexpectedResult) => failureExit("PicardMeanGcPreset failed.")
      }
    }
  }

  class PicardInsertSizeMetric(config: Config) extends Rows with RowFilter{
    val inputFile: String = config.inputFile
    val start = 6
    val end = 7
    config.delimiter = "\t"
    config.mdType = "PicardInsertSizeMetrics"
    def run(): Unit = filterResultHandler(filter(start, end), config)
  }

  class PicardEstimateLibraryComplexity(config: Config) extends Rows with RowFilter{
    val inputFile: String = config.inputFile
    val start = 6
    val end = 7
    config.delimiter = "\t"
    config.mdType = "PicardEstimateLibraryComplexity"
    def run(): Unit = filterResultHandler(filter(start, end), config)
  }

  class ErccStatsPreset(config: Config) extends Rows with RowFilter {
    val inputFile: String = config.inputFile
    val start = 1
    val end = 0
    config.delimiter = "\t"
    config.mdType = "ErccStats"
    def run(): Unit = {
      filter(start, end) match {
        case Right(filterResult) =>
          filterResultHandler(GetErccStats.getStats(filterResult, config.delimiter), config)
        case Left(unexpectedResult) => failureExit("ErccStatsPreset failed.")
      }
    }
  }

  class RnaSeqQCPreset(config: Config) extends Rows with RowFilter{
    val inputFile: String = config.inputFile
    val start = 1
    val end = 0
    config.delimiter = "\t"
    config.mdType = "RnaSeqQcStats"
    def run(): Unit = filterResultHandler(filter(start, end), config)
  }

  class AggregateRnaSeqQCPreset(config: Config) extends Rows with RowFilter{
    val inputFile: String = config.inputFile
    val start = 1
    val end: Int = Source.fromFile(inputFile).getLines().filterNot(_.isEmpty()).toList
      .indexWhere(_.startsWith(config.sampleId)) + 1
    config.delimiter = "\t"
    config.mdType = "RnaSeqQcStats"
    def run(): Unit = {
      filter(start, end) match {
        case Right(filterResult) =>
          val slicedResult: List[String] = List(filterResult.head, filterResult(end - 1))
          filterResultHandler(Right(slicedResult), config)
        case Left(unexpectedResult) => filterResultHandler(Left(unexpectedResult), config)

      }
    }
  }

  class SampleSheetPreset(config: Config) extends Rows with RowFilter {
    val inputFile: String = config.inputFile
    val start = 0
    val end: Int = Source.fromFile(inputFile).getLines().filterNot(_.isEmpty()).toList
      .indexWhere(_.startsWith(config.sampleId)) + 1
    config.delimiter = "\t"
    config.mdType = "SampleSheet"
    def run(): Unit = {
      filter(start, end) match {
        case Right(filterResult) =>
          val headers: List[String] = List("sampleName\tindexBarcode1\tindexBarcode2\torganism\tdataDir")
          val slice: List[String] = List(filterResult.head)
          val slicedResult: List[String] = headers ++ slice
          filterResultHandler(Right(slicedResult), config)
        case Left(unexpectedResult) => filterResultHandler(Left(unexpectedResult), config)
      }
    }
  }

  class DemultiplexedStatsPreset(config: Config) {
    config.delimiter = "\t"
    config.mdType = "DemultiplexedStats"
    val result: Either[String, List[String]] = new GetPctDemultiplexedStat(config).getStats()
    def run(): Unit = filterResultHandler(result, config)
  }
}