package org.broadinstitute.parsomatic
import org.broadinstitute.parsomatic.parserTraits._
import org.broadinstitute.parsomatic.Parsomatic.failureExit
import org.broadinstitute.parsomatic.Parsomatic.filterResultHandler
/**
  * Created by amr on 9/7/2016.
  */
object Presets {

  class PicardAlignmentMetricPreset(config: Config) extends Rows with RowFilter{
    val inputFile = config.inputFile
    /*Note these preset numbers factor in that inputProcessor removes blank lines.
     The header line in the file is line 7, but because there is a blank line above it, start is 6 and end is 9.
      */
    val start = 6
    val end = 9
    config.delimiter = "\t"
    def run() = filterResultHandler(filter(start, end), config)
  }

  class PicardMeanQualByCyclePreset(config: Config) extends Keys with KeyFilter {
    val inputFile = config.inputFile
    val start = "CYCLE"
    val end = ""
    config.delimiter = "\t"
    def run () = {
      filter(start, end) match {
        case Right(filterResult) => val result = new GetPicardMeanQualMetrics(filterResult, config.delimiter)
          filterResultHandler(result.getMeans,config)
        case Left(unexpectedResult) => failureExit("PicardMeanQualByCyclePreset failed.")
      }
    }
  }

  class PicardMeanGcPreset (config: Config) extends Keys with KeyFilter {
    val inputFile = config.inputFile
    val start = "GC"
    val end = ""
    config.delimiter = "\t"
    def run () = {
      filter(start, end) match {
        case Right(filterResult) => val result = new GetPicardMeanGcMetrics(filterResult, config.delimiter)
          filterResultHandler(result.getMean, config)
        case Left(unexpectedResult) => failureExit("PicardMeanGcPreset failed.")
      }
    }
  }

  class PicardHistoMetricPreset(config: Config) extends Rows with RowFilter{
    val inputFile = config.inputFile
    val start = 6
    val end = 7
    config.delimiter = "\t"
    def run() = filterResultHandler(filter(start, end), config)
  }

  class ErccStatsPreset(config: Config) extends Rows with RowFilter {
    val inputFile = config.inputFile
    val start = 1
    val end = 0
    config.delimiter = "\t"
    def run () = {
      filter(start, end) match {
        case Right(filterResult) => val result = new GetErccStats(filterResult)
          filterResultHandler(result.getStats, config)
        case Left(unexpectedResult) => failureExit("ErccStatsPreset failed.")
      }
    }
  }

  class RnaSeqQCPreset(config: Config) extends Rows with RowFilter{
    val inputFile = config.inputFile
    val start = 1
    val end = 0
    config.delimiter = "\t"
    def run() = filterResultHandler(filter(start, end), config)
  }
}