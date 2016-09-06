package org.broadinstitute.parsomatic
import java.io._

/**
  * Created by Amr on 9/6/2016.
  */
class InputProcessor(inputFile: File, delimiter: String) {
  val input_file: File = inputFile
  val delim: String = delimiter
  val reader = new FileReader(inputFile)
  def parseByRow(start: Int, end: Int): Unit = {
    println("Parsing by row")
  }
  def parseByKey(start: String, end: String): Unit = {
    println("Parsing by key")
  }
}

//class ParserByRow(h: Int, l: Int) extends Parser() {
//
//
//}
//
//class ParserByKey(s: String, e: String) extends Parser {
//
//}