package org.broadinstitute.parsomatic
import org.broadinstitute.parsomatic.parserTraits._

/**
  * Created by amr on 9/7/2016.
  */
object Presets {

  class PicardMetricPreset(config: Config) extends Rows with RowFilter with FileMapper with ObjectMapper{
    val inputFile = config.inputFile
    val start = 7
    val end = 0
    val delim = "\t"
    def run() {
      val fResult = filter(start, end)
      val mResult = mapFile(fResult, delim).parseToMap()
      mapToObject(config.mdType, mResult)
      }
    }

  class PicardHistoMetricPreset(input: String) extends Rows with RowFilter {
    val inputFile = input
    val start = 7
    val end = 8
    val delimiter = "\t"
    def run() = {
      filter(start, end)
    }
  }
  class RnaSeqQCPreset(input: String) extends Rows with RowFilter {
    val inputFile = input
    val start = 1
    val end = 0
    def run() = {
      filter(start, end)
    }
  }
}