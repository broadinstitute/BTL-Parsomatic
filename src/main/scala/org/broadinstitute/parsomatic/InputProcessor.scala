package org.broadinstitute.parsomatic
import java.io._

/**
  * Created by Amr on 9/6/2016.
  */
//Why does this have to be a case class in order for me to access it inside Parsomatic.scala?
case class InputProcessor(inputFile: File, delimiter: String) {
  val inputStream = new FileInputStream(inputFile)
  val delim: String = delimiter
  val inputData = io.Source.createBufferedSource( inputStream = inputStream, close = () => inputStream.close())

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