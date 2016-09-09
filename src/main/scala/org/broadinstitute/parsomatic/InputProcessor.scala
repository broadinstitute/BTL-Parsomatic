package org.broadinstitute.parsomatic

import scala.io.Source

/**
  * Created by Amr on 9/6/2016.
  */
class InputProcessor(inputFile: String) {
  val lines = Source.fromFile(inputFile).getLines()

  def filterByRow(start: Int, end: Int): Either[String, Iterator[String]] = {
    println("Parsing by row", start, end)
    end match {
      case 0 => Right(lines drop (start - 1))
      case _ if end > start => Right(lines.slice(start - 1, end))
      case _ => Left("filterByRow processing failed.")
    }
  }
  def filterByKey(start: String, end: String): Either[String, Iterator[String]] = {
    println("Parsing by key", start, end)
    def getKeyRow(lines: Iterator[String], word: String): Int = {
      val list = Source.fromFile(inputFile).getLines()
      val row = list.indexWhere(_.startsWith(word))
      println(word, "found at index", row + 1)
      row + 1
    }
    (start, end) match {
      case ("","") => filterByRow(1,0) //This is the same as filtering by row with default settings.
      case(x, "") => filterByRow(getKeyRow(lines, x), 0) //Filter file from first instance of start keyword to end of file
      case ("", y) => filterByRow(1, getKeyRow(lines, y)) //Filter file from first line to first instance of end keyword.
      case (x, y) => filterByRow(getKeyRow(lines, x), getKeyRow(lines, y)) //Filter file from first instance of start keyword to first instance of end keyword.
      case _ => Left("filterByKey processing failed.")
    }
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