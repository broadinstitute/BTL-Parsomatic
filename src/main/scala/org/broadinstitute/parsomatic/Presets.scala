package org.broadinstitute.parsomatic
import org.broadinstitute.parsomatic.parserTraits._
import org.broadinstitute.parsomatic.Parsomatic.filterResultHandler
/**
  * Created by amr on 9/7/2016.
  */
object Presets {

  class PicardAlignmentMetricPreset(config: Config) extends Rows with RowFilter{
    val inputFile = config.inputFile
    val start = 7
    val end = 10
    config.delimiter = "\t"
    def run() = filterResultHandler(filter(start, end), config)
  }

  class PicardHistoMetricPreset(config: Config) extends Rows with RowFilter{
    val inputFile = config.inputFile
    val start = 7
    val end = 8
    config.delimiter = "\t"
    def run() = filterResultHandler(filter(start, end), config)
  }

  class RnaSeqQCPreset(config: Config) extends Rows with RowFilter{
    val inputFile = config.inputFile
    val start = 1
    val end = 0
    config.delimiter = "\t"
    def run() = filterResultHandler(filter(start, end), config)
  }
}