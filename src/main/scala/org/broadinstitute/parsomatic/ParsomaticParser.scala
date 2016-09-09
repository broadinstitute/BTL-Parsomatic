package org.broadinstitute.parsomatic

/**
  * Created by Amr on 9/9/2016.
  */
class ParsomaticParser(iter: Either[String, Iterator[String]], delim: String) {
  def parse(): Unit = {
    for (x <- iter.right) for (y <- x) parseLine(y, delim) //do parsing
  }

  def parseLine(line: String, delim: String): Unit = {
    val lineArray = line.split("\t")
    print(lineArray(0).concat("\n"))
  }
}
