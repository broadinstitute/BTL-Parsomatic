package org.broadinstitute.parsomatic
import org.broadinstitute.parsomatic.Filters._

/**
  * Created by amr on 9/7/2016.
  */
object FilterPresets {

  class PicardMetricPreset(input: String) extends Rows with RowFilter {
    val inputFile = input
    val start = 7
    val end = 0
    def run() = {
      filter(start, end)
    }
  }

  class PicardHistoMetricPreset(input: String) extends Rows with RowFilter {
    val inputFile = input
    val start = 7
    val end = 8
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
    }  }
}
