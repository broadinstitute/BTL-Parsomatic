package org.broadinstitute.parsomatic

/**
  * Created by amr on 9/7/2016.
  */
object parserTraits {
//
  trait Rows {
    val start: Int
    val end: Int
  }

  trait Keys {
    val start: String
    val end: String
  }

  trait RowFilter{
    val inputFile: String
    def filter(start: Int, end: Int) = {
      new InputProcessor(inputFile).filterByRow(start, end)
    }
  }

  trait KeyFilter {
    val inputFile: String
    def filter(start: String, end: String) = {
      new InputProcessor(inputFile).filterByKey(start, end)
    }
  }
}
